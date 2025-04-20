package com.example.springboot.mapper;

import com.example.springboot.entity.UserAction;
import org.apache.ibatis.annotations.Mapper;
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
} 