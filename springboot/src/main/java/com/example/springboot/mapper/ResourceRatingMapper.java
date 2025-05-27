package com.example.springboot.mapper;

import com.example.springboot.entity.ResourceRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ResourceRatingMapper {
    
    /**
     * 保存评分记录
     */
    int insert(ResourceRating rating);
    
    /**
     * 更新评分记录
     */
    int update(ResourceRating rating);
    
    /**
     * 根据ID查询评分记录
     */
    ResourceRating findById(Integer id);
    
    /**
     * 根据资源ID和用户ID查询评分记录
     */
    ResourceRating findByResourceIdAndUserId(@Param("resourceId") Integer resourceId, @Param("userId") Integer userId);
    
    /**
     * 根据资源ID查询所有评分记录
     */
    List<ResourceRating> findByResourceId(Integer resourceId);
    
    /**
     * 根据用户ID查询所有评分记录
     */
    List<ResourceRating> findByUserId(Integer userId);
    
    /**
     * 计算资源的平均评分
     */
    Double calculateAverageRating(Integer resourceId);
    
    /**
     * 统计资源的评分人数
     */
    Integer countRatingsByResourceId(Integer resourceId);
    
    /**
     * 删除评分记录
     */
    int delete(Integer id);
    
    /**
     * 根据资源ID和用户ID删除评分记录
     */
    int deleteByResourceIdAndUserId(@Param("resourceId") Integer resourceId, @Param("userId") Integer userId);
} 