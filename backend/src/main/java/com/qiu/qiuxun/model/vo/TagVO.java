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
 * @description: 脱敏的标签信息
 * @className: TagVO.java
 * @author: qiu
 * @createTime: 2024/3/15 13:19
 */
@Data
@ApiModel(value = "脱敏的标签信息")
public class TagVO implements Serializable {

    private static final long serialVersionUID = 3762914435682632916L;

    @ApiModelProperty(value = "标签id")
    private Long id;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "上传标签的用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "父标签id")
    private Long parentId;

    @ApiModelProperty(value = "0 - 不是，1 - 父标签")
    private Integer isParent;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "子标签")
    private List<TagVO> children;

}
