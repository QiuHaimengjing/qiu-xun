package com.qiu.qiuxun.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 脱敏的队伍信息
 * @className: TeamVO.java
 * @author: qiu
 * @createTime: 2024/3/2 13:16
 */
@Data
@ApiModel(value = "脱敏的队伍信息")
public class TeamVO implements Serializable {

    private static final long serialVersionUID = 3045636396728962996L;

    @ApiModelProperty(value = "队伍id")
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    private String name;

    @ApiModelProperty(value = "队伍描述")
    private String description;

    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    @ApiModelProperty(value = "当前人数")
    private Integer currentNum;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "创建人id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long leader;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "已加入")
    private Boolean joined = false;

    @ApiModelProperty(value = "创建人用户信息")
    private UserVO createUser;

    @ApiModelProperty(value = "队伍成员")
    private List<UserVO> members;

}
