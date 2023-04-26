package org.example.controller;


import org.example.entity.CommonResult;
import org.example.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class consumerServiceFallback {
    @RequestMapping(value = "/fallback",method = RequestMethod.GET)
    public CommonResult getCommonResult(){
        return new CommonResult<>(403,"由于consumerService异常进行服务降级响应",new User());
    }
}
