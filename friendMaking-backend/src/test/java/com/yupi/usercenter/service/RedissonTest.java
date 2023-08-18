package com.yupi.usercenter.service;

import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        //list
        List<String> list = new ArrayList<>();
        list.add("lanmei");
        redissonClient.getList("123");
        //map
    }
}

