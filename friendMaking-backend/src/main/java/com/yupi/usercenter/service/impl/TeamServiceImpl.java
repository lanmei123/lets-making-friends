package com.yupi.usercenter.service.impl;

import com.alibaba.excel.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.model.enums.TeamStatusEnum;
import com.yupi.usercenter.model.request.TeamAddRequest;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
* @author win
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

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
}




