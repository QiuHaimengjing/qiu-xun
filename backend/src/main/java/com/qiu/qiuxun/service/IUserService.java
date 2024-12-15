package com.qiu.qiuxun.service;

import com.qiu.qiuxun.common.BaseResponse;
import com.qiu.qiuxun.model.request.user.UserLoginRequest;
import com.qiu.qiuxun.model.request.user.UserRegisterRequest;
import com.qiu.qiuxun.model.request.user.UserUpdateRequest;
import com.qiu.qiuxun.model.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiu.qiuxun.model.request.PageRequest;
import com.qiu.qiuxun.model.vo.UserListVO;
import com.qiu.qiuxun.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
public interface IUserService extends IService<User> {
    Boolean userRegister(UserRegisterRequest userRegisterRequest);

    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    List<UserVO> searchUsers(String username);

    Boolean updateUserById(User user);

    List<UserVO> searchUsersByTagsSQL(List<String> tags);

    List<UserVO> searchUsersByTagsMemory(List<String> tags);

    Boolean isAdmin(HttpServletRequest request);

    UserVO getLoginUser(HttpServletRequest request);

    Boolean updateUserByAuth(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    BaseResponse<UserListVO> recommendUsers(PageRequest pageRequest, HttpServletRequest request);

    UserListVO matchUsers(Long num, HttpServletRequest request);

    BaseResponse<UserListVO> getAllUser(PageRequest pageRequest);

    String uploadAvatar(MultipartFile file, HttpServletRequest request);

    List<Long> getJobUsers();

}
