package com.qiu.qiuxun.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.qiu.qiuxun.common.BaseResponse;
import com.qiu.qiuxun.common.ErrorCode;
import com.qiu.qiuxun.common.Result;
import com.qiu.qiuxun.exception.BusinessException;
import com.qiu.qiuxun.model.request.user.UserLoginRequest;
import com.qiu.qiuxun.model.request.user.UserRegisterRequest;
import com.qiu.qiuxun.model.request.user.UserUpdateRequest;
import com.qiu.qiuxun.model.po.User;
import com.qiu.qiuxun.model.request.PageRequest;
import com.qiu.qiuxun.model.vo.UserListVO;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.qiu.qiuxun.constant.UserConstant.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户中心")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * @description: 用户注册
     * @params: [userRegisterRequest]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:33
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public BaseResponse<Boolean> userRegister(@ApiParam(value = "用户注册请求体对象", required = true)
                                                  @RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String verifyPassword = userRegisterRequest.getVerifyPassword();
        // 1.校验
        if (BeanUtil.isEmpty(userRegisterRequest) || StrUtil.hasEmpty(userAccount, userPassword, verifyPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.userRegister(userRegisterRequest);
        return Result.success(result);
    }

    /**
     * @description: 用户登录
     * @params: [userLoginRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2023/12/16 19:46
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@ApiParam(value = "用户登录请求体对象", required = true)
                                              @RequestBody UserLoginRequest userLoginRequest,
                                          HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1.校验
        if (BeanUtil.isEmpty(userLoginRequest) || StrUtil.hasEmpty(userAccount, userPassword)) {
            return Result.error(ErrorCode.NULL_ERROR);
        }
        UserVO userVO = userService.userLogin(userLoginRequest, request);
        return Result.success(userVO);
    }

    /**
     * @description: 获取用户登录态
     * @params: [request]
     * @return: com.qiu.qiuxun.common.BaseResponse<com.qiu.qiuxun.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2023/12/16 19:50
     */
    @ApiOperation("获取用户登录态")
    @GetMapping("/current")
    public BaseResponse<UserVO> userCurrent(HttpServletRequest request) {
        // 检验是否登录
        UserVO userVO = userService.getLoginUser(request);
        if (BeanUtil.isEmpty(userVO)) {
            return Result.error(ErrorCode.NOT_LOGIN);
        }
        // 查库检验
        User user = userService.getById(userVO.getId());
        if (BeanUtil.isEmpty(user)) {
            return Result.error(ErrorCode.NULL_ERROR);
        }
        UserVO userInfo = BeanUtil.copyProperties(user, UserVO.class);
        return Result.success(userInfo);
    }

    /**
     * @description: 昵称查询用户
     * @params: [username, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List < com.qiu.qiuxun.model.vo.UserVO>>
     * @author: qiu
     * @dateTime: 2023/12/16 19:55
     */
    @ApiOperation("昵称查询用户")
    @GetMapping("/search")
    public BaseResponse<List<UserVO>> searchUsers(@ApiParam("用户名") @RequestParam(value = "username", required = false) String username, HttpServletRequest request) {
        // 1.鉴权
        if (!userService.isAdmin(request)) {
            return Result.error(ErrorCode.NO_AUTH);
        }
        List<UserVO> userVOList = userService.searchUsers(username);
        return Result.success(userVOList);
    }

    /**
     * @description: 根据id删除用户
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:57
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@ApiParam("用户id") @RequestParam("id") Long id, HttpServletRequest request) {
        // 1.鉴权
        if (!userService.isAdmin(request)) {
            return Result.error(ErrorCode.NO_AUTH);
        }
        boolean isDeleted = userService.removeById(id);
        return Result.success(isDeleted);
    }

    /**
     * @description: 用户注销
     * @params: [request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:58
     */
    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return Result.success(true);
    }

    /**
     * @description: 管理员更新用户信息
     * @params: [request, user]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/21 10:11
     */
    @ApiOperation("用户更新")
    @PutMapping("/update")
    public BaseResponse<Boolean> updateUserById(@ApiParam("用户信息") @RequestBody User user, HttpServletRequest request) {
        Boolean admin = userService.isAdmin(request);
        if (!admin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Boolean result = userService.updateUserById(user);
        return Result.success(result);
    }

    /**
     * @description: 根据标签搜寻用户列表
     * @params: [tags]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List < com.qiu.qiuxun.model.vo.UserVO>>
     * @author: qiu
     * @dateTime: 2024/2/23 15:16
     */
    @ApiOperation("根据标签搜寻用户列表")
    @GetMapping("/search/tags")
    public BaseResponse<List<UserVO>> searchUsersByTags(@ApiParam("标签列表") @RequestParam(required = false) List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return Result.error(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userVOList = userService.searchUsersByTagsMemory(tags);
        return Result.success(userVOList);
    }

    /**
     * @description: 用户更新个人信息
     * @params: [userUpdateRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/2/23 22:46
     */
    @ApiOperation("用户更新个人信息")
    @PutMapping("/update/auth")
    public BaseResponse<Boolean> updateUserByAuth(@ApiParam(value = "用户信息更新请求对象" ,required = true)
                                                      @RequestBody UserUpdateRequest userUpdateRequest,
                                                  HttpServletRequest request) {
        if (request == null) {
            return Result.error(ErrorCode.PARAMS_ERROR);
        }
        if (BeanUtil.isEmpty(userUpdateRequest)) {
            return Result.error(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.updateUserByAuth(userUpdateRequest, request);
        return Result.success(result);
    }

    /**
     * @description: 用户推荐
     * @params: [request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List < com.qiu.qiuxun.model.vo.UserListVO>>
     * @author: qiu
     * @dateTime: 2024/2/26 16:12
     */
    @ApiOperation("用户推荐")
    @PostMapping("/recommend")
    public BaseResponse<UserListVO> recommendUsers(
            @ApiParam("首页用户推荐请求参数")
            @RequestBody PageRequest pageRequest,
            HttpServletRequest request) {
        if (request == null) {
            return Result.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.recommendUsers(pageRequest, request);
    }

    /**
     * @description: 匹配用户
     * @params: [num, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List < com.qiu.qiuxun.model.vo.UserListVO>>
     * @author: qiu
     * @dateTime: 2024/3/10 21:59
     */
    @ApiOperation("匹配用户")
    @GetMapping("/match")
    public BaseResponse<UserListVO> matchUsers(@ApiParam("匹配数量") @RequestParam Long num, HttpServletRequest request) {
        UserListVO userListVO = userService.matchUsers(num, request);
        return Result.success(userListVO);
    }

    /**
     * @description: 获取所有用户
     * @params: [pageRequest]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List<com.qiu.qiuxun.model.vo.UserListVO>>
     * @author: qiu
     * @dateTime: 2024/4/15 13:49
     */
    @ApiOperation("获取所有用户")
    @PostMapping("/all")
    public BaseResponse<UserListVO> getAllUser(@ApiParam("分页参数") @RequestBody PageRequest pageRequest) {
        return userService.getAllUser(pageRequest);
    }

    /**
     * @description: 用户上传头像
     * @params: [file, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.String>
     * @author: qiu
     * @dateTime: 2024/3/25 22:51
     */
    @ApiOperation("用户上传头像")
    @PostMapping("/avatar")
    public BaseResponse<String> uploadAvatar(@ApiParam("图片文件")MultipartFile file, HttpServletRequest request) {
        String avatar = userService.uploadAvatar(file, request);
        return Result.success(avatar);
    }

}
