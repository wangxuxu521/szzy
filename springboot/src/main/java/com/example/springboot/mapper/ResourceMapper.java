package com.example.springboot.mapper;

import com.example.springboot.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper {
    Resource findById(Integer resourceId);
    List<Resource> findAll();
    
    /**
     * 分页查询资源
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 资源列表
     */
    List<Resource> findByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    void insert(Resource resource);
    void update(Resource resource);
    void delete(Integer resourceId);
    
    /**
     * 根据关键词和类型搜索资源
     * @param keyword 搜索关键词
     * @param type 资源类型（字符串类型，向后兼容）
     * @param typeId 资源类型ID（新版本）
     * @return 符合条件的资源列表
     */
    List<Resource> searchResources(@Param("keyword") String keyword, 
                                  @Param("type") String type, 
                                  @Param("typeId") Integer typeId);
    
    /**
     * 分页搜索资源
     * @param keyword 搜索关键词
     * @param type 资源类型（字符串类型，向后兼容）
     * @param typeId 资源类型ID（新版本）
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 分页后的资源列表
     */
    List<Resource> searchResourcesWithPage(@Param("keyword") String keyword, 
                                         @Param("type") String type, 
                                         @Param("typeId") Integer typeId,
                                         @Param("offset") Integer offset, 
                                         @Param("limit") Integer limit);
    
    /**
     * 统计符合条件的资源总数
     * @param keyword 搜索关键词
     * @param type 资源类型
     * @param typeId 资源类型ID
     * @return 符合条件的资源总数
     */
    int countResources(@Param("keyword") String keyword, 
                                  @Param("type") String type, 
                                  @Param("typeId") Integer typeId);
    
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
     * 根据typeId查询资源列表
     * @param typeId 类型ID
     * @return 资源列表
     */
    List<Resource> findByTypeId(Integer typeId);
    
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
     * 获取资源类型统计（字符串类型，向后兼容）
     * @return 资源类型及对应的资源数量
     */
    @Select("SELECT type, COUNT(*) as count FROM resource GROUP BY type")
    Map<String, Integer> countResourceByType();
    
    /**
     * 获取资源类型统计（根据typeId统计）
     * @return 资源类型及对应的资源数量
     */
    @Select("SELECT type_id, COUNT(*) as count FROM resource WHERE type_id IS NOT NULL GROUP BY type_id")
    Map<Integer, Integer> countResourceByTypeId();
    
    /**
     * 增加下载次数
     * @param resourceId 资源ID
     * @return 影响的行数
     */
    @Update("UPDATE resource SET download_count = download_count + 1 WHERE resource_id = #{resourceId}")
    int increaseDownloadCount(Integer resourceId);
    
    /**
     * 增加浏览次数
     * @param resourceId 资源ID
     * @return 影响的行数
     */
    @Update("UPDATE resource SET view_count = view_count + 1 WHERE resource_id = #{resourceId}")
    int increaseViewCount(Integer resourceId);
    
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
    List<Map<String, Object>> countByUploaderIdGroupByType(Integer uploaderId);
    
    /**
     * 更新资源的typeId
     * @param resourceId 资源ID
     * @param typeId 类型ID
     * @return 影响的行数
     */
    @Update("UPDATE resource SET type_id = #{typeId} WHERE resource_id = #{resourceId}")
    int updateTypeId(@Param("resourceId") Integer resourceId, @Param("typeId") Integer typeId);
} 