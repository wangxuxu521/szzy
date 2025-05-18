package com.example.springboot.mapper;

import com.example.springboot.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    
    @Select("SELECT * FROM system_config")
    List<SystemConfig> findAll();
    
    @Select("SELECT * FROM system_config WHERE id = #{id}")
    SystemConfig findById(Integer id);
    
    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfig findByKey(String configKey);
    
    @Insert("INSERT INTO system_config(config_key, config_value, description) VALUES(#{configKey}, #{configValue}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig systemConfig);
    
    @Update("UPDATE system_config SET config_value = #{configValue}, description = #{description}, update_time = NOW() WHERE config_key = #{configKey}")
    int updateByKey(SystemConfig systemConfig);
    
    @Delete("DELETE FROM system_config WHERE id = #{id}")
    int deleteById(Integer id);
    
    @Delete("DELETE FROM system_config WHERE config_key = #{configKey}")
    int deleteByKey(String configKey);
} 