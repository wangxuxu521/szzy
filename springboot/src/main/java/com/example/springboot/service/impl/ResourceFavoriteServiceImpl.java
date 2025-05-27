package com.example.springboot.service.impl;

import com.example.springboot.entity.ResourceFavorite;
import com.example.springboot.entity.Resource;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.ResourceFavoriteMapper;
import com.example.springboot.mapper.ResourceMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.ResourceFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ResourceFavoriteServiceImpl implements ResourceFavoriteService {

    @Autowired
    private ResourceFavoriteMapper resourceFavoriteMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ResourceFavorite addFavorite(Integer userId, Integer resourceId) {
        // 检查是否已收藏
        if (isFavorite(userId, resourceId)) {
            // 已收藏，直接返回
            return resourceFavoriteMapper.findByUserIdAndResourceId(userId, resourceId);
        }
        
        // 创建收藏记录
        ResourceFavorite favorite = new ResourceFavorite();
        favorite.setUserId(userId);
        favorite.setResourceId(resourceId);
        favorite.setCreateTime(new Date());
        
        // 保存收藏
        resourceFavoriteMapper.insert(favorite);
        
        // 查询资源和用户信息，用于展示
        Resource resource = resourceMapper.findById(resourceId);
        User user = userMapper.findById(userId);
        
        if (resource != null) {
            favorite.setResourceTitle(resource.getTitle());
            favorite.setResourceFormat(resource.getFormat());
            favorite.setResourceType(resource.getType());
        }
        
        if (user != null) {
            favorite.setUsername(user.getUsername());
        }
        
        return favorite;
    }

    @Override
    @Transactional
    public boolean cancelFavorite(Integer userId, Integer resourceId) {
        return resourceFavoriteMapper.delete(userId, resourceId) > 0;
    }

    @Override
    public List<ResourceFavorite> getUserFavorites(Integer userId) {
        return resourceFavoriteMapper.findByUserId(userId);
    }

    @Override
    public List<ResourceFavorite> getResourceFavorites(Integer resourceId) {
        return resourceFavoriteMapper.findByResourceId(resourceId);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer resourceId) {
        return resourceFavoriteMapper.isFavorite(userId, resourceId);
    }

    @Override
    public int countUserFavorites(Integer userId) {
        return resourceFavoriteMapper.countByUserId(userId);
    }

    @Override
    public int countResourceFavorites(Integer resourceId) {
        return resourceFavoriteMapper.countByResourceId(resourceId);
    }

    @Override
    public List<Integer> getUserFavoriteResourceIds(Integer userId) {
        return resourceFavoriteMapper.findResourceIdsByUserId(userId);
    }

    @Override
    @Transactional
    public boolean cancelFavoriteById(Integer favoriteId, Integer userId) {
        // 首先获取收藏记录，验证是否属于当前用户
        ResourceFavorite favorite = resourceFavoriteMapper.findById(favoriteId);
        
        // 如果收藏不存在或不属于当前用户，返回失败
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            return false;
        }
        
        // 执行删除操作
        return resourceFavoriteMapper.deleteById(favoriteId) > 0;
    }
    
    @Override
    public List<User> getResourceFavoriteUsers(Integer resourceId) {
        // 获取收藏该资源的用户ID列表
        List<Integer> userIds = resourceFavoriteMapper.findUserIdsByResourceId(resourceId);
        
        // 查询这些用户的详细信息
        return userMapper.findByIds(userIds);
    }
} 