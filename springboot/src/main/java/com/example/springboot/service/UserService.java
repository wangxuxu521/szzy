package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.entity.UserAction;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.mapper.UserActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserActionMapper userActionMapper;
    
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
     * 根据角色查找用户
     */
    public List<User> findByRole(String role) {
        return userMapper.findByRole(role);
    }

    /**
     * 保存用户（新增或更新）
     */
    public void save(User user) {
        // 检查用户名是否已存在
        if (user.getUserId() == null) {
            User existingUser = userMapper.findByUsername(user.getUsername());
            if (existingUser != null) {
                throw new RuntimeException("用户名已存在");
            }
            // 新增用户时加密密码(如果密码未加密)
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            // 设置创建时间
            user.setCreateTime(new Date());
            userMapper.insert(user);
        } else {
            // 更新用户时如果密码发生变化则加密
            User existingUser = userMapper.findById(user.getUserId());
            if (existingUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 检查用户名是否被其他用户使用
            User userWithSameUsername = userMapper.findByUsername(user.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("用户名已被其他用户使用");
            }
            
            // 如果密码发生变化且未加密，则进行加密
            if (!existingUser.getPassword().equals(user.getPassword()) && !user.getPassword().startsWith("$2a$")) {
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
        
        if (user == null) {
            return null; // 用户不存在
        }
        
        boolean passwordMatch = false;
        
        // 检查密码是否匹配
        // 1. 判断数据库中密码是否是加密格式
        if (user.getPassword().startsWith("$2a$")) {
            // 使用加密密码比较
            if (passwordEncoder.matches(password, user.getPassword())) {
                passwordMatch = true;
            }
        } else {
            // 数据库中存储的是明文密码，直接比较
            if (password.equals(user.getPassword())) {
                passwordMatch = true;
                
                // 找到用户且密码匹配，更新密码为加密格式
                User updatedUser = new User();
                updatedUser.setUserId(user.getUserId());
                updatedUser.setPassword(passwordEncoder.encode(password));
                try {
                    userMapper.updatePassword(updatedUser);
                    // 更新内存中的用户对象密码为加密后的值
                    user.setPassword(updatedUser.getPassword());
                } catch (Exception e) {
                    // 密码更新失败，但不影响登录，只记录日志
                    System.err.println("更新用户密码为加密格式失败: " + e.getMessage());
                }
            }
        }
        
        if (passwordMatch) {
            return user;
        }
        
        return null; // 密码不匹配
    }

    /**
     * 更新用户最后登录时间
     * @param user 用户对象
     */
    public void updateLastLoginTime(User user) {
        if (user != null && user.getUserId() != null) {
            user.setLastLoginTime(new Date());
            userMapper.updateLastLoginTime(user);
        }
    }

    /**
     * 获取用户总数
     * @return 用户总数
     */
    public int countTotalUsers() {
        try {
            return userMapper.countTotal();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存计数
            List<User> allUsers = userMapper.findAll();
            return allUsers.size();
        }
    }
    
    /**
     * 获取今日活跃用户数
     * @return 今日活跃用户数
     */
    public int countActiveUsers() {
        try {
            return userActionMapper.countTodayActiveUsers();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存过滤
            List<UserAction> allActions = userActionMapper.findAll();
            
            // 获取今天的日期（不包含时间）
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            // 用于记录今日活跃的用户ID
            java.util.Set<Integer> activeUsers = new java.util.HashSet<>();
            
            for (UserAction action : allActions) {
                if (action.getActionTime() != null) {
                    String actionDate = dateFormat.format(action.getActionTime());
                    if (today.equals(actionDate) && action.getUserId() != null) {
                        activeUsers.add(action.getUserId());
                    }
                }
            }
            
            return activeUsers.size();
        }
    }
} 