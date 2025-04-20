package com.example.springboot.service;

import com.example.springboot.entity.Resource;
import com.example.springboot.mapper.ResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    public Resource findById(Integer resourceId) {
        return resourceMapper.findById(resourceId);
    }

    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }

    public void save(Resource resource) {
        if (resource.getResourceId() == null) {
            resourceMapper.insert(resource);
        } else {
            resourceMapper.update(resource);
        }
    }

    public void update(Resource resource) {
        resourceMapper.update(resource);
    }

    public void delete(Integer resourceId) {
        resourceMapper.delete(resourceId);
    }
    
    /**
     * 增加资源浏览次数
     */
    public void increaseViewCount(Integer resourceId) {
        Resource resource = resourceMapper.findById(resourceId);
        if (resource != null) {
            Integer viewCount = resource.getViewCount();
            resource.setViewCount(viewCount == null ? 1 : viewCount + 1);
            resourceMapper.update(resource);
        }
    }
    
    /**
     * 增加资源下载次数
     */
    public void increaseDownloadCount(Integer resourceId) {
        Resource resource = resourceMapper.findById(resourceId);
        if (resource != null) {
            Integer downloadCount = resource.getDownloadCount();
            resource.setDownloadCount(downloadCount == null ? 1 : downloadCount + 1);
            resourceMapper.update(resource);
        }
    }
    
    /**
     * 搜索资源
     * @param keyword 关键词(可为null)
     * @param type 资源类型(可为null)
     * @return 符合条件的资源列表
     */
    public List<Resource> searchResources(String keyword, String type) {
        try {
            return resourceMapper.searchResources(keyword, type);
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存过滤
            List<Resource> allResources = resourceMapper.findAll();
            List<Resource> result = new ArrayList<>();
            
            for (Resource resource : allResources) {
                // 类型筛选
                if (type != null && !type.isEmpty() && !type.equals(resource.getType())) {
                    continue;
                }
                
                // 关键词搜索
                if (keyword != null && !keyword.isEmpty()) {
                    boolean match = false;
                    
                    // 搜索标题
                    if (resource.getTitle() != null && 
                        resource.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                        match = true;
                    }
                    
                    // 搜索描述
                    if (!match && resource.getDescription() != null && 
                        resource.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                        match = true;
                    }
                    
                    // 搜索标签
                    if (!match && resource.getTags() != null && 
                        resource.getTags().toLowerCase().contains(keyword.toLowerCase())) {
                        match = true;
                    }
                    
                    if (!match) {
                        continue;
                    }
                }
                
                result.add(resource);
            }
            
            return result;
        }
    }
    
    /**
     * 获取热门资源
     * @param limit 返回数量限制
     * @return 热门资源列表
     */
    public List<Resource> findHotResources(Integer limit) {
        try {
            return resourceMapper.findHotResources(limit);
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存排序
            List<Resource> allResources = resourceMapper.findAll();
            
            // 按下载次数排序
            allResources.sort((r1, r2) -> {
                Integer count1 = r1.getDownloadCount() == null ? 0 : r1.getDownloadCount();
                Integer count2 = r2.getDownloadCount() == null ? 0 : r2.getDownloadCount();
                return count2.compareTo(count1); // 降序
            });
            
            // 截取指定数量
            if (allResources.size() > limit) {
                return allResources.subList(0, limit);
            }
            
            return allResources;
        }
    }
} 