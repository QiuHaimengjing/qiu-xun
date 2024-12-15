package com.qiu.qiuxun.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.qiu.qiuxun.common.ErrorCode;
import com.qiu.qiuxun.exception.BusinessException;
import com.qiu.qiuxun.model.request.tag.TagAddRequest;
import com.qiu.qiuxun.model.request.tag.TagUpdateRequest;
import com.qiu.qiuxun.model.po.Tag;
import com.qiu.qiuxun.mapper.TagMapper;
import com.qiu.qiuxun.model.vo.TagVO;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiu.qiuxun.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.qiu.qiuxun.constant.TagConstant.CHILDREN;
import static com.qiu.qiuxun.constant.TagConstant.PARENT;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author qiu
 * @since 2024-02-20
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Resource
    private IUserService userService;

    /**
     * @description: 创建标签
     * @params: [tagAddRequest, request]
     * @return: java.lang.Long
     * @author: qiu
     * @dateTime: 2024/3/14 0:07
     */
    @Override
    public Long tagAdd(TagAddRequest tagAddRequest, HttpServletRequest request) {
        // 1.校验参数
        UserVO user = userService.getLoginUser(request);
        Long userId = user.getId();
        if (BeanUtil.isEmpty(tagAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.检查标签名是否存在
        String tagName = tagAddRequest.getTagName();
        if (StrUtil.isBlank(tagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名不允许为空");
        }
        Long exist = lambdaQuery().eq(Tag::getTagName, tagName).count();
        if (exist > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "标签已存在");
        }
        // 3.检查父标签
        Long parentId = tagAddRequest.getParentId();
        Tag tag = new Tag();
        // 4.有无父标签id
        if (BeanUtil.isEmpty(parentId) || parentId.equals(0L)) {
            // 没有设置为新的父标签
            tag.setIsParent(PARENT);
        } else {
            // 有父标签则查找父标签
            Tag parentTag = lambdaQuery().eq(Tag::getId, parentId).one();
            if (BeanUtil.isEmpty(parentTag) || parentTag.getIsParent().equals(CHILDREN)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不存在该父标签");
            }
            tag.setParentId(parentTag.getId());
        }
        // 5.添加数据库
        tag.setUserId(userId);
        tag.setTagName(tagName);
        boolean save = save(tag);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return tag.getId();
    }

    /**
     * @description: 删除标签
     * @params: [id, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/15 13:02
     */
    @Override
    public Boolean tagDelete(Long id, HttpServletRequest request) {
        // 1.校验参数
        Tag tag = checkTag(id, request);
        // 2.如果为父标签还需要删除其子标签
        Integer isParent = tag.getIsParent();
        if (isParent.equals(PARENT)) {
            boolean result = lambdaUpdate().eq(Tag::getId, id).or().eq(Tag::getParentId, id).remove();
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            return true;
        }
        // 3.如果为子标签直接删除
        boolean result = removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    /**
     * @description: 修改标签
     * @params: [tagUpdateDTO, request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/15 13:18
     */
    @Override
    public Boolean tagUpdate(TagUpdateRequest tagUpdateRequest, HttpServletRequest request) {
        // 1.校验参数
        Long id = tagUpdateRequest.getId();
        checkTag(id, request);
        // 2.标签名不能为空
        String tagName = tagUpdateRequest.getTagName();
        if (StrUtil.isBlank(tagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名不能为空");
        }
        // 3.更新标签信息
        Tag updateTag = BeanUtil.copyProperties(tagUpdateRequest, Tag.class);
        boolean result = updateById(updateTag);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    /**
     * @description: 查询标签列表
     * @params: [request]
     * @return: java.util.List<com.qiu.qiuxun.model.vo.TagVO>
     * @author: qiu
     * @dateTime: 2024/3/15 13:53
     */
    @Override
    public List<TagVO> tagList(HttpServletRequest request) {
        // 1.校验参数
        userService.getLoginUser(request);
        // 2.查询所有标签
        List<Tag> list = list();
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<TagVO> tagVOList = BeanUtil.copyToList(list, TagVO.class);
        // 3.筛选出父标签
        List<TagVO> parentList = tagVOList.stream().filter(item -> item.getIsParent().equals(PARENT)).collect(Collectors.toList());
        // 4.筛选出子标签
        List<TagVO> childrenList = tagVOList.stream().filter(item -> item.getIsParent().equals(CHILDREN)).collect(Collectors.toList());
        // 5.父标签的子标签列表赋值
        for (TagVO tagVO : parentList) {
            List<TagVO> collect = childrenList.stream()
                    .filter(item -> item.getParentId() != null && item.getParentId().equals(tagVO.getId()))
                    .collect(Collectors.toList());
            tagVO.setChildren(collect);
        }
        return parentList;
    }

    /**
     * @description: 检查标签是否存在和用户权限
     * @params: [id, request]
     * @return: com.qiu.qiuxun.model.po.Tag
     * @author: qiu
     * @dateTime: 2024/3/15 14:31
     */
    private Tag checkTag(Long id, HttpServletRequest request) {
        // 1.校验参数
        UserVO user = userService.getLoginUser(request);
        Long userId = user.getId();
        Boolean admin = userService.isAdmin(request);
        if (BeanUtil.isEmpty(id) || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不存在");
        }
        // 2.检验标签是否存在
        Tag tag = getById(id);
        if (BeanUtil.isEmpty(tag)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "标签不存在");
        }
        // 3.检验用户是否为标签创建者或管理员
        Long tagUserId = tag.getUserId();
        if (!tagUserId.equals(userId) && !admin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return tag;
    }
}
