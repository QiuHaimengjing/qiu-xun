package com.qiu.qiuxun.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiu.qiuxun.common.BaseResponse;
import com.qiu.qiuxun.common.ErrorCode;
import com.qiu.qiuxun.common.Result;
import com.qiu.qiuxun.exception.BusinessException;
import com.qiu.qiuxun.model.request.user.UserLoginRequest;
import com.qiu.qiuxun.model.request.user.UserRegisterRequest;
import com.qiu.qiuxun.model.request.user.UserUpdateRequest;
import com.qiu.qiuxun.model.po.User;
import com.qiu.qiuxun.mapper.UserMapper;
import com.qiu.qiuxun.model.request.PageRequest;
import com.qiu.qiuxun.model.vo.UserListVO;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiu.qiuxun.utils.AlgorithmUtils;
import com.qiu.qiuxun.utils.OssClient;
import com.qiu.qiuxun.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qiu.qiuxun.constant.RedisConstant.*;
import static com.qiu.qiuxun.constant.UserConstant.*;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @description: 用户注册
     * @params: [userRegisterRequest]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 22:56
     */
    @Override
    public Boolean userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String verifyPassword = userRegisterRequest.getVerifyPassword();

        // 1.账号密码校验
        Boolean checkRules = sameCheckRules(userRegisterRequest);
        if (!checkRules) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.密码和校验密码不相同
        if (!StrUtil.equals(userPassword, verifyPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "密码和校验密码不相同");
        }
        // 3.账户不能重复
        Long count = this.lambdaQuery().eq(User::getUserAccount, userAccount).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户已注册");
        }
        // 4.对密码进行加密
        SHA256 encryption = SHA256.encryptionPassword(userPassword);
        userRegisterRequest.setUserPassword(encryption.getSha256Password());
        // 5.插入用户数据
        User user = BeanUtil.copyProperties(userRegisterRequest, User.class);
        user.setSalt(encryption.getSalt());
        this.save(user);
        return true;
    }

    /**
     * @description: 用户登录
     * @params: [userLoginRequest, request]
     * @return: com.qiu.qiuxun.model.vo.UserVO
     * @author: qiu
     * @dateTime: 2024/3/13 23:02
     */
    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 1.校验
        Boolean checkRules = sameCheckRules(userLoginRequest);
        if (!checkRules) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.查询该用户
        User user = lambdaQuery().eq(User::getUserAccount, userAccount).one();
        if (BeanUtil.isEmpty(user)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码错误");
        }
        // 3.校验密码
        String password = user.getUserPassword();
        String salt = user.getSalt();
        String decryptPassword = SHA256.decryptPassword(salt, userPassword);
        if (!StrUtil.equals(decryptPassword, password)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码错误");
        }
        // 4.脱敏并记录用户登录状态
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * @description: 昵称查询用户
     * @params: [username]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:07
     */
    @Override
    public List<UserVO> searchUsers(String username) {
        List<User> users = this.lambdaQuery().like(username != null, User::getUsername, username).list();
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(users, UserVO.class);
    }

    /**
     * @description: 根据id更新用户信息
     * @params: [user]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:10
     */
    @Override
    public Boolean updateUserById(User user) {
        // 1.判断用户是否为空
        if (BeanUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.更新用户信息
        boolean isUpdate = this.updateById(user);
        // 3.判断是否更新成功
        if (!isUpdate) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "更新失败，用户不存在");
        }
        // 4.更新成功返回true
        return true;
    }

    /**
     * @description: 通过标签列表查询用户列表（SQL查询）
     * @params: [tags]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:12
     */
    @Override
    public List<UserVO> searchUsersByTagsSQL(List<String> tags) {
        // 1.校验标签列表
        if (CollUtil.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.构造查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        for (String tag : tags) {
            wrapper.like(User::getTags, tag);
        }
        // 3.查询数据库
        List<User> users = userMapper.selectList(wrapper);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        // 4.脱敏
        return BeanUtil.copyToList(users, UserVO.class);
    }

    /**
     * @description: 通过标签列表查询用户列表（内存查询）
     * @params: [tags]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:14
     */
    @Override
    public List<UserVO> searchUsersByTagsMemory(List<String> tags) {
        // 1.校验标签列表
        if (CollUtil.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.查询数据库的所有用户
        List<User> users = this.query().list();
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        // 3.在内存中筛选具有所有标签的用户
        List<User> userList = users.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tag : tags) {
                if (!tempTagNameSet.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return BeanUtil.copyToList(userList, UserVO.class);
    }

    /**
     * @description: 用户鉴权
     * @params: [request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2023/12/9 19:13
     */
    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO userVO = BeanUtil.copyProperties(attribute, UserVO.class);
        Integer userRole = userVO.getUserRole();
        return NumberUtil.equals(userRole, ADMIN);
    }

    /**
     * @description: 获取当前登录用户
     * @params: [request]
     * @return: com.qiu.qiuxun.model.vo.UserVO
     * @author: qiu
     * @dateTime: 2024/2/23 23:03
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (BeanUtil.isEmpty(attribute)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return BeanUtil.copyProperties(attribute, UserVO.class);
    }

    /**
     * @description: 用户更新个人信息
     * @params: [userUpdateRequest, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:16
     */
    @Override
    public Boolean updateUserByAuth(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 1.获取当前登录用户
        UserVO loginUser = getLoginUser(request);
        if (BeanUtil.isEmpty(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 2.检查空数据
        if (BeanUtil.isEmpty(userUpdateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 3.更新数据库
        User user = BeanUtil.copyProperties(userUpdateRequest, User.class);
        boolean update = updateById(user);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return true;
    }

    /**
     * @description: 获取推荐用户
     * @params: [pageNo, pageSize, request]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:19
     */
    @Override
    public BaseResponse<UserListVO> recommendUsers(PageRequest pageRequest, HttpServletRequest request) {
        // 1.校验分页参数
        Integer pageNO = pageRequest.getPageNO();
        Integer pageSize = pageRequest.getPageSize();
        if (pageNO == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        // 2.获取当前登录用户
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserListVO userListVO = new UserListVO();
        // 如果用户未登录
        if (BeanUtil.isEmpty(attribute)) {
            // 未登录的用户key
            String redisKey = USER_RECOMMEND_KEY + DEFAULT_RECOMMEND_USERID;
            String recommend = opsForValue.get(redisKey);
            if (StrUtil.isNotBlank(recommend)) {
                List<UserVO> list = JSONUtil.toList(recommend, UserVO.class);
                int total = list.size();
                userListVO.setUserList(list);
                userListVO.setTotal((long) total);
                return Result.success(userListVO);
            }
        } else {
            // 用户已登录
            UserVO userVO = BeanUtil.copyProperties(attribute, UserVO.class);
            String redisKey = USER_RECOMMEND_KEY + userVO.getId();
            String recommend = opsForValue.get(redisKey);
            if (StrUtil.isNotBlank(recommend)) {
                try {
                    stringRedisTemplate.delete(redisKey);
                } catch (Exception e) {
                    log.error("delete recommend users fail, id{}", userVO.getId());
                }
                List<UserVO> list = JSONUtil.toList(recommend, UserVO.class);
                int total = list.size();
                userListVO.setUserList(list);
                userListVO.setTotal((long) total);
                return Result.success(userListVO);
            }
        }
        // 无缓存，查数据库
        Page<User> page = Page.of(pageNO, pageSize);
        Page<User> userPage = this.page(page);
        List<User> records = userPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            userListVO.setUserList(Collections.emptyList());
            userListVO.setTotal(0L);
            return Result.success(userListVO);
        }
        List<UserVO> userVOList = BeanUtil.copyToList(records, UserVO.class);
        userListVO.setUserList(userVOList);
        userListVO.setTotal(userPage.getTotal());
        return Result.success(userListVO);
    }

    /**
     * @description: 匹配用户
     * @params: [num, request]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/3/13 23:20
     */
    @Override
    public UserListVO matchUsers(Long num, HttpServletRequest request) {
        UserListVO userListVO = new UserListVO();
        // 1.校验参数
        if (BeanUtil.isEmpty(num) || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.限定匹配人数
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "匹配人数不符合要求");
        }
        // 3.判断用户是否登录
        UserVO user = getLoginUser(request);
        Long userId = user.getId();
        if (BeanUtil.isEmpty(user) || userId <= 0) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 4.取出所有用户，只查id和tags，并且tags不为空
        List<User> userList = lambdaQuery().isNotNull(User::getTags).select(User::getId, User::getTags).list();
        if (CollUtil.isEmpty(userList)) {
            userListVO.setUserList(Collections.emptyList());
            userListVO.setTotal(0L);
            return userListVO;
        }
        // 5.JSON字符串转为标签列表
        String tags = user.getTags();
        List<String> myTagList = JSONUtil.toList(tags, String.class);
        // 存储列表, 用户和编辑距离分数
        List<Pair<User, Integer>> pairList = new ArrayList<>();
        // 6.计算每个用户的编辑距离
        for (User number : userList) {
            String numberTags = number.getTags();
            // 6.1 过滤掉标签为空的用户和自己
            if (StrUtil.isBlank(numberTags) || number.getId().equals(userId)) {
                continue;
            }
            // 6.2 JSON字符串转列表
            List<String> numberTagList = JSONUtil.toList(numberTags, String.class);
            // 6.3 编辑距离
            int score = AlgorithmUtils.minDistance(myTagList, numberTagList);
            // 6.4 存放该用户
            pairList.add(new Pair<>(number, score));
        }
        // 7.根据分数升序排序，值越小越匹配，得到排序好的列表
        List<Pair<User, Integer>> sortedList = pairList.stream().sorted(Comparator.comparingInt(Pair::getValue))
                .limit(num).collect(Collectors.toList());
        // 8.得到id列表
        List<Long> idList = sortedList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        if (CollUtil.isEmpty(idList)) {
            userListVO.setUserList(Collections.emptyList());
            userListVO.setTotal(0L);
            return userListVO;
        }
        // 9.根据id列表查询数据库
        List<User> matchUserList = listByIds(idList);
        if (CollUtil.isEmpty(matchUserList)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 10.因为查询出来数据是乱的，要按照原先id列表的顺序排列
        Map<Long, List<UserVO>> map = matchUserList.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class))
                .collect(Collectors.groupingBy(UserVO::getId));
        List<UserVO> finalUserList = new ArrayList<>();
        for (Long id : idList) {
            finalUserList.add(map.get(id).get(0));
        }
        userListVO.setUserList(finalUserList);
        userListVO.setTotal((long) finalUserList.size());
        return userListVO;
    }

    /**
     * @description: 获取所有用户
     * @params: [pageRequest]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/4/15 13:49
     */
    @Override
    public BaseResponse<UserListVO> getAllUser(PageRequest pageRequest) {
        // 1.校验分页参数
        Integer pageNO = pageRequest.getPageNO();
        Integer pageSize = pageRequest.getPageSize();
        if (pageNO == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> page = Page.of(pageNO, pageSize);
        Page<User> userPage = this.page(page);
        long total = userPage.getTotal();
        List<User> records = userPage.getRecords();
        UserListVO userListVO = new UserListVO();
        if (CollUtil.isEmpty(records)) {
            userListVO.setUserList(Collections.emptyList());
            userListVO.setTotal(total);
            return Result.success(userListVO);
        }
        List<UserVO> userVOList = BeanUtil.copyToList(records, UserVO.class);
        userListVO.setUserList(userVOList);
        userListVO.setTotal(total);
        return Result.success(userListVO);
    }

    /**
     * @description: 用户上传头像
     * @params: [file, request]
     * @return: java.lang.String
     * @author: qiu
     * @dateTime: 2024/3/25 22:39
     */
    @Override
    public String uploadAvatar(MultipartFile file, HttpServletRequest request) {
        if (file.getSize() > 1048576) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像大小限制1MB");
        }
        // 1.校验参数
        UserVO userVO = getLoginUser(request);
        // Redis查询该用户上传次数
        String redisKey = USER_UPLOAD_LIMIT_KEY + userVO.getId();
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        String loadNumStr = opsForValue.get(redisKey);
        int uploadNum = 0;
        // 如果不为空，并且次数达到5次禁止上传
        if (StrUtil.isNotBlank(loadNumStr)) {
            uploadNum = Integer.parseInt(loadNumStr);
            if (uploadNum >= 5) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "已达到今日上传次数限制");
            }
        }
        String avatarUrl;
        try {
            avatarUrl = OssClient.uploadAvatar(file, userVO.getId());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传头像失败");
        }
        // 2.更新数据库
        userVO.setAvatarUrl(avatarUrl);
        User user = BeanUtil.copyProperties(userVO, User.class);
        boolean result = updateById(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传头像失败");
        }
        // 如果Redis键为空先创建
        if (StrUtil.isBlank(loadNumStr)) {
            try {
                opsForValue.set(redisKey, "1", USER_UPLOAD_LIMIT_TTL, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        } else {
            // 否则次数+1
            uploadNum = uploadNum + 1;
            String nowUploadNum = String.valueOf(uploadNum);
            Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (expire != null && expire > 0) {
                Boolean setIfPresent = opsForValue.setIfPresent(redisKey, nowUploadNum, expire, TimeUnit.SECONDS);
                if (Boolean.FALSE.equals(setIfPresent)) {
                    log.error("redis update key error");
                }
            }
        }
        return avatarUrl;
    }

    /**
     * @description: 获取缓存预热用户
     * @params: []
     * @return: java.util.List<java.lang.Long>
     * @author: qiu
     * @dateTime: 2024/4/15 13:00
     */
    @Override
    public List<Long> getJobUsers() {
        // 缓存20名重点用户
        Page<User> page = Page.of(1, 20);
        Page<User> userPage = this.page(page);
        List<User> records = userPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(User::getId).collect(Collectors.toList());
    }

    /**
     * @description: 账号密码校验
     * @params: [object]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 22:54
     */
    private Boolean sameCheckRules(Object object) {
        User user = BeanUtil.copyProperties(object, User.class);
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();

        // 1.非空校验
        if (BeanUtil.isEmpty(user) || StrUtil.hasEmpty(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码为空");
        }
        // 2.账户不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不小于4位");
        }
        // 3.密码不小于8位
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不小于6位");
        }
        // 4.账户不包含特殊字符
        if (!Validator.isGeneral(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不包含特殊字符");
        }
        return true;
    }
}
