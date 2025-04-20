package com.example.springboot.controller;

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
    public List<UserAction> findByUserId(@PathVariable Integer userId) {
        return userActionService.findByUserId(userId);
    }
    
    @GetMapping("/resource/{resourceId}")
    public List<UserAction> findByResourceId(@PathVariable Integer resourceId) {
        return userActionService.findByResourceId(resourceId);
    }
    
    @GetMapping("/type/{actionType}")
    public List<UserAction> findByActionType(@PathVariable String actionType) {
        return userActionService.findByActionType(actionType);
    }

    @PostMapping
    public void save(@RequestBody UserAction userAction) {
        userActionService.save(userAction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userActionService.delete(id);
    }
} 