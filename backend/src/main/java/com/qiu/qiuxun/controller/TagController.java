package com.qiu.qiuxun.controller;


import com.qiu.qiuxun.common.BaseResponse;
import com.qiu.qiuxun.common.Result;
import com.qiu.qiuxun.model.request.tag.TagAddRequest;
import com.qiu.qiuxun.model.request.tag.TagUpdateRequest;
import com.qiu.qiuxun.model.vo.TagListVO;
import com.qiu.qiuxun.service.ITagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 标签 前端控制器
 * </p>
 *
 * @author qiu
 * @since 2024-02-20
 */
@RestController
@RequestMapping("/tag")
@Api(tags = "标签")
public class TagController {

    @Resource
    private ITagService tagService;

    /**
     * @description: 添加标签
     * @params: [tagAddRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Long>
     * @author: qiu
     * @dateTime: 2024/3/14 0:08
     */
    @ApiOperation("添加标签")
    @PostMapping("/add")
    public BaseResponse<Long> tagAdd(@ApiParam(value = "标签信息", required = true)
                                         @RequestBody TagAddRequest tagAddRequest,
                                     HttpServletRequest request) {
        Long tagId = tagService.tagAdd(tagAddRequest, request);
        return Result.success(tagId);
    }

    /**
     * @description: 删除标签
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/15 13:02
     */
    @ApiOperation("删除标签")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> tagDelete(@ApiParam(value = "标签id", required = true) @RequestParam Long id,
                                           HttpServletRequest request) {
        Boolean result = tagService.tagDelete(id, request);
        return Result.success(result);
    }

    /**
     * @description: 修改标签
     * @params: [tagUpdateRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/15 13:18
     */
    @ApiOperation("修改标签")
    @PutMapping("/update")
    public BaseResponse<Boolean> tagUpdate(@ApiParam(value = "修改标签请求体对象", required = true)
                                               @RequestBody TagUpdateRequest tagUpdateRequest,
                                           HttpServletRequest request) {
        Boolean result = tagService.tagUpdate(tagUpdateRequest, request);
        return Result.success(result);
    }

    /**
     * @description: 查询标签列表
     * @params: [request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List<com.qiu.qiuxun.model.vo.TagVO>>
     * @author: qiu
     * @dateTime: 2024/3/15 13:53
     */
    @ApiOperation("查询标签列表")
    @GetMapping("/list")
    public BaseResponse<TagListVO> tagList(HttpServletRequest request) {
        TagListVO tagListVO = new TagListVO();
        tagListVO.setTagList(tagService.tagList(request));
        return Result.success(tagListVO);
    }

}
