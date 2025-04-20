package com.example.springboot.service;

import com.example.springboot.entity.Tag;
import com.example.springboot.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagMapper tagMapper;

    public Tag findById(Integer tagId) {
        return tagMapper.findById(tagId);
    }

    public List<Tag> findAll() {
        return tagMapper.findAll();
    }

    public void save(Tag tag) {
        if (tag.getTagId() == null) {
            tagMapper.insert(tag);
        } else {
            tagMapper.update(tag);
        }
    }

    public void delete(Integer tagId) {
        tagMapper.delete(tagId);
    }
} 