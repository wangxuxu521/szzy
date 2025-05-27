package com.example.springboot.controller;

import com.example.springboot.entity.Type;
import com.example.springboot.service.TypeService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    // 获取类型树结构
    @GetMapping("/tree")
    public Result getTypeTree() {
        try {
            return Result.success(typeService.getTypeTree());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取类型树失败: " + e.getMessage());
        }
    }
    
    // 获取子类型
    @GetMapping("/children/{parentId}")
    public Result getChildren(@PathVariable Integer parentId) {
        try {
            return Result.success(typeService.findChildrenByParentId(parentId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取子类型失败: " + e.getMessage());
        }
    }
    
    // 搜索类型
    @GetMapping("/search")
    public Result searchTypes(@RequestParam String keyword) {
        try {
            return Result.success(typeService.searchByName(keyword));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索类型失败: " + e.getMessage());
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
    
    // 更新类型状态
    @PutMapping("/{id}/status/{status}")
    public Result updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        try {
            Type type = typeService.findById(id);
            if (type == null) {
                return Result.error("类型不存在");
            }
            
            typeService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新类型状态失败: " + e.getMessage());
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
    
    // 批量删除类型
    @DeleteMapping("/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        try {
            typeService.batchDelete(ids);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除类型失败: " + e.getMessage());
        }
    }
} 