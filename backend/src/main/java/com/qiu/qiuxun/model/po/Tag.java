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
 * 标签
 * </p>
 *
 * @author qiu
 * @since 2024-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tag")
@ApiModel(value="Tag对象", description="标签")
public class Tag implements Serializable {

    private static final long serialVersionUID = 3839174446147108138L;

    @ApiModelProperty(value = "标签id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签名称")
    @TableField("tag_name")
    private String tagName;

    @ApiModelProperty(value = "上传标签的用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "父标签id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "0 - 不是，1 - 父标签")
    @TableField("is_parent")
    private Integer isParent;

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
