package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Announcement;
import com.example.springboot.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 获取所有公告
     */
    @GetMapping
    public Result getAllAnnouncements(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Announcement> announcements = announcementService.findAll(limit);
        return Result.success(announcements);
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result getAnnouncementById(@PathVariable Integer id) {
        Announcement announcement = announcementService.findById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }
    
    /**
     * 创建公告(需要管理员权限)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result createAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.save(announcement);
        if (success) {
            return Result.success();
        } else {
            return Result.error("创建公告失败");
        }
    }
    
    /**
     * 更新公告(需要管理员权限)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result updateAnnouncement(
            @PathVariable Integer id, 
            @RequestBody Announcement announcement) {
        
        announcement.setId(id);
        boolean success = announcementService.update(announcement);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新公告失败");
        }
    }
    
    /**
     * 删除公告(需要管理员权限)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result deleteAnnouncement(@PathVariable Integer id) {
        boolean success = announcementService.delete(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除公告失败");
        }
    }
} 