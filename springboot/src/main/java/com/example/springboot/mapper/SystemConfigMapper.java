package com.example.springboot.mapper;

import com.example.springboot.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    
    List<SystemConfig> findAll();
    
    SystemConfig findById(Integer id);
    
    SystemConfig findByKey(String configKey);
    
    int insert(SystemConfig systemConfig);
    
    int updateByKey(SystemConfig systemConfig);
    
    int deleteById(Integer id);
    
    int deleteByKey(String configKey);
} 