package com.example.springboot.mapper;

import com.example.springboot.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TagMapper {
    Tag findById(Integer tagId);
    List<Tag> findAll();
    void insert(Tag tag);
    void update(Tag tag);
    void delete(Integer tagId);
} 