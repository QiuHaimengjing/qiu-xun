package com.qiu.qiuxun.model.request.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 创建标签请求体对象
 * @className: TagAddRequest.java
 * @author: qiu
 * @createTime: 2024/3/13 15:17
 */
@Data
@ApiModel(value = "创建标签请求体对象")
public class TagAddRequest implements Serializable {

    private static final long serialVersionUID = 6915911242393475264L;

    @ApiModelProperty(value = "标签名称", required = true)
    private String tagName;

    @ApiModelProperty(value = "父标签id")
    private Long parentId;

}
