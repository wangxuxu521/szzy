package com.example.springboot.mapper;

import com.example.springboot.entity.ResourceFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ResourceFavoriteMapper {
    
    /**
     * 添加收藏
     */
    int insert(ResourceFavorite favorite);
    
    /**
     * 取消收藏
     */
    int delete(@Param("userId") Integer userId, @Param("resourceId") Integer resourceId);
    
    /**
     * 根据ID查询收藏
     */
    ResourceFavorite findById(Integer id);
    
    /**
     * 根据用户ID和资源ID查询收藏
     */
    ResourceFavorite findByUserIdAndResourceId(@Param("userId") Integer userId, @Param("resourceId") Integer resourceId);
    
    /**
     * 根据用户ID查询收藏列表
     */
    List<ResourceFavorite> findByUserId(Integer userId);
    
    /**
     * 根据资源ID查询收藏列表
     */
    List<ResourceFavorite> findByResourceId(Integer resourceId);
    
    /**
     * 检查资源是否被收藏
     */
    boolean isFavorite(@Param("userId") Integer userId, @Param("resourceId") Integer resourceId);
    
    /**
     * 统计用户收藏数量
     */
    int countByUserId(Integer userId);
    
    /**
     * 统计资源被收藏数量
     */
    int countByResourceId(Integer resourceId);
    
    /**
     * 查询用户收藏的资源ID列表
     */
    List<Integer> findResourceIdsByUserId(Integer userId);
    
    /**
     * 根据ID删除收藏
     */
    int deleteById(Integer id);
    
    /**
     * 查询收藏了指定资源的用户ID列表
     */
    List<Integer> findUserIdsByResourceId(Integer resourceId);
} 