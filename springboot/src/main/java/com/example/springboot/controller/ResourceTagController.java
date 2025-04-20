package com.example.springboot.controller;

import com.example.springboot.entity.ResourceTag;
import com.example.springboot.service.ResourceTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource-tags")
public class ResourceTagController {
    @Autowired
    private ResourceTagService resourceTagService;

    @GetMapping("/{id}")
    public ResourceTag findById(@PathVariable Integer id) {
        return resourceTagService.findById(id);
    }

    @GetMapping
    public List<ResourceTag> findAll() {
        return resourceTagService.findAll();
    }
    
    @GetMapping("/resource/{resourceId}")
    public List<ResourceTag> findByResourceId(@PathVariable Integer resourceId) {
        return resourceTagService.findByResourceId(resourceId);
    }
    
    @GetMapping("/tag/{tagId}")
    public List<ResourceTag> findByTagId(@PathVariable Integer tagId) {
        return resourceTagService.findByTagId(tagId);
    }

    @PostMapping
    public void save(@RequestBody ResourceTag resourceTag) {
        resourceTagService.save(resourceTag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        resourceTagService.delete(id);
    }
} 