package com.qiu.qiuxun.model.request.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 用户加入队伍请求体对象
 * @className: TeamJoinRequest.java
 * @author: qiu
 * @createTime: 2024/3/3 13:44
 */
@Data
@ApiModel(value = "用户加入队伍请求体对象")
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 7476129300484769272L;

    @ApiModelProperty(value = "队伍id", required = true)
    private Long id;

    @ApiModelProperty(value = "队伍密码")
    private String password;

}
