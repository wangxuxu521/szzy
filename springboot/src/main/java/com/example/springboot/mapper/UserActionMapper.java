package com.example.springboot.mapper;

import com.example.springboot.entity.UserAction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserActionMapper {
    UserAction findById(Integer actionId);
    List<UserAction> findAll();
    List<UserAction> findByUserId(Integer userId);
    List<UserAction> findByResourceId(Integer resourceId);
    List<UserAction> findByActionType(String actionType);
    void insert(UserAction userAction);
    void update(UserAction userAction);
    void delete(Integer actionId);
    
    /**
     * 获取今日活跃用户数（有操作的用户）
     * @return 今日活跃用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM user_action WHERE DATE(action_time) = CURDATE()")
    int countTodayActiveUsers();
} 