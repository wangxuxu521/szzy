package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    // 密码加密器
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据ID查找用户
     */
    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }

    /**
     * 查找所有用户
     */
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 保存用户（新增或更新）
     */
    public void save(User user) {
        if (user.getUserId() == null) {
            // 新增用户时加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userMapper.insert(user);
        } else {
            // 更新用户时如果密码发生变化则加密
            User existingUser = userMapper.findById(user.getUserId());
            if (!existingUser.getPassword().equals(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userMapper.update(user);
        }
    }

    /**
     * 删除用户
     */
    public void delete(Integer userId) {
        userMapper.delete(userId);
    }
    
    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户对象，失败返回null
     */
    public User login(String username, String password) {
        // 从数据库查询用户
        User user = userMapper.findByUsername(username);
        System.out.println("查询到用户: " + user);
        
        if (user != null) {
            // 根据SQL中的数据，所有用户密码都是123456
            // 检查密码是否匹配（优先检查明文密码）
            if (password.equals(user.getPassword())) {
                System.out.println("明文密码匹配成功");
                return user;
            }
            
            // 如果明文密码不匹配，尝试加密密码比较（以防万一密码已加密）
            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("加密密码匹配成功");
                return user;
            }
        }
        
        System.out.println("登录失败: 用户名或密码错误");
        return null;
    }
} 