package com.example.springboot.mapper;

import com.example.springboot.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    
    /**
     * 添加评论
     */
    int insert(Comment comment);
    
    /**
     * 根据ID查询评论
     */
    Comment findById(Integer commentId);
    
    /**
     * 根据资源ID查询评论列表
     */
    List<Comment> findByResourceId(Integer resourceId);
    
    /**
     * 根据用户ID查询评论列表
     */
    List<Comment> findByUserId(Integer userId);
    
    /**
     * 根据父评论ID查询子评论列表
     */
    List<Comment> findByParentId(Integer parentId);
    
    /**
     * 删除评论
     */
    int delete(Integer commentId);
    
    /**
     * 修改评论
     */
    int update(Comment comment);
    
    /**
     * 更新评论状态
     */
    int updateStatus(@Param("commentId") Integer commentId, @Param("status") Integer status);
    
    /**
     * 查询最新评论列表
     */
    List<Comment> findLatestComments(@Param("limit") Integer limit);
    
    /**
     * 统计资源评论数量
     */
    int countByResourceId(Integer resourceId);
    
    /**
     * 统计用户评论数量
     */
    int countByUserId(Integer userId);
} 