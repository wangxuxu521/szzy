package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.SystemConfig;
import com.example.springboot.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system-config")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @GetMapping
    public Result getAllConfigs() {
        List<SystemConfig> configs = systemConfigService.findAll();
        return Result.success(configs);
    }
    
    @GetMapping("/map")
    public Result getAllConfigMap() {
        Map<String, String> configMap = systemConfigService.getAllConfigMap();
        return Result.success(configMap);
    }
    
    @GetMapping("/{id}")
    public Result getConfigById(@PathVariable Integer id) {
        SystemConfig config = systemConfigService.findById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }
    
    @GetMapping("/key/{configKey}")
    public Result getConfigByKey(@PathVariable String configKey) {
        SystemConfig config = systemConfigService.findByKey(configKey);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result saveConfig(@RequestBody SystemConfig systemConfig) {
        if (systemConfigService.findByKey(systemConfig.getConfigKey()) != null) {
            return Result.error("配置键已存在");
        }
        
        boolean success = systemConfigService.save(systemConfig);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存配置失败");
        }
    }
    
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result updateConfig(@RequestBody SystemConfig systemConfig) {
        SystemConfig existing = systemConfigService.findByKey(systemConfig.getConfigKey());
        if (existing == null) {
            return Result.error("配置不存在");
        }
        
        boolean success = systemConfigService.update(systemConfig);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新配置失败");
        }
    }
    
    @PutMapping("/{configKey}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result updateConfigValue(@PathVariable String configKey, @RequestBody Map<String, String> body) {
        String configValue = body.get("configValue");
        if (configValue == null) {
            return Result.error("配置值不能为空");
        }
        
        boolean success = systemConfigService.updateByKey(configKey, configValue);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新配置失败");
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result deleteConfig(@PathVariable Integer id) {
        boolean success = systemConfigService.deleteById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除配置失败");
        }
    }
    
    @DeleteMapping("/key/{configKey}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result deleteConfigByKey(@PathVariable String configKey) {
        boolean success = systemConfigService.deleteByKey(configKey);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除配置失败");
        }
    }
} 