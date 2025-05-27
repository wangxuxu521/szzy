package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.util.JwtUtils;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器，处理登录/登出/注册
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 用户登录 - 使用JWT认证
     * @param loginData 登录数据，包含用户名和密码
     * @return 登录结果，成功返回JWT token和用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        try {
            // 先使用原有的login方法验证用户名和密码（兼容明文密码）
            User user = userService.login(username, password);
            
            if (user == null) {
                return Result.error("用户名或密码错误");
            }
            
            // 生成访问令牌和刷新令牌
            String accessToken = jwtUtils.generateToken(username, user.getRole(), user.getUserId());
            String refreshToken = jwtUtils.generateRefreshToken(username, user.getRole(), user.getUserId());
            
            // 更新用户最后登录时间
            user.setLastLoginTime(new Date());
            userService.updateLastLoginTime(user);
            
            // 准备返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", accessToken);
            data.put("refreshToken", refreshToken);
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("role", user.getRole());
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户注册
     * @param registerData 注册数据，包含用户名、密码和其他信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> registerData) {
        String username = registerData.get("username");
        String password = registerData.get("password");
        String name = registerData.get("name");
        String role = registerData.get("role");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        // 验证用户名长度
        if (username.length() < 3 || username.length() > 20) {
            return Result.error("用户名长度应为3-20个字符");
        }
        
        // 验证密码长度
        if (password.length() < 6 || password.length() > 20) {
            return Result.error("密码长度应为6-20个字符");
        }
        
        // 默认角色为学生
        if (role == null || role.isEmpty()) {
            role = "student";
        }
        
        try {
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(username);
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // UserService会自动加密
            newUser.setName(name != null ? name : username);
            newUser.setRole(role);
            newUser.setStatus(1); // 设置状态为启用
            newUser.setCreateTime(new Date());
            
            // 保存用户
            userService.save(newUser);
            
            return Result.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     * @param tokenData 包含刷新令牌的数据
     * @return 新的访问令牌
     */
    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestBody Map<String, String> tokenData) {
        String refreshToken = tokenData.get("refreshToken");
        
        if (refreshToken == null) {
            return Result.error(401, "刷新令牌不能为空");
        }
        
        try {
            // 验证刷新令牌
            if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
                return Result.error(401, "无效的刷新令牌");
            }
            
            // 从令牌中提取用户信息
            String username = jwtUtils.extractUsername(refreshToken);
            String role = jwtUtils.extractRole(refreshToken);
            Integer userId = jwtUtils.extractUserId(refreshToken);
            
            // 检查用户是否存在
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error(401, "用户不存在");
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtUtils.generateToken(username, role, userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            
            return Result.success("令牌刷新成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(401, "刷新令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result logout() {
        // JWT无状态，服务端不需要额外操作
        return Result.success("已成功登出");
    }
    
    /**
     * 获取当前用户信息 - 从JWT令牌获取
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "未授权");
        }
        
        try {
            String jwt = token.substring(7);
            String username = jwtUtils.extractUsername(jwt);
            
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
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("avatar", user.getAvatar());
            data.put("status", user.getStatus());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(401, "获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证令牌有效性
     * @param token 认证令牌
     * @return 验证结果
     */
    @GetMapping("/validate-token")
    public Result validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "无效的令牌格式");
        }
        
        try {
            String jwt = token.substring(7);
            
            if (!jwtUtils.validateToken(jwt)) {
                return Result.error(401, "令牌已过期或无效");
            }
            
            return Result.success(true);
        } catch (Exception e) {
            return Result.error(401, "令牌验证失败: " + e.getMessage());
        }
    }
} 