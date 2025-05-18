package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Resource;
import com.example.springboot.entity.UserAction;
import com.example.springboot.service.ResourceService;
import com.example.springboot.service.UserActionService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserActionService userActionService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取系统摘要统计数据
     */
    @GetMapping("/summary")
    public Result getSystemSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // 获取资源总数
        int totalResources = resourceService.countTotalResources();
        summary.put("totalResources", totalResources);
        
        // 获取今日新增资源数
        int todayResources = resourceService.countTodayResources();
        summary.put("todayResources", todayResources);
        
        // 获取用户总数
        int totalUsers = userService.countTotalUsers();
        summary.put("totalUsers", totalUsers);
        
        // 获取今日活跃用户数
        int activeUsers = userService.countActiveUsers();
        summary.put("activeUsers", activeUsers);
        
        // 获取总下载次数
        int totalDownloads = resourceService.countTotalDownloads();
        summary.put("totalDownloads", totalDownloads);
        
        return Result.success(summary);
    }
    
    /**
     * 获取资源类型统计
     */
    @GetMapping("/resource-type-count")
    public Result getResourceTypeCount() {
        Map<String, Integer> typeCounts = resourceService.countResourceByType();
        return Result.success(typeCounts);
    }
    
    @GetMapping("/resource-count")
    public Result getResourceCount() {
        // 资源总数统计
        List<Resource> resources = resourceService.findAll();
        
        // 按审核状态统计
        Map<String, Long> statusCount = resources.stream()
                .collect(Collectors.groupingBy(Resource::getReviewStatus, Collectors.counting()));
        
        // 按资源类型统计
        Map<String, Long> typeCount = resources.stream()
                .filter(r -> r.getType() != null)
                .collect(Collectors.groupingBy(Resource::getType, Collectors.counting()));
        
        // 按资源格式统计
        Map<String, Long> formatCount = resources.stream()
                .collect(Collectors.groupingBy(Resource::getFormat, Collectors.counting()));
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", resources.size());
        result.put("statusCount", statusCount);
        result.put("typeCount", typeCount);
        result.put("formatCount", formatCount);
        
        return Result.success(result);
    }
    
    @GetMapping("/resource-trend")
    public Result getResourceTrend(@RequestParam(required = false) Integer days) {
        if (days == null) {
            days = 30; // 默认30天
        }
        
        List<Resource> resources = resourceService.findAll();
        
        // 获取指定天数前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();
        
        // 按上传日期分组统计
        Map<String, Long> uploadTrend = new LinkedHashMap<>();
        
        // 初始化日期范围
        for (int i = 0; i < days; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = String.format("%d-%02d-%02d", 
                    cal.get(Calendar.YEAR), 
                    cal.get(Calendar.MONTH) + 1, 
                    cal.get(Calendar.DAY_OF_MONTH));
            uploadTrend.put(dateStr, 0L);
        }
        
        // 填充实际数据
        for (Resource resource : resources) {
            if (resource.getUploadTime().after(startDate)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(resource.getUploadTime());
                String dateStr = String.format("%d-%02d-%02d", 
                        cal.get(Calendar.YEAR), 
                        cal.get(Calendar.MONTH) + 1, 
                        cal.get(Calendar.DAY_OF_MONTH));
                
                if (uploadTrend.containsKey(dateStr)) {
                    uploadTrend.put(dateStr, uploadTrend.get(dateStr) + 1);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("uploadTrend", uploadTrend);
        
        return Result.success(result);
    }
    
    @GetMapping("/user-actions")
    public Result getUserActions() {
        List<UserAction> actions = userActionService.findAll();
        
        // 按操作类型统计
        Map<String, Long> actionTypeCount = actions.stream()
                .collect(Collectors.groupingBy(UserAction::getActionType, Collectors.counting()));
        
        // 最活跃用户TOP10
        Map<Integer, Long> userActionCount = actions.stream()
                .collect(Collectors.groupingBy(UserAction::getUserId, Collectors.counting()));
        
        List<Map.Entry<Integer, Long>> topUsers = userActionCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        // 最热门资源TOP10
        Map<Integer, Long> resourceActionCount = actions.stream()
                .collect(Collectors.groupingBy(UserAction::getResourceId, Collectors.counting()));
        
        List<Map.Entry<Integer, Long>> topResources = resourceActionCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalActions", actions.size());
        result.put("actionTypeCount", actionTypeCount);
        result.put("topUsers", topUsers);
        result.put("topResources", topResources);
        
        return Result.success(result);
    }
    
    @GetMapping("/download-statistics")
    public Result getDownloadStatistics() {
        List<UserAction> downloadActions = userActionService.findByActionType("download");
        
        // 下载总次数
        long totalDownloads = downloadActions.size();
        
        // 按天统计下载次数
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30); // 最近30天
        Date startDate = calendar.getTime();
        
        Map<String, Long> dailyDownloads = new LinkedHashMap<>();
        
        // 初始化日期范围
        for (int i = 0; i < 30; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = String.format("%d-%02d-%02d", 
                    cal.get(Calendar.YEAR), 
                    cal.get(Calendar.MONTH) + 1, 
                    cal.get(Calendar.DAY_OF_MONTH));
            dailyDownloads.put(dateStr, 0L);
        }
        
        // 填充实际数据
        for (UserAction action : downloadActions) {
            if (action.getActionTime().after(startDate)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(action.getActionTime());
                String dateStr = String.format("%d-%02d-%02d", 
                        cal.get(Calendar.YEAR), 
                        cal.get(Calendar.MONTH) + 1, 
                        cal.get(Calendar.DAY_OF_MONTH));
                
                if (dailyDownloads.containsKey(dateStr)) {
                    dailyDownloads.put(dateStr, dailyDownloads.get(dateStr) + 1);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalDownloads", totalDownloads);
        result.put("dailyDownloads", dailyDownloads);
        
        return Result.success(result);
    }
} 