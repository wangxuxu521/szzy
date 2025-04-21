package com.example.springboot.controller;

import com.example.springboot.util.Result;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User loginUser) {
        if (loginUser.getUsername() == null || loginUser.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }
        User user = userService.findByUsername(loginUser.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        User authenticatedUser = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (authenticatedUser != null) {
            // 生成简单的令牌
            String token = UUID.randomUUID().toString();
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", authenticatedUser.getUserId());
            data.put("username", authenticatedUser.getUsername());
            data.put("name", authenticatedUser.getName());
            data.put("role", authenticatedUser.getRole());
            data.put("token", token);
            
            return Result.success(data);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @GetMapping("/info")
    public Result getUserInfo(@RequestParam Integer userId) {
        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PostMapping
    public Result save(@RequestBody User user) {
        try {
            // 验证必填字段
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                return Result.error("角色不能为空");
            }
            
            // 验证角色是否合法
            if (!user.getRole().matches("^(admin|teacher|student)$")) {
                return Result.error("角色不合法");
            }
            
            // 验证密码长度
            if (user.getPassword().length() < 6) {
                return Result.error("密码长度不能少于6位");
            }
            
            userService.save(user);
            return Result.success("用户保存成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存用户失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        User user = userService.findById(id);
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result list() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success();
    }

    // 获取所有教师
    @GetMapping("/teachers")
    public Result findAllTeachers() {
        try {
            return Result.success(userService.findByRole("teacher"));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师列表失败: " + e.getMessage());
        }
    }
} 