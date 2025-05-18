package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.UserAction;
import com.example.springboot.service.UserActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-actions")
public class UserActionController {
    @Autowired
    private UserActionService userActionService;

    @GetMapping("/{id}")
    public UserAction findById(@PathVariable Integer id) {
        return userActionService.findById(id);
    }

    @GetMapping
    public List<UserAction> findAll() {
        return userActionService.findAll();
    }
    
    @GetMapping("/user/{userId}")
    public Result<?> findByUserId(@PathVariable Integer userId) {
        List<UserAction> actions = userActionService.findByUserId(userId);
        return Result.success(actions);
    }
    
    @GetMapping("/resource/{resourceId}")
    public Result<?> findByResourceId(@PathVariable Integer resourceId) {
        List<UserAction> actions = userActionService.findByResourceId(resourceId);
        return Result.success(actions);
    }
    
    @GetMapping("/type/{actionType}")
    public List<UserAction> findByActionType(@PathVariable String actionType) {
        return userActionService.findByActionType(actionType);
    }

    @PostMapping
    public Result<?> save(@RequestBody UserAction userAction) {
        userActionService.save(userAction);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userActionService.delete(id);
    }
} 