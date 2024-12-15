package com.qiu.qiuxun.service;

import com.qiu.qiuxun.model.request.tag.TagAddRequest;
import com.qiu.qiuxun.model.request.tag.TagUpdateRequest;
import com.qiu.qiuxun.model.po.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiu.qiuxun.model.vo.TagVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 标签 服务类
 * </p>
 *
 * @author qiu
 * @since 2024-02-20
 */
public interface ITagService extends IService<Tag> {

    Long tagAdd(TagAddRequest tagAddRequest, HttpServletRequest request);

    Boolean tagDelete(Long id, HttpServletRequest request);

    Boolean tagUpdate(TagUpdateRequest tagUpdateRequest, HttpServletRequest request);

    List<TagVO> tagList(HttpServletRequest request);
}
