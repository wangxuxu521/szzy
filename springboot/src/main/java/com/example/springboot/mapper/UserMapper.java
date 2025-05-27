package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    
    /**
     * 更新用户最后登录时间
     */
    void updateLastLoginTime(User user);
    
    /**
     * 更新用户密码
     */
    @Update("UPDATE user SET password = #{password} WHERE user_id = #{userId}")
    void updatePassword(User user);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    int countTotal();
    
    /**
     * 根据ID列表查询用户
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    List<User> findByIds(@Param("userIds") List<Integer> userIds);
} 