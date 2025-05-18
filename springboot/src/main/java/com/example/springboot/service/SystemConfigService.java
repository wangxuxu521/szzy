package com.example.springboot.service;

import com.example.springboot.entity.SystemConfig;
import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    
    List<SystemConfig> findAll();
    
    SystemConfig findById(Integer id);
    
    SystemConfig findByKey(String configKey);
    
    Map<String, String> getAllConfigMap();
    
    boolean save(SystemConfig systemConfig);
    
    boolean update(SystemConfig systemConfig);
    
    boolean updateByKey(String configKey, String configValue);
    
    boolean deleteById(Integer id);
    
    boolean deleteByKey(String configKey);
} 