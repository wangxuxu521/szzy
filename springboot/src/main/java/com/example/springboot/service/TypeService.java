package com.example.springboot.service;

import com.example.springboot.entity.Type;
import com.example.springboot.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    
    public void save(Type type) {
        if (type.getStatus() == null) {
            type.setStatus(1); // 默认状态为启用
        }
        
        if (type.getTypeId() == null) {
            typeMapper.insert(type);
        } else {
            typeMapper.update(type);
        }
    }
    
    public void delete(Integer typeId) {
        typeMapper.delete(typeId);
    }
} 