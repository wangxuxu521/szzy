package com.example.springboot.service;

import com.example.springboot.entity.Comment;
import java.util.List;

public interface CommentService {
    
    /**
     * 添加评论
     */
    Comment addComment(Comment comment);
    
    /**
     * 根据ID获取评论
     */
    Comment findById(Integer commentId);
    
    /**
     * 根据资源ID获取评论列表
     */
    List<Comment> findByResourceId(Integer resourceId);
    
    /**
     * 根据用户ID获取评论列表
     */
    List<Comment> findByUserId(Integer userId);
    
    /**
     * 获取评论的回复列表
     */
    List<Comment> findReplies(Integer commentId);
    
    /**
     * 删除评论
     */
    boolean deleteComment(Integer commentId);
    
    /**
     * 更新评论状态
     */
    boolean updateStatus(Integer commentId, Integer status);
    
    /**
     * 获取最新评论
     */
    List<Comment> getLatestComments(Integer limit);
    
    /**
     * 统计资源评论数
     */
    int countByResourceId(Integer resourceId);
} 