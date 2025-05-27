package com.example.springboot.controller;

import com.example.springboot.entity.Comment;
import com.example.springboot.entity.User;
import com.example.springboot.service.CommentService;
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
 * 评论管理控制器
 */
@RestController
@RequestMapping("/comments")
public class CommentController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * 添加评论
     */
    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法评论");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 设置评论用户ID
            comment.setUserId(user.getUserId());

            // 保存评论
            Comment savedComment = commentService.addComment(comment);
            return Result.success(savedComment);
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return Result.error("添加评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取资源的评论列表
     */
    @GetMapping("/resource/{resourceId}")
    public Result getResourceComments(@PathVariable Integer resourceId) {
        try {
            List<Comment> comments = commentService.findByResourceId(resourceId);
            return Result.success(comments);
        } catch (Exception e) {
            log.error("获取资源评论失败", e);
            return Result.error("获取资源评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的评论列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserComments(@PathVariable Integer userId) {
        try {
            List<Comment> comments = commentService.findByUserId(userId);
            return Result.success(comments);
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
            return Result.error("获取用户评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/replies/{parentId}")
    public Result getCommentReplies(@PathVariable Integer parentId) {
        try {
            List<Comment> replies = commentService.findReplies(parentId);
            return Result.success(replies);
        } catch (Exception e) {
            log.error("获取评论回复失败", e);
            return Result.error("获取评论回复失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable Integer commentId) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法删除评论");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 获取评论信息
            Comment comment = commentService.findById(commentId);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            // 检查权限：只有评论作者或管理员可以删除评论
            if (!comment.getUserId().equals(user.getUserId()) && !"admin".equals(user.getRole())) {
                return Result.error("无权限删除此评论");
            }

            boolean success = commentService.deleteComment(commentId);
            if (success) {
                return Result.success("评论已删除");
            } else {
                return Result.error("删除评论失败");
            }
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 更新评论状态 (仅管理员可用)
     */
    @PutMapping("/{commentId}/status")
    public Result updateCommentStatus(@PathVariable Integer commentId, @RequestBody Map<String, Integer> statusMap) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法操作");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null || !"admin".equals(user.getRole())) {
                return Result.error("无权限执行此操作");
            }

            Integer status = statusMap.get("status");
            if (status == null) {
                return Result.error("状态值不能为空");
            }

            boolean success = commentService.updateStatus(commentId, status);
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新评论状态失败", e);
            return Result.error("更新评论状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论数量
     */
    @GetMapping("/count/{resourceId}")
    public Result getCommentCount(@PathVariable Integer resourceId) {
        try {
            int count = commentService.countByResourceId(resourceId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取评论数量失败", e);
            return Result.error("获取评论数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新评论列表 (用于首页展示)
     */
    @GetMapping("/latest")
    public Result getLatestComments(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Comment> comments = commentService.getLatestComments(limit);
            return Result.success(comments);
        } catch (Exception e) {
            log.error("获取最新评论失败", e);
            return Result.error("获取最新评论失败: " + e.getMessage());
        }
    }
} 