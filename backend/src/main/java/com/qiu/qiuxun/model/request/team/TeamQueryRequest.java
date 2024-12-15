package com.qiu.qiuxun.model.request.team;

import com.qiu.qiuxun.model.request.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @description: 队伍查询请求体对象
 * @className: TeamQueryRequest.java
 * @author: qiu
 * @createTime: 2024/3/2 13:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "队伍查询请求体对象")
public class TeamQueryRequest extends PageRequest {

    private static final long serialVersionUID = -5955005228268560738L;

    @ApiModelProperty(value = "队伍id")
    private Long id;

    @ApiModelProperty(value = "搜索关键词(对应队伍名称和描述)")
    private String keyWords;

    @ApiModelProperty(value = "队伍名称")
    private String name;

    @ApiModelProperty(value = "队伍描述")
    private String description;

    @ApiModelProperty(value = "最大人数")
    private Integer maxNum;

    @ApiModelProperty(value = "创建人id")
    private Long leader;

    @ApiModelProperty(value = "0-公开，1-私有，2-加密")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
