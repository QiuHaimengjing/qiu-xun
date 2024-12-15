package com.qiu.qiuxun.model.request.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 修改标签请求体对象
 * @className: TagUpdateRequest.java
 * @author: qiu
 * @createTime: 2024/3/15 13:03
 */
@Data
@ApiModel(value = "修改标签请求体对象")
public class TagUpdateRequest implements Serializable {

    private static final long serialVersionUID = -3816707117480261750L;

    @ApiModelProperty(value = "标签id", required = true)
    private Long id;

    @ApiModelProperty(value = "标签名称", required = true)
    private String tagName;

}
