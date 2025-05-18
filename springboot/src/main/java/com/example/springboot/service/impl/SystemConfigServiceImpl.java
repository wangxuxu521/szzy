package com.example.springboot.service.impl;

import com.example.springboot.entity.SystemConfig;
import com.example.springboot.mapper.SystemConfigMapper;
import com.example.springboot.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    @Override
    public List<SystemConfig> findAll() {
        return systemConfigMapper.findAll();
    }
    
    @Override
    public SystemConfig findById(Integer id) {
        return systemConfigMapper.findById(id);
    }
    
    @Override
    public SystemConfig findByKey(String configKey) {
        return systemConfigMapper.findByKey(configKey);
    }
    
    @Override
    public Map<String, String> getAllConfigMap() {
        List<SystemConfig> configList = systemConfigMapper.findAll();
        Map<String, String> configMap = new HashMap<>();
        
        for (SystemConfig config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        
        return configMap;
    }
    
    @Override
    public boolean save(SystemConfig systemConfig) {
        return systemConfigMapper.insert(systemConfig) > 0;
    }
    
    @Override
    public boolean update(SystemConfig systemConfig) {
        return systemConfigMapper.updateByKey(systemConfig) > 0;
    }
    
    @Override
    public boolean updateByKey(String configKey, String configValue) {
        SystemConfig config = systemConfigMapper.findByKey(configKey);
        if (config == null) {
            return false;
        }
        
        config.setConfigValue(configValue);
        return systemConfigMapper.updateByKey(config) > 0;
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return systemConfigMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean deleteByKey(String configKey) {
        return systemConfigMapper.deleteByKey(configKey) > 0;
    }
} 