package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据ID查找用户
     */
    User findById(Integer userId);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 根据角色查找用户
     */
    List<User> findByRole(@Param("role") String role);
    
    /**
     * 查找所有用户
     */
    List<User> findAll();
    
    /**
     * 添加用户
     */
    void insert(User user);
    
    /**
     * 更新用户
     */
    void update(User user);
    
    /**
     * 删除用户
     */
    void delete(Integer userId);
} 