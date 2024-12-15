package com.qiu.qiuxun.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiu.qiuxun.common.ErrorCode;
import com.qiu.qiuxun.exception.BusinessException;
import com.qiu.qiuxun.model.request.team.TeamAddRequest;
import com.qiu.qiuxun.model.request.team.TeamQueryRequest;
import com.qiu.qiuxun.model.request.team.TeamUpdateRequest;
import com.qiu.qiuxun.model.enums.TeamStatusEnum;
import com.qiu.qiuxun.model.po.Team;
import com.qiu.qiuxun.mapper.TeamMapper;
import com.qiu.qiuxun.model.po.User;
import com.qiu.qiuxun.model.po.UserTeam;
import com.qiu.qiuxun.model.request.team.TeamJoinRequest;
import com.qiu.qiuxun.model.vo.TeamVO;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiu.qiuxun.service.IUserService;
import com.qiu.qiuxun.service.IUserTeamService;
import com.qiu.qiuxun.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qiu.qiuxun.constant.RedisConstant.USER_JOIN_TEAM_LOCK;
import static com.qiu.qiuxun.constant.UserConstant.USER_LOGIN_STATE;

/**
 * <p>
 * 队伍 服务实现类
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

    @Resource
    private IUserService userService;

    @Resource
    private IUserTeamService userTeamService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * @description: 创建队伍
     * @params: [teamAddRequest, request]
     * @return: java.lang.Long
     * @author: qiu
     * @dateTime: 2024/3/13 23:25
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        UserVO user = parameterValidation(teamAddRequest, request);
        // 2. 获取当前登录用户
        Long userId = user.getId();
        // 3. 校验信息
        // 4. 队伍人数 >1 且 <= 20
        int maxNum = Optional.ofNullable(teamAddRequest.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        // 5. 队伍名称 <= 20
        String name = teamAddRequest.getName();
        if (StrUtil.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        // 6. 描述 <= 512
        String description = teamAddRequest.getDescription();
        if (StrUtil.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不符合要求");
        }
        // 7. 状态，不传默认为0-公开
        Integer status = Optional.ofNullable(teamAddRequest.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态设置不符合要求");
        }
        // 8. 如果队伍是加密状态，一定要有密码，且密码 <= 32
        String password = teamAddRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByValue)) {
            if (StrUtil.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不符合要求");
            }
            // 密码加密
            password = SHA256.encryptionPasswordForTeam(password);
            teamAddRequest.setPassword(password);
        }
        // 9. 超时时间 > 当前时间
        LocalDateTime expireTime = teamAddRequest.getExpireTime();
        if (LocalDateTime.now().isAfter(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间必须大于当前时间");
        }
        // 10. 校验用户最多创建5个队伍
        // TODO 有bug，可能同时创建 100 个队伍
        Long count = lambdaQuery().eq(Team::getLeader, userId).count();
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        // 11. 插入队伍信息到队伍表
        Team team = BeanUtil.copyProperties(teamAddRequest, Team.class);
        team.setLeader(userId);
        team.setCurrentNum(1);
        boolean result = save(team);
        Long teamId = team.getId();
        if (!result || BeanUtil.isEmpty(teamId)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        // 12. 插入用户 => 用户队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setIsLeader(1);
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    /**
     * @description: 删除队伍
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/2 12:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(Long id, HttpServletRequest request) {
        // 1.校验请求参数，获取当前登录用户
        UserVO user = parameterValidation(id, request);
        Long userId = user.getId();
        // 2.非创建人或者管理员不能删除队伍
        Boolean admin = userService.isAdmin(request);
        Long count = lambdaQuery().eq(Team::getId, id).eq(Team::getLeader, userId).count();
        if (count <= 0 && !admin) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限删除队伍");
        }
        // 3.查库校验，删除队伍表
        boolean result = lambdaUpdate().eq(Team::getId, id).eq(Team::getLeader, userId).remove();
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        // 4.删除用户队伍表所有成员记录
        boolean userTeamResult = userTeamService.lambdaUpdate().eq(UserTeam::getTeamId, id).remove();
        if (!userTeamResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return true;
    }

    /**
     * @description: 修改队伍
     * @params: [teamUpdateRequest, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:26
     */
    @Override
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 1.校验请求参数，获取当前登录用户
        UserVO user = parameterValidation(teamUpdateRequest, request);
        Boolean admin = userService.isAdmin(request);
        // 2.查询队伍是否存在
        Long teamId = teamUpdateRequest.getId();
        Team oldTeam = getById(teamId);
        if (BeanUtil.isEmpty(oldTeam)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 3.查库校验，修改，只有队伍创建者或管理员可以修改
        Long userId = user.getId();
        if (!Objects.equals(oldTeam.getLeader(), userId) && !admin) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限修改队伍");
        }
        // 4.如果队伍状态改为加密，必须要设置密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            String password = teamUpdateRequest.getPassword();
            if (StrUtil.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须设置密码");
            }
            // 加密密码
            String finalPassword = SHA256.encryptionPasswordForTeam(password);
            teamUpdateRequest.setPassword(finalPassword);
        }
        // 5.最大人数不能小于当前队伍人数
        if (teamUpdateRequest.getMaxNum() < oldTeam.getCurrentNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最大人数不能小于当前队伍人数");
        }
        Team team = BeanUtil.copyProperties(teamUpdateRequest, Team.class);
        boolean result = updateById(team);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return true;
    }

    /**
     * @description: 查询队伍列表
     * @params: [teamQueryRequest, request]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.TeamVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:27
     */
    @Override
    public List<TeamVO> teamList(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        // 1.校验参数
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO userVO = null;
        Long userId = null;
        if (BeanUtil.isNotEmpty(attribute)) {
            userVO = BeanUtil.copyProperties(attribute, UserVO.class);
            userId = userVO.getId();
        }
        // 判断当前用户是否为管理员
        Boolean isAdmin = false;
        if (BeanUtil.isNotEmpty(userVO)) {
            isAdmin = userService.isAdmin(request);
        }
        // 2.组合查询条件
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        // 2.1 队伍id
        Long id = teamQueryRequest.getId();
        if (BeanUtil.isNotEmpty(id) && id > 0) {
            queryWrapper.eq(Team::getId, id);
        }
        // 2.2 搜索关键词，包含队伍名和描述
        String keyWords = teamQueryRequest.getKeyWords();
        if (StrUtil.isNotBlank(keyWords)) {
            queryWrapper.and(qw -> qw.like(Team::getName, keyWords).or().like(Team::getDescription, keyWords));
        }
        // 2.3 队伍名称
        String name = teamQueryRequest.getName();
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like(Team::getName, name);
        }
        // 2.4 队伍描述
        String description = teamQueryRequest.getDescription();
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.like(Team::getDescription, description);
        }
        // 2.5 最大人数
        Integer maxNum = teamQueryRequest.getMaxNum();
        if (BeanUtil.isNotEmpty(maxNum) && maxNum > 0) {
            queryWrapper.eq(Team::getMaxNum, maxNum);
        }
        // 2.6 创建人
        Long leader = teamQueryRequest.getLeader();
        if (BeanUtil.isNotEmpty(leader) && leader > 0) {
            queryWrapper.eq(Team::getLeader, leader);
        }
        // 2.7 队伍状态，默认访问公开队伍
        Integer status = teamQueryRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        // 2.8 非管理员不能访问私密房间
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限查询私密房间");
        }
        queryWrapper.eq(Team::getStatus, statusEnum.getValue());
        // 2.9 不展示已过期的队伍
        // 过期时间大于当前时间，或者未设置过期时间
        queryWrapper.and(qw -> qw.gt(Team::getExpireTime, new Date()).or().isNull(Team::getExpireTime));
        // 3. 对队伍进行分页
        // 3.1 校验分页参数
        Integer pageNO = teamQueryRequest.getPageNO();
        Integer pageSize = teamQueryRequest.getPageSize();
        if (BeanUtil.isEmpty(pageNO) || BeanUtil.isEmpty(pageSize)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法的分页参数");
        }
        Page<Team> page = Page.of(pageNO, pageSize);
        Page<Team> teamsPage = this.page(page, queryWrapper);
        // 4. 获取分页记录
        List<Team> records = teamsPage.getRecords();
        // 5.如果为空直接返回
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        // 6. 队伍信息脱敏
        List<TeamVO> teamVOList = BeanUtil.copyToList(records, TeamVO.class);
        // 7. 封装创建人和成员信息
        List<TeamVO> teamVOS = teamVOHandler(teamVOList);
        if (userId != null) {
            Long finalUserId = userId;
            teamVOS.forEach(item -> {
                boolean contains = item.getMembers().stream().map(UserVO::getId).collect(Collectors.toList()).contains(finalUserId);
                if (contains) {
                    item.setJoined(true);
                }
            });
        }
        return teamVOS;
    }

    /**
     * @description: 用户加入队伍
     * @params: [teamJoinRequest, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:28
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        // 1.校验参数
        UserVO userVO = parameterValidation(teamJoinRequest, request);
        Long userId = userVO.getId();
        // 用户加入队伍分布式锁
        RLock lock = redissonClient.getLock(USER_JOIN_TEAM_LOCK + userId);
        try {
            boolean isLock = lock.tryLock(0, -1, TimeUnit.MILLISECONDS);
            if (!isLock) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "不允许一次性发起多次请求");
            }
            Thread.sleep(10000);
            // 2.用户最多加入5个队伍
            Long hasJoinNum = userTeamService.lambdaQuery().eq(UserTeam::getUserId, userId).count();
            if (hasJoinNum >= 5) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
            }
            // 3.不能重复加入已加入的队伍
            Long teamId = teamJoinRequest.getId();
            Long hasUserJoinTeam = userTeamService.lambdaQuery()
                    .eq(UserTeam::getUserId, userId)
                    .eq(UserTeam::getTeamId, teamId).count();
            if (hasUserJoinTeam > 0) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "用户已加入该队伍");
            }
            // 4.队伍必须存在
            if (BeanUtil.isEmpty(teamId) || teamId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id错误");
            }
            Team team = getById(teamId);
            if (BeanUtil.isEmpty(team)) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
            }
            // 5.队伍是否已过期
            LocalDateTime expireTime = team.getExpireTime();
            if (BeanUtil.isNotEmpty(expireTime) && expireTime.isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
            }
            // 6.禁止加入私有队伍
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
            if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有队伍");
            }
            // 7.如果是加密队伍，校验密码
            String password = SHA256.decryptPasswordForTeam(teamJoinRequest.getPassword());
            if (TeamStatusEnum.SECRET.equals(statusEnum)) {
                if (StrUtil.isBlank(password) || !password.equals(team.getPassword())) {
                    throw new BusinessException(ErrorCode.NULL_ERROR, "密码错误");
                }
            }
            // 8.队伍是否已满
            Long teamHasJoinNum = userTeamService.lambdaQuery().eq(UserTeam::getTeamId, teamId).count();
            if (teamHasJoinNum >= team.getMaxNum()) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
            }
            // 9.更新数据库，修改用户队伍表信息
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(teamId);
            boolean result = userTeamService.save(userTeam);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入失败");
            }
            // 10.更新队伍表当前人数
            boolean teamResult = lambdaUpdate().eq(Team::getId, teamId).setSql("`current_num` = `current_num`+ 1").update();
            if (!teamResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入失败");
            }
            return true;
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "userJoinTeamLockFail");
        } finally {
            // 只能释放自己的锁
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * @description: 用户退出队伍
     * @params: [id, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:30
     * TODO 队长顺位，下一任队长队伍超过5个怎么办
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quitTeam(Long id, HttpServletRequest request) {
        // 1.校验请求参数
        UserVO userVO = parameterValidation(id, request);
        // 2.查验队伍是否存在
        Team team = lambdaQuery().eq(Team::getId, id).one();
        if (BeanUtil.isEmpty(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 3.校验我是否已加入队伍
        Long userId = userVO.getId();
        boolean userExist = false;
        List<UserTeam> userTeamList = userTeamService.lambdaQuery().eq(UserTeam::getTeamId, id).list();
        for (UserTeam userTeam : userTeamList) {
            Long teamUserId = userTeam.getUserId();
            if (teamUserId.equals(userId)) {
                userExist = true;
                break;
            }
        }
        if (!userExist) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未加入队伍");
        }
        // 4.如果队伍只剩一人，队伍解散，否则队长顺位
        if (userTeamList.size() == 1) {
            // 4.1 删除用户队伍表信息
            boolean userTeamResult = userTeamService.lambdaUpdate().eq(UserTeam::getTeamId, id).remove();
            if (!userTeamResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户队伍关系失败");
            }
            // 4.2 删除队伍表信息
            boolean teamResult = lambdaUpdate().eq(Team::getId, id).remove();
            if (!teamResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍解散失败");
            }
        } else {
            // 5. 如果还有其他人
            Long leader = team.getLeader();
            // 6 是队长退出队伍，权限转移给第二早加入时的用户
            if (leader.equals(userId)) {
                UserTeam nextLeader = userTeamService.lambdaQuery().eq(UserTeam::getTeamId, id).ne(UserTeam::getUserId, userId)
                        .orderByAsc(UserTeam::getId).one();
                if (BeanUtil.isEmpty(nextLeader)) {
                    throw new BusinessException(ErrorCode.NULL_ERROR, "没有下一任队长");
                }
                Long nextLeaderId = nextLeader.getUserId();
                // 6.1 更新队伍表数据
                Team updateTeam = new Team();
                updateTeam.setLeader(nextLeaderId);
                boolean result = lambdaUpdate().eq(Team::getId, id).setSql("`current_num` = `current_num` - 1").update(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
                // 6.2 更新新队长的用户队伍关系
                UserTeam userTeam = new UserTeam();
                userTeam.setIsLeader(1);
                boolean update = userTeamService.lambdaUpdate().eq(UserTeam::getTeamId, id)
                        .eq(UserTeam::getUserId, nextLeaderId).update(userTeam);
                if (!update) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户队伍关系失败");
                }
            } else {
                // 7. 是非队长成员退出
                // 7.1 更新队伍表数据
                boolean result = lambdaUpdate().eq(Team::getId, id).setSql("`current_num` = `current_num` - 1").update();
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
            // 8. 删除用户队伍表关系
            boolean remove = userTeamService.lambdaUpdate().eq(UserTeam::getTeamId, id).eq(UserTeam::getUserId, userId).remove();
            if (!remove) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户队伍关系失败");
            }
        }
        return true;
    }

    /**
     * @description: 我加入的队伍
     * @params: [id, request]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.TeamVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:31
     */
    @Override
    public List<TeamVO> myTeams(Long id, HttpServletRequest request) {
        // 1.请求参数校验
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.获取当前登录用户
        UserVO user = userService.getLoginUser(request);
        Long userId = user.getId();
        if (BeanUtil.isEmpty(user) || BeanUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 3.获取当前用户加入的队伍
        List<UserTeam> userTeamList = userTeamService.lambdaQuery().eq(UserTeam::getUserId, userId).list();
        if (CollUtil.isEmpty(userTeamList)) {
            return Collections.emptyList();
        }
        List<Long> teamIds = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        if (BeanUtil.isNotEmpty(id)) {
            if (!teamIds.contains(id)) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "你不是该队伍成员");
            }
            teamIds = teamIds.stream().filter(item -> Objects.equals(item, id)).collect(Collectors.toList());
        }
        List<Team> teams = listByIds(teamIds);
        if (CollUtil.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 4.队伍信息脱敏
        List<TeamVO> teamVOList = BeanUtil.copyToList(teams, TeamVO.class);
        teamVOList.forEach(item -> item.setJoined(true));
        // 5.封装创建人和成员信息
        return teamVOHandler(teamVOList);
    }

    /**
     * @description: 请求参数和登录校验，返回当前登录用户
     * @params: [object, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/2 12:24
     */
    private <T> UserVO parameterValidation(T object, HttpServletRequest request) {
        // 1.校验请求参数
        if (BeanUtil.isEmpty(object) || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.获取当前登录用户
        UserVO user = userService.getLoginUser(request);
        Long userId = user.getId();
        if (BeanUtil.isEmpty(user) || BeanUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    /**
     * @description: 封装队伍创建人和成员信息
     * @params: [teamVOList]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.TeamVO>
     * @author: qiu
     * @dateTime: 2024/3/9 11:43
     */
    private List<TeamVO> teamVOHandler(List<TeamVO> teamVOList) {
        // 1. 遍历每一个队伍
        for (TeamVO teamVO : teamVOList) {
            // 2. 查询创建人信息
            Long createUserId = teamVO.getLeader();
            User createUser = userService.getById(createUserId);
            if (BeanUtil.isEmpty(createUser)) {
                continue;
            }
            // 3. 创建人信息脱敏并赋值
            UserVO createUserVO = BeanUtil.copyProperties(createUser, UserVO.class);
            teamVO.setCreateUser(createUserVO);
            // 4. 根据该队伍id查询所有成员信息
            List<UserVO> userVOS = getBaseMapper().queryMembers(teamVO.getId());
            if (CollUtil.isEmpty(userVOS)) {
                continue;
            }
            teamVO.setMembers(userVOS);
        }
        return teamVOList;
    }
}
