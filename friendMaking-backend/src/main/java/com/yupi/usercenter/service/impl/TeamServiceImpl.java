package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.model.dto.TeamQuery;
import com.yupi.usercenter.model.enums.TeamStatusEnum;
import com.yupi.usercenter.model.request.TeamJoinRequest;
import com.yupi.usercenter.model.request.TeamQuitRequest;
import com.yupi.usercenter.model.request.TeamUpdateRequest;
import com.yupi.usercenter.model.vo.TeamUserVO;
import com.yupi.usercenter.model.vo.UserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author win
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1.请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登陆，未登录不允许创建
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();

        //3.校验信息
        //  1.队伍人数> 1 且 <=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(20);
        if (maxNum <1 || maxNum >=20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //  2.队伍名字 <=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名字不符合要求");
        }
        //  3.描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //  4.status 是否公开 ,默认为0公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //  5.如果 status 是加密状态，一定要有密码，且密码 <32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码格式不正确");
        }
        //  6.超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间> 当前时间");
        }
        //  7.校验用户最多创建 5 个队伍
        // todo 有点问题,创建的时候如果点的很快可能超过5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5){
             throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个用户");
        }
        //  8.注入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();

        if (!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        //  9.注入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result2 = userTeamService.save(userTeam);
        if (!result2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery,Boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //如果为不为空就查询，组合查询条件
        if (teamQuery != null){
            Long id = teamQuery.getId();
            if (id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            List<Long> teamIdList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(teamIdList)){
                queryWrapper.in("id",teamIdList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            //查询最大人数相等
            if (maxNum != null && maxNum >0){
                queryWrapper.eq("maxNum",maxNum);
            }
            // 根据创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId >0){
                queryWrapper.eq("userId",userId);
            }

            // 根据状态查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",statusEnum.getValue());
        }
        // 不显示已过期的任务
        queryWrapper.and(qw -> qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        List<Team> teamList  = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询信息
        //1. 自己写sql
        // 查询对合和创建人的信息
        // select * from team t left join user u on t.userId = u.id
        //2. 查询队伍和已加入队伍成员的信息
        // select * from team t left join user_team ut on t.id = ut.temId
        //如果要关联的表比较少，就自己写语句，如果关联的比较多，就自己写sql
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);

            if (user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (oldTeam.getUserId() != loginUser.getId() && userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间需要有密码");
            }
        }
        Team newTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        boolean result = this.updateById(newTeam);
        return result;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有的队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不对");
            }
        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多只能加入5个队伍");
        }
        // 不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已经加入该队伍");
        }
        // 已加入的队伍人数
        long teamHasJoinNum = countTeamUserByTeamId(teamId);
        if (teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已满");
        }
        // 修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未加入队伍");
        }
        long teamHasJoinNum = countTeamUserByTeamId(teamId);
        //队伍只剩一人
        if (teamHasJoinNum == 1){
            // 删除队伍和加入队伍的关系
            this.removeById(teamId);
        }else {
            //队伍还剩至少两人
            //是队长
            if (team.getUserId() == userId){
                //把队伍转移给加入最早的用户
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        // 校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        // 校验是否是队伍的队长
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"只有组长可以删除队伍");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据id获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求队伍不存在");
        }
        return team;
    }

    /**
     * 统计一个队有多少个人
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(Long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        return teamHasJoinNum;
    }
}




