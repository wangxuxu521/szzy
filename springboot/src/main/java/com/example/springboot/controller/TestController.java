package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/db")
    public Result testDatabase() {
        try {
            List<User> users = userMapper.findAll();
            return Result.success("数据库连接正常，用户数量：" + users.size(), users);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据库错误：" + e.getMessage());
        }
    }
    
    @GetMapping("/hello")
    public Result hello() {
        return Result.success("Hello World!");
    }
} 