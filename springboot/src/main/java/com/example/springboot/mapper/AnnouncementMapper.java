package com.example.springboot.mapper;

import com.example.springboot.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper {
    
    /**
     * 查询所有公告
     * @param limit 限制返回数量
     * @return 公告列表
     */
    @Select({
        "<script>",
        "SELECT * FROM announcement",
        "WHERE status = 1", // 只查询已发布的公告
        "ORDER BY priority DESC, publish_time DESC",
        "<if test='limit != null'>",
        "LIMIT #{limit}",
        "</if>",
        "</script>"
    })
    List<Announcement> findAll(@Param("limit") Integer limit);
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告对象
     */
    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Announcement findById(@Param("id") Integer id);
    
    /**
     * 插入公告
     * @param announcement 公告对象
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO announcement",
        "(title, content, type, priority, publisher, publish_time, create_time, update_time, status)",
        "VALUES",
        "(#{title}, #{content}, #{type}, #{priority}, #{publisher}, #{publishTime}, #{createTime}, #{updateTime}, #{status})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);
    
    /**
     * 更新公告
     * @param announcement 公告对象
     * @return 影响行数
     */
    @Update({
        "<script>",
        "UPDATE announcement",
        "<set>",
        "<if test='title != null'>title = #{title},</if>",
        "<if test='content != null'>content = #{content},</if>",
        "<if test='type != null'>type = #{type},</if>",
        "<if test='priority != null'>priority = #{priority},</if>",
        "<if test='publisher != null'>publisher = #{publisher},</if>",
        "<if test='publishTime != null'>publish_time = #{publishTime},</if>",
        "<if test='updateTime != null'>update_time = #{updateTime},</if>",
        "<if test='status != null'>status = #{status},</if>",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(Announcement announcement);
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 影响行数
     */
    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int delete(@Param("id") Integer id);
} 