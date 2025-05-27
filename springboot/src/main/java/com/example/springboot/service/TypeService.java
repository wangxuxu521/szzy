package com.example.springboot.service;

import com.example.springboot.entity.Type;
import com.example.springboot.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TypeService {
    @Autowired
    private TypeMapper typeMapper;
    
    public Type findById(Integer typeId) {
        return typeMapper.findById(typeId);
    }
    
    public List<Type> findAll() {
        return typeMapper.findAll();
    }
    
    public boolean checkTypeNameExists(String typeName) {
        return typeMapper.checkTypeNameExists(typeName) > 0;
    }
    
    @Transactional
    public void save(Type type) {
        if (type.getStatus() == null) {
            type.setStatus(1); // 默认状态为启用
        }
        
        if (type.getSortOrder() == null) {
            type.setSortOrder(0); // 默认排序为0
        }
        
        if (type.getTypeId() == null) {
            typeMapper.insert(type);
        } else {
            typeMapper.update(type);
        }
    }
    
    @Transactional
    public void delete(Integer typeId) {
        // 检查是否有子类型
        if (typeMapper.countChildrenByParentId(typeId) > 0) {
            throw new RuntimeException("该类型下存在子类型，不能删除");
        }
        
        typeMapper.delete(typeId);
    }
    
    public List<Type> findChildrenByParentId(Integer parentId) {
        return typeMapper.findChildrenByParentId(parentId);
    }
    
    public boolean hasChildren(Integer typeId) {
        return typeMapper.countChildrenByParentId(typeId) > 0;
    }
    
    public void updateStatus(Integer typeId, Integer status) {
        typeMapper.updateStatus(typeId, status);
    }
    
    public List<Type> searchByName(String keyword) {
        return typeMapper.searchByName(keyword);
    }
    
    /**
     * 获取类型树结构
     */
    public List<Type> getTypeTree() {
        List<Type> allTypes = typeMapper.findTypeTree();
        
        // 处理每个类型是否有子类型
        for (Type type : allTypes) {
            boolean hasChildren = hasChildren(type.getTypeId());
            type.setHasChildren(hasChildren);
        }
        
        // 构建树结构
        List<Type> rootTypes = new ArrayList<>();
        Map<Integer, List<Type>> childrenMap = allTypes.stream()
                .filter(type -> type.getParentId() != null)
                .collect(Collectors.groupingBy(Type::getParentId));
                
        // 添加根节点
        for (Type type : allTypes) {
            if (type.getParentId() == null) {
                rootTypes.add(type);
            }
        }
        
        return rootTypes;
    }
    
    /**
     * 批量删除类型
     */
    @Transactional
    public void batchDelete(List<Integer> typeIds) {
        for (Integer typeId : typeIds) {
            delete(typeId);
        }
    }
} 