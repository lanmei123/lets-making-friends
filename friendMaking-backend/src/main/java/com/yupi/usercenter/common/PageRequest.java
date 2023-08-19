package com.yupi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 7132556857756071421L;
    /**
     * 每页大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}
