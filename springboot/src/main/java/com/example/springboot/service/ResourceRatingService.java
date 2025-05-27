package com.example.springboot.service;

import com.example.springboot.entity.ResourceRating;
import java.util.List;

public interface ResourceRatingService {
    
    /**
     * 给资源评分
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param rating 评分值
     * @return 评分记录
     */
    ResourceRating rateResource(Integer resourceId, Integer userId, Double rating);
    
    /**
     * 获取用户对资源的评分
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 评分记录，如果不存在返回null
     */
    ResourceRating getUserResourceRating(Integer resourceId, Integer userId);
    
    /**
     * 获取资源的所有评分记录
     * @param resourceId 资源ID
     * @return 评分记录列表
     */
    List<ResourceRating> getResourceRatings(Integer resourceId);
    
    /**
     * 获取用户的所有评分记录
     * @param userId 用户ID
     * @return 评分记录列表
     */
    List<ResourceRating> getUserRatings(Integer userId);
    
    /**
     * 计算资源的平均评分
     * @param resourceId 资源ID
     * @return 平均评分
     */
    Double calculateAverageRating(Integer resourceId);
    
    /**
     * 统计资源的评分人数
     * @param resourceId 资源ID
     * @return 评分人数
     */
    Integer countRatingsByResourceId(Integer resourceId);
    
    /**
     * 删除评分记录
     * @param id 评分记录ID
     * @return 是否成功删除
     */
    boolean deleteRating(Integer id);
    
    /**
     * 删除用户对资源的评分记录
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 是否成功删除
     */
    boolean deleteUserResourceRating(Integer resourceId, Integer userId);
} 