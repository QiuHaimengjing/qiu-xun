package com.qiu.qiuxun.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "脱敏的标签列表")
public class TagListVO implements Serializable {

    private static final long serialVersionUID = -7732106487042088521L;

    @ApiModelProperty(value = "脱敏的标签列表")
    List<TagVO> tagList;
}
