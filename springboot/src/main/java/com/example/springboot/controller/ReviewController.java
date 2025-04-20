package com.example.springboot.controller;

import com.example.springboot.entity.Review;
import com.example.springboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}")
    public Review findById(@PathVariable Integer id) {
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> findAll() {
        return reviewService.findAll();
    }
    
    @GetMapping("/resource/{resourceId}")
    public List<Review> findByResourceId(@PathVariable Integer resourceId) {
        return reviewService.findByResourceId(resourceId);
    }
    
    @GetMapping("/reviewer/{reviewerId}")
    public List<Review> findByReviewerId(@PathVariable Integer reviewerId) {
        return reviewService.findByReviewerId(reviewerId);
    }

    @PostMapping
    public void save(@RequestBody Review review) {
        reviewService.save(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        reviewService.delete(id);
    }
} 