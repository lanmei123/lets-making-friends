package com.yupi.usercenter.model.request;

import lombok.Data;

/**
 * 用户退出队伍请求体
 */

@Data
public class TeamQuitRequest {
    /**
     * 队伍的id
     */
    private Long teamId;

}
