package com.example.springboot.mapper;

import com.example.springboot.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TypeMapper {
    /**
     * 根据ID查询类型
     */
    Type findById(Integer typeId);
    
    /**
     * 查询所有类型
     */
    List<Type> findAll();
    
    /**
     * 添加类型
     */
    void insert(Type type);
    
    /**
     * 更新类型
     */
    void update(Type type);
    
    /**
     * 删除类型
     */
    void delete(Integer typeId);
    
    /**
     * 查询类型是否已存在
     */
    Integer checkTypeNameExists(@Param("typeName") String typeName);
} 