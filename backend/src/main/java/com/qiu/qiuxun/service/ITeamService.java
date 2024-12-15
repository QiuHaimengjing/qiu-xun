package com.qiu.qiuxun.service;

import com.qiu.qiuxun.model.request.team.TeamAddRequest;
import com.qiu.qiuxun.model.request.team.TeamQueryRequest;
import com.qiu.qiuxun.model.request.team.TeamUpdateRequest;
import com.qiu.qiuxun.model.po.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiu.qiuxun.model.request.team.TeamJoinRequest;
import com.qiu.qiuxun.model.vo.TeamVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 队伍 服务类
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
public interface ITeamService extends IService<Team> {

    Long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request);

    Boolean deleteTeam(Long id, HttpServletRequest request);

    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    List<TeamVO> teamList(TeamQueryRequest teamQueryRequest, HttpServletRequest request);

    Boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request);

    Boolean quitTeam(Long id, HttpServletRequest request);

    List<TeamVO> myTeams(Long id, HttpServletRequest request);

}
