package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器，处理登录/登出
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录 - 从数据库验证用户
     * @param loginData 登录数据，包含用户名和密码
     * @return 登录结果，成功返回token和用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData) {
        System.out.println("收到登录请求，数据: " + loginData);
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        try {
            // 调用Service层验证用户
            User user = userService.login(username, password);
            
            if (user != null) {
                // 准备返回数据
                Map<String, Object> data = new HashMap<>();
                String simpleToken = "token_" + username + "_" + System.currentTimeMillis();
                data.put("token", simpleToken);
                data.put("userId", user.getUserId());
                data.put("username", user.getUsername());
                data.put("name", user.getName());
                data.put("role", user.getRole());
                
                return Result.success("登录成功", data);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result logout() {
        return Result.success("已成功登出");
    }
    
    /**
     * 获取当前用户信息 - 从数据库获取
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "未授权");
        }
        
        try {
            String actualToken = token.substring(7); // 去掉"Bearer "前缀
            // 从简单token中提取用户名: token_username_timestamp
            String[] parts = actualToken.split("_");
            if (parts.length < 3) {
                return Result.error(401, "无效的令牌");
            }
            
            String username = parts[1];
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return Result.error(401, "用户不存在");
            }
            
            // 准备返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("role", user.getRole());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(401, "获取用户信息失败: " + e.getMessage());
        }
    }
} 