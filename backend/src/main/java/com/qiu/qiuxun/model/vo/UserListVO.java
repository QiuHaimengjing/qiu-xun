package com.qiu.qiuxun.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 脱敏的用户列表
 * @className: UserListVO.java
 * @author: qiu
 * @createTime: 2023/12/9 15:50
 */
@Data
@ApiModel(value = "脱敏的用户列表")
public class UserListVO implements Serializable {

    private static final long serialVersionUID = 3743790298113657174L;

    @ApiModelProperty(value = "用户列表")
    List<UserVO> userList;

    @ApiModelProperty(value = "总记录数")
    Long total;
}
