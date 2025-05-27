package com.example.springboot.service.impl;

import com.example.springboot.entity.ResourceRating;
import com.example.springboot.entity.Resource;
import com.example.springboot.mapper.ResourceRatingMapper;
import com.example.springboot.mapper.ResourceMapper;
import com.example.springboot.service.ResourceRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ResourceRatingServiceImpl implements ResourceRatingService {

    @Autowired
    private ResourceRatingMapper resourceRatingMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    @Transactional
    public ResourceRating rateResource(Integer resourceId, Integer userId, Double rating) {
        // 检查资源是否存在
        Resource resource = resourceMapper.findById(resourceId);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        
        // 检查用户是否已经评过分
        ResourceRating existingRating = resourceRatingMapper.findByResourceIdAndUserId(resourceId, userId);
        
        if (existingRating != null) {
            // 更新已有评分
            existingRating.setRating(rating);
            existingRating.setUpdateTime(new Date());
            resourceRatingMapper.update(existingRating);
            
            // 更新资源的平均评分和评分人数
            updateResourceRatingInfo(resourceId);
            
            return existingRating;
        } else {
            // 创建新评分记录
            ResourceRating newRating = new ResourceRating();
            newRating.setResourceId(resourceId);
            newRating.setUserId(userId);
            newRating.setRating(rating);
            newRating.setCreateTime(new Date());
            newRating.setUpdateTime(new Date());
            
            resourceRatingMapper.insert(newRating);
            
            // 更新资源的平均评分和评分人数
            updateResourceRatingInfo(resourceId);
            
            return newRating;
        }
    }

    @Override
    public ResourceRating getUserResourceRating(Integer resourceId, Integer userId) {
        return resourceRatingMapper.findByResourceIdAndUserId(resourceId, userId);
    }

    @Override
    public List<ResourceRating> getResourceRatings(Integer resourceId) {
        return resourceRatingMapper.findByResourceId(resourceId);
    }

    @Override
    public List<ResourceRating> getUserRatings(Integer userId) {
        return resourceRatingMapper.findByUserId(userId);
    }

    @Override
    public Double calculateAverageRating(Integer resourceId) {
        return resourceRatingMapper.calculateAverageRating(resourceId);
    }

    @Override
    public Integer countRatingsByResourceId(Integer resourceId) {
        return resourceRatingMapper.countRatingsByResourceId(resourceId);
    }

    @Override
    @Transactional
    public boolean deleteRating(Integer id) {
        ResourceRating rating = resourceRatingMapper.findById(id);
        if (rating != null) {
            resourceRatingMapper.delete(id);
            // 更新资源的平均评分和评分人数
            updateResourceRatingInfo(rating.getResourceId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteUserResourceRating(Integer resourceId, Integer userId) {
        int result = resourceRatingMapper.deleteByResourceIdAndUserId(resourceId, userId);
        if (result > 0) {
            // 更新资源的平均评分和评分人数
            updateResourceRatingInfo(resourceId);
            return true;
        }
        return false;
    }
    
    /**
     * 更新资源的平均评分和评分人数
     */
    private void updateResourceRatingInfo(Integer resourceId) {
        Resource resource = resourceMapper.findById(resourceId);
        if (resource != null) {
            // 计算平均评分
            Double avgRating = calculateAverageRating(resourceId);
            if (avgRating == null) {
                avgRating = 0.0;
            }
            
            // 统计评分人数
            Integer ratingCount = countRatingsByResourceId(resourceId);
            if (ratingCount == null) {
                ratingCount = 0;
            }
            
            // 更新资源信息
            resource.setRating(avgRating.floatValue());
            resource.setRatingCount(ratingCount);
            resourceMapper.update(resource);
        }
    }
} 