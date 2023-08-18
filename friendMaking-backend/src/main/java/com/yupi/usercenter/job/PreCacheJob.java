package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */

@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天执行
     */
    @Scheduled(cron = "0 23 11 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("lanmei:preCacheJob:doCache:lock");
        try {
            if (lock.tryLock(0,30,TimeUnit.MINUTES)){
                for (Long userId : mainUserList) {
                    String redisKey = String.format("lanmei:user:recommend:%s",userId);
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 8), queryWrapper);

                    //写缓存
                    try {
                        redisTemplate.opsForValue().set(redisKey,userPage,30, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
                lock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
