package com.yupi.usercenter.mapper;

import com.yupi.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 *
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> getUserBySex(int gender);

    List<User> getUserBySexGo(int gender);
}



