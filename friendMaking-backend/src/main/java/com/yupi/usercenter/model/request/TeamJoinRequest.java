package com.yupi.usercenter.model.request;

import lombok.Data;

/**
 * 用户加入队伍的请求体
 */
@Data
public class TeamJoinRequest {
    /**
     * 队伍的id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
