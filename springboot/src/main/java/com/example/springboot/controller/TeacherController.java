package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.Resource;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserAction;
import com.example.springboot.service.CourseService;
import com.example.springboot.service.ResourceService;
import com.example.springboot.service.UserActionService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserActionService userActionService;

    /**
     * 获取当前登录教师信息
     */
    @GetMapping("/info")
    public Result getTeacherInfo() {
        try {
            // 获取当前登录用户的认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User teacher = userService.findByUsername(username);

            if (teacher != null && "teacher".equals(teacher.getRole())) {
                return Result.success(teacher);
            } else {
                return Result.error("用户不存在或不是教师角色");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/info")
    public Result updateTeacherInfo(@RequestBody User teacher) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username);

            if (currentUser == null || !"teacher".equals(currentUser.getRole())) {
                return Result.error("用户不存在或不是教师角色");
            }

            // 确保只能修改自己的信息
            teacher.setUserId(currentUser.getUserId());
            teacher.setRole("teacher"); // 保持角色不变
            teacher.setUsername(currentUser.getUsername()); // 用户名不可修改

            userService.save(teacher);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新教师信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师上传的资源列表
     */
    @GetMapping("/resources")
    public Result getTeacherResources() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User teacher = userService.findByUsername(username);

            if (teacher == null) {
                return Result.error("用户不存在");
            }

            // 查询该教师上传的所有资源
            List<Resource> resources = resourceService.findByUploaderId(teacher.getUserId());
            return Result.success(resources);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师资源列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/courses")
    public Result getTeacherCourses() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User teacher = userService.findByUsername(username);

            if (teacher == null) {
                return Result.error("用户不存在");
            }

            // 查询该教师创建的所有课程
            List<Course> courses = courseService.findByTeacherId(teacher.getUserId());
            return Result.success(courses);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师活动记录
     */
    @GetMapping("/activities")
    public Result getTeacherActivities(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User teacher = userService.findByUsername(username);

            if (teacher == null) {
                return Result.error("用户不存在");
            }

            // 查询该教师的活动记录
            List<UserAction> activities = userActionService.findByUserId(teacher.getUserId(), limit);
            return Result.success(activities);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师活动记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师统计数据
     */
    @GetMapping("/statistics")
    public Result getTeacherStatistics() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User teacher = userService.findByUsername(username);

            if (teacher == null) {
                return Result.error("用户不存在");
            }

            // 查询统计数据
            int resourceCount = resourceService.countByUploaderId(teacher.getUserId());
            int courseCount = courseService.countByTeacherId(teacher.getUserId());
            
            // 统计不同类型的资源数量
            Map<String, Integer> resourceTypeCount = resourceService.countByUploaderIdGroupByType(teacher.getUserId());
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("resourceCount", resourceCount);
            statistics.put("courseCount", courseCount);
            statistics.put("resourceTypeCount", resourceTypeCount);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取教师统计数据失败: " + e.getMessage());
        }
    }
} 