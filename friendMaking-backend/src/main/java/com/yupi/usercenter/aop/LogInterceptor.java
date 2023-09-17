package com.yupi.usercenter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogInterceptor {

//    @Around("execution(* com.yupi.usercenter.controller.*.*(..))")
//    public  Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
//        return null;
//    }
}
