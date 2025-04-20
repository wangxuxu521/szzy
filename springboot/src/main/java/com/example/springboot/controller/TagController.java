package com.example.springboot.controller;

import com.example.springboot.entity.Tag;
import com.example.springboot.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/{id}")
    public Tag findById(@PathVariable Integer id) {
        return tagService.findById(id);
    }

    @GetMapping
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @PostMapping
    public void save(@RequestBody Tag tag) {
        tagService.save(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        tagService.delete(id);
    }
} 