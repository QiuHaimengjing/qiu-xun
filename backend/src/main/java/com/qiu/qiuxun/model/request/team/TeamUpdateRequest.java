package com.qiu.qiuxun.model.request.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 队伍修改信息请求体对象
 * @className: TeamUpdateRequest.java
 * @author: qiu
 * @createTime: 2024/3/2 12:03
 */
@Data
@ApiModel(value = "队伍修改信息请求体对象")
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -824366520288382548L;

    @ApiModelProperty(value = "队伍id", required = true)
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    private String name;

    @ApiModelProperty(value = "队伍描述")
    private String description;

    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    private Integer status;

    @ApiModelProperty(value = "队伍密码")
    private String password;

}
