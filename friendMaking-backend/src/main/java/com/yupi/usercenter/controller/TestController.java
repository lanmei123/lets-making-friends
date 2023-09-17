package com.yupi.usercenter.controller;

import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.request.TestRequest;
import com.yupi.usercenter.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/test")
@RestController
public class TestController {
    @Resource
    UserService userService;
    /**
     * 查询性别为1的信息
     */
    @GetMapping("/sex/{gender}")
    public BaseResponse<List<User>> selectMan(@PathVariable int gender){
        List<User> userBySex = userService.getUserBySex(gender);
        return ResultUtils.success(userBySex);
    }
    @PostMapping("/sex/go")
    public BaseResponse<List<User>> selectManGo(TestRequest testRequest){
        List<User> userBySexGo = userService.getUserBySexGo(testRequest.getGender());
        return ResultUtils.success(userBySexGo);
    }
    @PostMapping("/query")
    public BaseResponse<List<User>> queryTest(TestRequest testRequest){
        List<User> userBySexGo = userService.getUserByQuery(testRequest.getGender());
        return ResultUtils.success(userBySexGo);
    }
}
