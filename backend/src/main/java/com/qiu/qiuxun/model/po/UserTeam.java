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
 * 用户队伍关系
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_team")
@ApiModel(value="UserTeam对象", description="用户队伍关系")
public class UserTeam implements Serializable {

    private static final long serialVersionUID = 5603908006394931216L;

    @ApiModelProperty(value = "用户队伍关系id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "队伍id")
    @TableField("team_id")
    private Long teamId;

    @ApiModelProperty(value = "是否为创建人 1-创建人 0-否 （默认0）")
    @TableField("is_leader")
    private Integer isLeader;

    @ApiModelProperty(value = "加入时间")
    @TableField("join_time")
    private LocalDateTime joinTime;

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
