package com.qiu.qiuxun.model.request.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 队伍创建请求体对象
 * @className: TeamAddRequest.java
 * @author: qiu
 * @createTime: 2024/3/1 11:19
 */
@Data
@ApiModel(value = "队伍创建请求体对象")
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = -8003686684481386020L;

    @ApiModelProperty(value = "队伍名称", required = true)
    private String name;

    @ApiModelProperty(value = "队伍描述", required = true)
    private String description;

    @ApiModelProperty(value = "最大人数", required = true)
    private Integer maxNum;

    @ApiModelProperty(value = "过期时间", required = true)
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密", required = true)
    private Integer status;

    @ApiModelProperty(value = "队伍密码")
    private String password;

}
