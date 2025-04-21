package com.example.springboot.controller;

import com.example.springboot.entity.Type;
import com.example.springboot.service.TypeService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/types")
public class TypeController {
    @Autowired
    private TypeService typeService;
    
    // 获取类型详情
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        try {
            Type type = typeService.findById(id);
            if (type != null) {
                return Result.success(type);
            } else {
                return Result.error("类型不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取类型详情失败: " + e.getMessage());
        }
    }
    
    // 获取所有类型
    @GetMapping
    public Result findAll() {
        try {
            return Result.success(typeService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取类型列表失败: " + e.getMessage());
        }
    }
    
    // 添加或更新类型
    @PostMapping
    public Result save(@RequestBody Type type) {
        try {
            // 检查类型名称是否已存在
            if (type.getTypeId() == null && typeService.checkTypeNameExists(type.getTypeName())) {
                return Result.error("类型名称已存在");
            }
            
            typeService.save(type);
            return Result.success(type);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存类型失败: " + e.getMessage());
        }
    }
    
    // 删除类型
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            Type type = typeService.findById(id);
            if (type == null) {
                return Result.error("类型不存在");
            }
            
            typeService.delete(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除类型失败: " + e.getMessage());
        }
    }
} 