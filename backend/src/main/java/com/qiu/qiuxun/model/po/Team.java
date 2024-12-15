package com.qiu.qiuxun.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 队伍
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("team")
@ApiModel(value="Team对象", description="队伍")
public class Team implements Serializable {

    private static final long serialVersionUID = -128437579588307316L;

    @ApiModelProperty(value = "队伍id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "队伍名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "队伍描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "最大人数")
    @TableField("max_num")
    private Integer maxNum;

    @ApiModelProperty(value = "当前人数")
    @TableField("current_num")
    private Integer currentNum;

    @ApiModelProperty(value = "过期时间")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("leader")
    private Long leader;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "队伍密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否删除 0 1（默认值0）")
    @TableField("deleted")
    private Integer deleted;


}
