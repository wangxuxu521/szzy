package com.example.springboot.service.impl;

import com.example.springboot.entity.Resource;
import com.example.springboot.mapper.ResourceMapper;
import com.example.springboot.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Resource findById(Integer resourceId) {
        return resourceMapper.findById(resourceId);
    }

    @Override
    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }
    
    @Override
    public List<Resource> findByPage(Integer page, Integer size) {
        try {
            // 计算起始位置，页码从1开始
            int offset = (page - 1) * size;
            return resourceMapper.findByPage(offset, size);
        } catch (Exception e) {
            // 如果数据库方法失败，使用内存分页
            List<Resource> allResources = resourceMapper.findAll();
            int offset = (page - 1) * size;
            
            // 计算结束索引，确保不超出列表范围
            int toIndex = Math.min(offset + size, allResources.size());
            
            // 确保起始索引不超出列表范围
            if (offset >= allResources.size() || offset < 0) {
                return new ArrayList<>();
            }
            
            return allResources.subList(offset, toIndex);
        }
    }

    @Override
    @Transactional
    public void save(Resource resource) {
        if (resource.getResourceId() == null) {
            resourceMapper.insert(resource);
        } else {
            resourceMapper.update(resource);
        }
    }

    @Override
    @Transactional
    public void update(Resource resource) {
        resourceMapper.update(resource);
    }

    @Override
    @Transactional
    public void delete(Integer resourceId) {
        resourceMapper.delete(resourceId);
    }
    
    @Override
    @Transactional
    public void increaseViewCount(Integer resourceId) {
        try {
            resourceMapper.increaseViewCount(resourceId);
        } catch (Exception e) {
            // 如果直接更新失败，尝试读取后更新
            Resource resource = resourceMapper.findById(resourceId);
            if (resource != null) {
                Integer viewCount = resource.getViewCount();
                resource.setViewCount(viewCount == null ? 1 : viewCount + 1);
                resourceMapper.update(resource);
            }
        }
    }
    
    @Override
    @Transactional
    public void increaseDownloadCount(Integer resourceId) {
        try {
            resourceMapper.increaseDownloadCount(resourceId);
        } catch (Exception e) {
            // 如果直接更新失败，尝试读取后更新
            Resource resource = resourceMapper.findById(resourceId);
            if (resource != null) {
                Integer downloadCount = resource.getDownloadCount();
                resource.setDownloadCount(downloadCount == null ? 1 : downloadCount + 1);
                resourceMapper.update(resource);
            }
        }
    }
    
    @Override
    public List<Resource> searchResources(String keyword, String type, Integer typeId) {
        try {
            return resourceMapper.searchResources(keyword, type, typeId);
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存过滤
            List<Resource> allResources = resourceMapper.findAll();
            List<Resource> result = new ArrayList<>();
            
            for (Resource resource : allResources) {
                // 类型筛选 - 优先使用typeId
                if (typeId != null && resource.getTypeId() != null && !typeId.equals(resource.getTypeId())) {
                    continue;
                }
                
                // 字符串类型筛选 - 向后兼容
                if (typeId == null && type != null && !type.isEmpty() && !type.equals(resource.getType())) {
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
    
    @Override
    public List<Resource> searchResources(String keyword, String type, Integer typeId, Integer page, Integer size) {
        try {
            // 计算起始位置，页码从1开始
            int offset = (page - 1) * size;
            return resourceMapper.searchResourcesWithPage(keyword, type, typeId, offset, size);
        } catch (Exception e) {
            // 如果数据库方法失败，使用内存分页
            List<Resource> filteredResources = searchResources(keyword, type, typeId);
            
            int offset = (page - 1) * size;
            
            // 计算结束索引，确保不超出列表范围
            int toIndex = Math.min(offset + size, filteredResources.size());
            
            // 确保起始索引不超出列表范围
            if (offset >= filteredResources.size() || offset < 0) {
                return new ArrayList<>();
            }
            
            return filteredResources.subList(offset, toIndex);
        }
    }
    
    @Override
    public int countResources(String keyword, String type, Integer typeId) {
        try {
            return resourceMapper.countResources(keyword, type, typeId);
        } catch (Exception e) {
            // 如果数据库方法失败，使用内存计数
            List<Resource> filteredResources = searchResources(keyword, type, typeId);
            return filteredResources.size();
        }
    }
    
    @Override
    public List<Resource> findByTypeId(Integer typeId) {
        try {
            return resourceMapper.findByTypeId(typeId);
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存过滤
            List<Resource> allResources = resourceMapper.findAll();
            List<Resource> result = new ArrayList<>();
            
            for (Resource resource : allResources) {
                if (resource.getTypeId() != null && resource.getTypeId().equals(typeId)) {
                    result.add(resource);
                }
            }
            
            return result;
        }
    }
    
    @Override
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
    
    @Override
    public List<String> findAllTypes() {
        try {
            return resourceMapper.findAllTypes();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存去重
            List<Resource> allResources = resourceMapper.findAll();
            List<String> types = new ArrayList<>();
            
            for (Resource resource : allResources) {
                String type = resource.getType();
                if (type != null && !type.isEmpty() && !types.contains(type)) {
                    types.add(type);
                }
            }
            
            return types;
        }
    }
    
    @Override
    public int countTotalResources() {
        try {
            return resourceMapper.countTotal();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存计数
            List<Resource> allResources = resourceMapper.findAll();
            return allResources.size();
        }
    }
    
    @Override
    public int countTodayResources() {
        try {
            return resourceMapper.countTodayResources();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存过滤
            List<Resource> allResources = resourceMapper.findAll();
            int count = 0;
            
            // 获取今天的日期（不包含时间）
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            for (Resource resource : allResources) {
                if (resource.getUploadTime() != null) {
                    String uploadDate = dateFormat.format(resource.getUploadTime());
                    if (today.equals(uploadDate)) {
                        count++;
                    }
                }
            }
            
            return count;
        }
    }
    
    @Override
    public int countTotalDownloads() {
        try {
            return resourceMapper.countTotalDownloads();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存计算
            List<Resource> allResources = resourceMapper.findAll();
            int totalDownloads = 0;
            
            for (Resource resource : allResources) {
                if (resource.getDownloadCount() != null) {
                    totalDownloads += resource.getDownloadCount();
                }
            }
            
            return totalDownloads;
        }
    }
    
    @Override
    public Map<String, Integer> countResourceByType() {
        try {
            return resourceMapper.countResourceByType();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存计算
            List<Resource> allResources = resourceMapper.findAll();
            Map<String, Integer> typeCounts = new HashMap<>();
            
            for (Resource resource : allResources) {
                String type = resource.getType();
                if (type != null && !type.isEmpty()) {
                    typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
                }
            }
            
            return typeCounts;
        }
    }
    
    @Override
    public Map<Integer, Integer> countResourceByTypeId() {
        try {
            return resourceMapper.countResourceByTypeId();
        } catch (Exception e) {
            // 如果mapper中尚未实现该方法，使用内存计算
            List<Resource> allResources = resourceMapper.findAll();
            Map<Integer, Integer> typeCounts = new HashMap<>();
            
            for (Resource resource : allResources) {
                Integer typeId = resource.getTypeId();
                if (typeId != null) {
                    typeCounts.put(typeId, typeCounts.getOrDefault(typeId, 0) + 1);
                }
            }
            
            return typeCounts;
        }
    }
    
    @Override
    public List<Resource> findByUploaderId(Integer uploaderId) {
        return resourceMapper.findByUploaderId(uploaderId);
    }
    
    @Override
    public int countByUploaderId(Integer uploaderId) {
        return resourceMapper.countByUploaderId(uploaderId);
    }
    
    @Override
    public Map<String, Integer> countByUploaderIdGroupByType(Integer uploaderId) {
        List<Map<String, Object>> results = resourceMapper.countByUploaderIdGroupByType(uploaderId);
        Map<String, Integer> typeCount = new HashMap<>();
        
        for (Map<String, Object> result : results) {
            String type = (String) result.get("type");
            Integer count = ((Number) result.get("count")).intValue();
            typeCount.put(type, count);
        }
        
        return typeCount;
    }
    
    @Override
    @Transactional
    public boolean updateTypeId(Integer resourceId, Integer typeId) {
        try {
            return resourceMapper.updateTypeId(resourceId, typeId) > 0;
        } catch (Exception e) {
            // 如果直接更新失败，尝试读取后更新
            Resource resource = resourceMapper.findById(resourceId);
            if (resource != null) {
                resource.setTypeId(typeId);
                resourceMapper.update(resource);
                return true;
            }
            return false;
        }
    }
    
    @Override
    @Transactional
    public double updateResourceRating(Integer resourceId, Integer userId, Double rating) {
        Resource resource = resourceMapper.findById(resourceId);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        
        // 假设存在评分记录表，将用户评分保存到数据库
        // 这里简化处理，直接更新资源的平均评分
        
        // 获取当前评分和评分人数
        Float currentRating = resource.getRating();
        Integer ratingCount = resource.getRatingCount();
        
        if (currentRating == null) {
            currentRating = 0.0f;
        }
        
        if (ratingCount == null) {
            ratingCount = 0;
        }
        
        // 计算新的平均评分
        double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
        
        // 更新资源评分信息
        resource.setRating((float) newRating);
        resource.setRatingCount(ratingCount + 1);
        
        // 保存更新
        resourceMapper.update(resource);
        
        return newRating;
    }
} 