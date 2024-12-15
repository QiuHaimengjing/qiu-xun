package com.qiu.qiuxun.controller;

import com.qiu.qiuxun.common.BaseResponse;
import com.qiu.qiuxun.common.Result;
import com.qiu.qiuxun.model.request.team.TeamAddRequest;
import com.qiu.qiuxun.model.request.team.TeamQueryRequest;
import com.qiu.qiuxun.model.request.team.TeamUpdateRequest;
import com.qiu.qiuxun.model.request.team.TeamJoinRequest;
import com.qiu.qiuxun.model.vo.TeamVO;
import com.qiu.qiuxun.service.ITeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 队伍 前端控制器
 * </p>
 *
 * @author qiu
 * @since 2024-03-01
 */
@RestController
@RequestMapping("/team")
@Api(tags = "队伍")
public class TeamController {

    @Resource
    private ITeamService teamService;

    /**
     * @description: 创建队伍
     * @params: [teamAddRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Long>
     * @author: qiu
     * @dateTime: 2024/3/2 12:00
     */
    @ApiOperation("创建队伍")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@ApiParam(value = "队伍创建请求体对象", required = true)
                                          @RequestBody TeamAddRequest teamAddRequest,
                                      HttpServletRequest request) {
        Long teamId = teamService.addTeam(teamAddRequest, request);
        return Result.success(teamId);
    }

    /**
     * @description: 删除队伍
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/2 11:26
     */
    @ApiOperation("删除队伍")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@ApiParam(value = "队伍id", required = true)
                                                @RequestParam Long id, HttpServletRequest request) {
        Boolean result = teamService.deleteTeam(id, request);
        return Result.success(result);
    }

    /**
     * @description: 修改队伍
     * @params: [teamUpdateRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/2 13:15
     */
    @ApiOperation("修改队伍")
    @PutMapping("/update")
    public BaseResponse<Boolean> updateTeam(@ApiParam(value = "队伍修改信息请求体对象", required = true)
                                                @RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        Boolean result = teamService.updateTeam(teamUpdateRequest, request);
        return Result.success(result);
    }

    /**
     * @description: 查询队伍列表
     * @params: [teamQueryRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List<com.qiu.qiuxun.model.vo.TeamVO>>
     * @author: qiu
     * @dateTime: 2024/3/4 23:18
     */
    @ApiOperation("查询队伍列表")
    @PostMapping("/list")
    public BaseResponse<List<TeamVO>> teamList(@ApiParam(value = "队伍查询请求体对象", required = true)
                                                   @RequestBody TeamQueryRequest teamQueryRequest,
                                               HttpServletRequest request) {
        List<TeamVO> teamVOList = teamService.teamList(teamQueryRequest, request);
        return Result.success(teamVOList);
    }

    /**
     * @description: 用户加入队伍
     * @params: [teamJoinRequest, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/3 14:27
     */
    @ApiOperation("用户加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@ApiParam(value = "用户加入队伍请求体对象" ,required = true)
                                              @RequestBody TeamJoinRequest teamJoinRequest,
                                          HttpServletRequest request) {
        Boolean result = teamService.joinTeam(teamJoinRequest, request);
        return Result.success(result);
    }

    /**
     * @description: 用户退出队伍
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2024/3/6 10:38
     */
    @ApiOperation("用户退出队伍")
    @DeleteMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@ApiParam(value = "队伍id", required = true)
                                              @RequestParam Long id, HttpServletRequest request) {
        Boolean result = teamService.quitTeam(id, request);
        return Result.success(result);
    }

    /**
     * @description: 我加入的队伍
     * @params: [id, request]
     * @return: com.qiu.qiuxun.common.BaseResponse<java.util.List<com.qiu.qiuxun.model.vo.TeamVO>>
     * @author: qiu
     * @dateTime: 2024/3/9 16:48
     */
    @ApiOperation("我加入的队伍")
    @GetMapping("/my")
    public BaseResponse<List<TeamVO>> myTeams(@ApiParam("队伍id")
                                                  @RequestParam(required = false) Long id,
                                              HttpServletRequest request) {
        List<TeamVO> teamVOList = teamService.myTeams(id, request);
        return Result.success(teamVOList);
    }

}
