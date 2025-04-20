package com.example.springboot.service;

import com.example.springboot.entity.ResourceTag;
import com.example.springboot.mapper.ResourceTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceTagService {
    @Autowired
    private ResourceTagMapper resourceTagMapper;

    public ResourceTag findById(Integer id) {
        return resourceTagMapper.findById(id);
    }

    public List<ResourceTag> findAll() {
        return resourceTagMapper.findAll();
    }
    
    public List<ResourceTag> findByResourceId(Integer resourceId) {
        return resourceTagMapper.findByResourceId(resourceId);
    }
    
    public List<ResourceTag> findByTagId(Integer tagId) {
        return resourceTagMapper.findByTagId(tagId);
    }

    public void save(ResourceTag resourceTag) {
        if (resourceTag.getId() == null) {
            resourceTagMapper.insert(resourceTag);
        } else {
            resourceTagMapper.update(resourceTag);
        }
    }

    public void delete(Integer id) {
        resourceTagMapper.delete(id);
    }
} 