package com.example.springboot.service;

import com.example.springboot.entity.ResourceFavorite;
import com.example.springboot.entity.User;
import java.util.List;

public interface ResourceFavoriteService {
    
    /**
     * 添加收藏
     */
    ResourceFavorite addFavorite(Integer userId, Integer resourceId);
    
    /**
     * 取消收藏
     */
    boolean cancelFavorite(Integer userId, Integer resourceId);
    
    /**
     * 获取用户收藏列表
     */
    List<ResourceFavorite> getUserFavorites(Integer userId);
    
    /**
     * 获取资源收藏列表
     */
    List<ResourceFavorite> getResourceFavorites(Integer resourceId);
    
    /**
     * 检查资源是否被用户收藏
     */
    boolean isFavorite(Integer userId, Integer resourceId);
    
    /**
     * 统计用户收藏数量
     */
    int countUserFavorites(Integer userId);
    
    /**
     * 统计资源被收藏数量
     */
    int countResourceFavorites(Integer resourceId);
    
    /**
     * 获取用户收藏的资源ID列表
     */
    List<Integer> getUserFavoriteResourceIds(Integer userId);
    
    /**
     * 根据收藏ID取消收藏
     * @param favoriteId 收藏ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功取消
     */
    boolean cancelFavoriteById(Integer favoriteId, Integer userId);
    
    /**
     * 获取收藏该资源的用户列表
     * @param resourceId 资源ID
     * @return 用户列表
     */
    List<User> getResourceFavoriteUsers(Integer resourceId);
} 