package com.example.springboot.controller;

import com.example.springboot.entity.ResourceFavorite;
import com.example.springboot.entity.User;
import com.example.springboot.service.ResourceFavoriteService;
import com.example.springboot.service.UserService;
import com.example.springboot.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 资源收藏控制器
 */
@RestController
@RequestMapping("/favorites")
public class ResourceFavoriteController {
    private static final Logger log = LoggerFactory.getLogger(ResourceFavoriteController.class);

    @Autowired
    private ResourceFavoriteService resourceFavoriteService;

    @Autowired
    private UserService userService;

    /**
     * 添加收藏
     */
    @PostMapping
    public Result addFavorite(@RequestBody Map<String, Integer> requestBody) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法收藏");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            Integer resourceId = requestBody.get("resourceId");
            if (resourceId == null) {
                return Result.error("资源ID不能为空");
            }

            // 添加收藏
            ResourceFavorite favorite = resourceFavoriteService.addFavorite(user.getUserId(), resourceId);
            return Result.success(favorite);
        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return Result.error("添加收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{resourceId}")
    public Result cancelFavorite(@PathVariable Integer resourceId) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法操作");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 取消收藏
            boolean success = resourceFavoriteService.cancelFavorite(user.getUserId(), resourceId);
            if (success) {
                return Result.success("取消收藏成功");
            } else {
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.error("取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserFavorites(@PathVariable Integer userId) {
        try {
            List<ResourceFavorite> favorites = resourceFavoriteService.getUserFavorites(userId);
            return Result.success(favorites);
        } catch (Exception e) {
            log.error("获取用户收藏失败", e);
            return Result.error("获取用户收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户收藏列表
     */
    @GetMapping("/current")
    public Result getCurrentUserFavorites() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法获取收藏");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            List<ResourceFavorite> favorites = resourceFavoriteService.getUserFavorites(user.getUserId());
            return Result.success(favorites);
        } catch (Exception e) {
            log.error("获取当前用户收藏失败", e);
            return Result.error("获取当前用户收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源收藏列表
     */
    @GetMapping("/resource/{resourceId}")
    public Result getResourceFavorites(@PathVariable Integer resourceId) {
        try {
            List<ResourceFavorite> favorites = resourceFavoriteService.getResourceFavorites(resourceId);
            return Result.success(favorites);
        } catch (Exception e) {
            log.error("获取资源收藏失败", e);
            return Result.error("获取资源收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检查资源是否被当前用户收藏
     */
    @GetMapping("/check")
    public Result checkFavorite(@RequestParam Integer resourceId) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.success(false); // 未登录默认未收藏
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.success(false); // 用户不存在默认未收藏
            }

            boolean isFavorite = resourceFavoriteService.isFavorite(user.getUserId(), resourceId);
            return Result.success(isFavorite);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return Result.error("检查收藏状态失败: " + e.getMessage());
        }
    }

    /**
     * 统计用户收藏数量
     */
    @GetMapping("/count/user/{userId}")
    public Result countUserFavorites(@PathVariable Integer userId) {
        try {
            int count = resourceFavoriteService.countUserFavorites(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户收藏数量失败", e);
            return Result.error("统计用户收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 统计资源被收藏数量
     */
    @GetMapping("/count/resource/{resourceId}")
    public Result countResourceFavorites(@PathVariable Integer resourceId) {
        try {
            int count = resourceFavoriteService.countResourceFavorites(resourceId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计资源收藏数量失败", e);
            return Result.error("统计资源收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 批量取消收藏
     */
    @DeleteMapping("/batch")
    public Result batchCancelFavorites(@RequestBody Map<String, List<Integer>> requestBody) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法操作");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            List<Integer> favoriteIds = requestBody.get("favoriteIds");
            if (favoriteIds == null || favoriteIds.isEmpty()) {
                return Result.error("收藏ID列表不能为空");
            }

            // 实现批量取消收藏的逻辑
            // 这里简化处理，调用现有的取消收藏方法
            int successCount = 0;
            for (Integer favoriteId : favoriteIds) {
                if (resourceFavoriteService.cancelFavoriteById(favoriteId, user.getUserId())) {
                    successCount++;
                }
            }

            return Result.success("成功取消" + successCount + "个收藏");
        } catch (Exception e) {
            log.error("批量取消收藏失败", e);
            return Result.error("批量取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源的收藏用户列表
     */
    @GetMapping("/users/{resourceId}")
    public Result getResourceFavoriteUsers(@PathVariable Integer resourceId) {
        try {
            List<User> users = resourceFavoriteService.getResourceFavoriteUsers(resourceId);
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取资源收藏用户列表失败", e);
            return Result.error("获取资源收藏用户列表失败: " + e.getMessage());
        }
    }
} 