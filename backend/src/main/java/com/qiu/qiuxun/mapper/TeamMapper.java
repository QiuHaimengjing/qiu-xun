package com.qiu.qiuxun.mapper;

import com.qiu.qiuxun.model.po.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiu.qiuxun.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 队伍 Mapper 接口
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
public interface TeamMapper extends BaseMapper<Team> {
    List<UserVO> queryMembers(@Param("teamId") Long id);

}
