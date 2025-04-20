package com.example.springboot.service;

import com.example.springboot.entity.UserAction;
import com.example.springboot.mapper.UserActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActionService {
    @Autowired
    private UserActionMapper userActionMapper;

    public UserAction findById(Integer actionId) {
        return userActionMapper.findById(actionId);
    }

    public List<UserAction> findAll() {
        return userActionMapper.findAll();
    }
    
    public List<UserAction> findByUserId(Integer userId) {
        return userActionMapper.findByUserId(userId);
    }
    
    public List<UserAction> findByResourceId(Integer resourceId) {
        return userActionMapper.findByResourceId(resourceId);
    }
    
    public List<UserAction> findByActionType(String actionType) {
        return userActionMapper.findByActionType(actionType);
    }

    public void save(UserAction userAction) {
        if (userAction.getActionId() == null) {
            userActionMapper.insert(userAction);
        } else {
            userActionMapper.update(userAction);
        }
    }

    public void delete(Integer actionId) {
        userActionMapper.delete(actionId);
    }
} 