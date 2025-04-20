package com.example.springboot.mapper;

import com.example.springboot.entity.ResourceTag;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ResourceTagMapper {
    ResourceTag findById(Integer id);
    List<ResourceTag> findAll();
    List<ResourceTag> findByResourceId(Integer resourceId);
    List<ResourceTag> findByTagId(Integer tagId);
    void insert(ResourceTag resourceTag);
    void update(ResourceTag resourceTag);
    void delete(Integer id);
} 