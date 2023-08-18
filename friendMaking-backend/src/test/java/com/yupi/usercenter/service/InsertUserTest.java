//package com.yupi.usercenter.service;
//
//import com.yupi.usercenter.mapper.UserMapper;
//import com.yupi.usercenter.model.domain.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.StopWatch;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//@SpringBootTest
//public class InsertUserTest {
//
//    @Resource
//    private UserMapper userMapper;
//
//    @Resource
//    private UserService userService;
//
//    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
//
//    /**
//     * 批量插入用户
//     * 为了加快速度，这里用的是saveBatch，批量插入
//     * 单线程
//     */
////    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
//    @Test
//    public void doInsertUsers() {
//        StopWatch stopWatch = new StopWatch();
//        System.out.println("goodgoodgood");
//        List<User> userList = new ArrayList<>();
//        stopWatch.start();
//        final int INSERT_NUM = 10;
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("蓝莓可爱"+i);
//            user.setUserAccount("lanmeiKeAi");
//            user.setAvatarUrl("https://i.postimg.cc/Wp03V70K/image.png");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setPhone("123");
//            user.setEmail("123@qq.com");
//            user.setTags("[]");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setPlanetCode(String.valueOf(i) + 16);
//            userList.add(user);
////            userMapper.insert(user);
//        }
//        userService.saveBatch(userList,1000);
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//
//    /**
//     * 批量插入用户
//     * 为了加快速度，这里用的是saveBatch，并发批量
//     * 多线程
//     */
////    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
//    @Test
//    public void doConcurrencyInsertUsers() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 1000000;
//        final int batchSize = 10000;
//        //分10组
//        int j = 0;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            List<User> userList = new ArrayList<>();
//            while(true){
//                j++;
//                User user = new User();
//                user.setUsername("蓝莓可爱"+i);
//                user.setUserAccount("lanmeiKeAi");
//                user.setAvatarUrl("https://i.postimg.cc/Wp03V70K/image.png");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("123");
//                user.setEmail("123@qq.com");
//                user.setTags("[]");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode(String.valueOf(i) + 16);
//                userList.add(user);
//                if (j %batchSize == 0){
//                    break;
//                }
//            }
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
//                System.out.println("threadName: " + Thread.currentThread().getName());
//                userService.saveBatch(userList,batchSize);
//            },executorService);
//            futureList.add(future);
//            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//}
