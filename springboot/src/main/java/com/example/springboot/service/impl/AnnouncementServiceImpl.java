package com.example.springboot.service.impl;

import com.example.springboot.entity.Announcement;
import com.example.springboot.mapper.AnnouncementMapper;
import com.example.springboot.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Override
    public List<Announcement> findAll(Integer limit) {
        return announcementMapper.findAll(limit);
    }
    
    @Override
    public Announcement findById(Integer id) {
        return announcementMapper.findById(id);
    }
    
    @Override
    public boolean save(Announcement announcement) {
        // 设置创建时间等
        Date now = new Date();
        announcement.setCreateTime(now);
        announcement.setUpdateTime(now);
        
        // 如果是已发布状态，设置发布时间
        if (announcement.getStatus() != null && announcement.getStatus() == 1) {
            announcement.setPublishTime(now);
        }
        
        return announcementMapper.insert(announcement) > 0;
    }
    
    @Override
    public boolean update(Announcement announcement) {
        // 更新时间
        announcement.setUpdateTime(new Date());
        
        // 如果状态从未发布变为已发布，设置发布时间
        Announcement old = announcementMapper.findById(announcement.getId());
        if (old != null && (old.getStatus() == null || old.getStatus() == 0) 
                && announcement.getStatus() != null && announcement.getStatus() == 1) {
            announcement.setPublishTime(new Date());
        }
        
        return announcementMapper.update(announcement) > 0;
    }
    
    @Override
    public boolean delete(Integer id) {
        return announcementMapper.delete(id) > 0;
    }
} 