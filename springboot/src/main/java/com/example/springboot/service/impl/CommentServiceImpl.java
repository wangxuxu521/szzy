package com.example.springboot.service.impl;

import com.example.springboot.entity.Comment;
import com.example.springboot.mapper.CommentMapper;
import com.example.springboot.mapper.ResourceMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    @Transactional
    public Comment addComment(Comment comment) {
        // 设置创建时间和默认状态
        comment.setCreateTime(new Date());
        comment.setStatus(1); // 默认状态为正常
        
        // 保存评论
        commentMapper.insert(comment);
        
        // 获取用户信息和资源信息，用于返回
        if (comment.getUserId() != null) {
            comment.setUsername(userMapper.findById(comment.getUserId()).getUsername());
        }
        
        if (comment.getResourceId() != null) {
            comment.setResourceTitle(resourceMapper.findById(comment.getResourceId()).getTitle());
        }
        
        return comment;
    }

    @Override
    public Comment findById(Integer commentId) {
        return commentMapper.findById(commentId);
    }

    @Override
    public List<Comment> findByResourceId(Integer resourceId) {
        return commentMapper.findByResourceId(resourceId);
    }

    @Override
    public List<Comment> findByUserId(Integer userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    public List<Comment> findReplies(Integer parentId) {
        return commentMapper.findByParentId(parentId);
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId) {
        // 先删除该评论的所有回复
        List<Comment> replies = commentMapper.findByParentId(commentId);
        for (Comment reply : replies) {
            commentMapper.delete(reply.getCommentId());
        }
        
        // 再删除评论本身
        return commentMapper.delete(commentId) > 0;
    }

    @Override
    public boolean updateStatus(Integer commentId, Integer status) {
        return commentMapper.updateStatus(commentId, status) > 0;
    }

    @Override
    public List<Comment> getLatestComments(Integer limit) {
        return commentMapper.findLatestComments(limit);
    }

    @Override
    public int countByResourceId(Integer resourceId) {
        return commentMapper.countByResourceId(resourceId);
    }
} 