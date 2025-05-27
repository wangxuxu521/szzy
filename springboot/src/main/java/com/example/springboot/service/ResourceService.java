package com.example.springboot.service;

import com.example.springboot.entity.Resource;
import java.util.List;
import java.util.Map;

public interface ResourceService {
    /**
     * 根据ID查找资源
     */
    Resource findById(Integer resourceId);

    /**
     * 查找所有资源
     */
    List<Resource> findAll();

    /**
     * 分页查找资源
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 资源列表
     */
    List<Resource> findByPage(Integer page, Integer size);

    /**
     * 保存资源
     */
    void save(Resource resource);

    /**
     * 更新资源
     */
    void update(Resource resource);

    /**
     * 删除资源
     */
    void delete(Integer resourceId);
    
    /**
     * 增加资源浏览次数
     */
    void increaseViewCount(Integer resourceId);
    
    /**
     * 增加资源下载次数
     */
    void increaseDownloadCount(Integer resourceId);
    
    /**
     * 搜索资源
     * @param keyword 关键词(可为null)
     * @param type 资源类型(可为null，字符串格式，向后兼容)
     * @param typeId 资源类型ID(可为null，新版本)
     * @return 符合条件的资源列表
     */
    List<Resource> searchResources(String keyword, String type, Integer typeId);
    
    /**
     * 分页搜索资源
     * @param keyword 关键词(可为null)
     * @param type 资源类型(可为null，字符串格式)
     * @param typeId 资源类型ID(可为null)
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 符合条件的资源列表
     */
    List<Resource> searchResources(String keyword, String type, Integer typeId, Integer page, Integer size);
    
    /**
     * 统计符合条件的资源总数
     * @param keyword 关键词(可为null)
     * @param type 资源类型(可为null，字符串格式)
     * @param typeId 资源类型ID(可为null)
     * @return 符合条件的资源总数
     */
    int countResources(String keyword, String type, Integer typeId);
    
    /**
     * 根据类型ID查询资源
     * @param typeId 资源类型ID
     * @return 资源列表
     */
    List<Resource> findByTypeId(Integer typeId);
    
    /**
     * 获取热门资源
     * @param limit 返回数量限制
     * @return 热门资源列表
     */
    List<Resource> findHotResources(Integer limit);
    
    /**
     * 获取所有资源类型(字符串格式，向后兼容)
     * @return 所有不同的资源类型列表
     */
    List<String> findAllTypes();
    
    /**
     * 获取资源总数
     * @return 资源总数
     */
    int countTotalResources();
    
    /**
     * 获取今日新增资源数
     * @return 今日新增资源数
     */
    int countTodayResources();
    
    /**
     * 获取资源总下载次数
     * @return 总下载次数
     */
    int countTotalDownloads();
    
    /**
     * 获取资源类型统计(字符串格式，向后兼容)
     * @return 资源类型及对应的资源数量
     */
    Map<String, Integer> countResourceByType();
    
    /**
     * 获取资源类型统计(根据typeId)
     * @return 资源类型ID及对应的资源数量
     */
    Map<Integer, Integer> countResourceByTypeId();
    
    /**
     * 根据上传者ID查询资源
     * @param uploaderId 上传者ID
     * @return 资源列表
     */
    List<Resource> findByUploaderId(Integer uploaderId);
    
    /**
     * 统计上传者的资源数量
     * @param uploaderId 上传者ID
     * @return 资源数量
     */
    int countByUploaderId(Integer uploaderId);
    
    /**
     * 按类型统计上传者的资源数量
     * @param uploaderId 上传者ID
     * @return 各类型资源数量
     */
    Map<String, Integer> countByUploaderIdGroupByType(Integer uploaderId);
    
    /**
     * 更新资源的typeId
     * @param resourceId 资源ID
     * @param typeId 类型ID
     * @return 是否更新成功
     */
    boolean updateTypeId(Integer resourceId, Integer typeId);
    
    /**
     * 更新资源评分
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param rating 评分值
     * @return 更新后的平均评分
     */
    double updateResourceRating(Integer resourceId, Integer userId, Double rating);
} 