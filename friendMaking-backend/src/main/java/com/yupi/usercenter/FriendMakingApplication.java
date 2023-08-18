package com.yupi.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.yupi.usercenter.mapper")
@EnableScheduling
public class FriendMakingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendMakingApplication.class, args);
    }

}
