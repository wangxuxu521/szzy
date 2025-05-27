package com.example.springboot.service;

import com.example.springboot.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 查找所有公告
     * @param limit 限制返回数量
     * @return 公告列表
     */
    List<Announcement> findAll(Integer limit);
    
    /**
     * 根据ID查找公告
     * @param id 公告ID
     * @return 公告对象
     */
    Announcement findById(Integer id);
    
    /**
     * 保存公告
     * @param announcement 公告对象
     * @return 是否成功
     */
    boolean save(Announcement announcement);
    
    /**
     * 更新公告
     * @param announcement 公告对象
     * @return 是否成功
     */
    boolean update(Announcement announcement);
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 是否成功
     */
    boolean delete(Integer id);
} 