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
    
    /**
     * 查询子类型列表
     */
    List<Type> findChildrenByParentId(Integer parentId);
    
    /**
     * 检查是否有子类型
     */
    Integer countChildrenByParentId(Integer parentId);
    
    /**
     * 获取类型树结构
     */
    List<Type> findTypeTree();
    
    /**
     * 根据名称搜索类型
     */
    List<Type> searchByName(@Param("keyword") String keyword);
    
    /**
     * 更新类型状态
     */
    void updateStatus(@Param("typeId") Integer typeId, @Param("status") Integer status);
} 