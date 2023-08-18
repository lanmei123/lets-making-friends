package com.yupi.usercenter.service;
import java.util.Date;

import com.yupi.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("lanmei1","1");
        valueOperations.set("lanmei2", 1);
        valueOperations.set("lanmei3",1.1);
        User user = new User();
        user.setId(1L);
        user.setUsername("lanmeihaochimiao");
        valueOperations.set("lanmei4",user);

        Object lanmei1 = valueOperations.get("lanmei1");
        System.out.println(lanmei1.toString());
    }
}













