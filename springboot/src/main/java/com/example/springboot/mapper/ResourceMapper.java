package com.example.springboot.mapper;

import com.example.springboot.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper {
    Resource findById(Integer resourceId);
    List<Resource> findAll();
    void insert(Resource resource);
    void update(Resource resource);
    void delete(Integer resourceId);
    
    /**
     * 根据关键词和类型搜索资源
     * @param keyword 搜索关键词
     * @param type 资源类型
     * @return 符合条件的资源列表
     */
    List<Resource> searchResources(@Param("keyword") String keyword, @Param("type") String type);
    
    /**
     * 获取热门资源列表(按下载量降序排序)
     * @param limit 返回数量限制
     * @return 热门资源列表
     */
    List<Resource> findHotResources(@Param("limit") Integer limit);
    
    /**
     * 获取所有不同的资源类型
     * @return 资源类型列表
     */
    List<String> findAllTypes();
    
    /**
     * 获取资源总数
     * @return 资源总数
     */
    @Select("SELECT COUNT(*) FROM resource")
    int countTotal();
    
    /**
     * 获取今日新增资源数
     * @return 今日新增资源数
     */
    @Select("SELECT COUNT(*) FROM resource WHERE DATE(upload_time) = CURDATE()")
    int countTodayResources();
    
    /**
     * 获取资源总下载次数
     * @return 总下载次数
     */
    @Select("SELECT COALESCE(SUM(download_count), 0) FROM resource")
    int countTotalDownloads();
    
    /**
     * 获取资源类型统计
     * @return 资源类型及对应的资源数量
     */
    @Select("SELECT type, COUNT(*) as count FROM resource GROUP BY type")
    Map<String, Integer> countResourceByType();
} 