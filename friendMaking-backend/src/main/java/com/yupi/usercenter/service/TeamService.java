package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.request.TeamAddRequest;

/**
* @author win
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-08-18 15:20:49
*/
public interface TeamService extends IService<Team> {
    /**
     *
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
