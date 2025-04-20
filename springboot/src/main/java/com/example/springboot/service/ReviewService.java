package com.example.springboot.service;

import com.example.springboot.entity.Review;
import com.example.springboot.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewMapper reviewMapper;

    public Review findById(Integer reviewId) {
        return reviewMapper.findById(reviewId);
    }

    public List<Review> findAll() {
        return reviewMapper.findAll();
    }
    
    public List<Review> findByResourceId(Integer resourceId) {
        return reviewMapper.findByResourceId(resourceId);
    }
    
    public List<Review> findByReviewerId(Integer reviewerId) {
        return reviewMapper.findByReviewerId(reviewerId);
    }

    public void save(Review review) {
        if (review.getReviewId() == null) {
            reviewMapper.insert(review);
        } else {
            reviewMapper.update(review);
        }
    }

    public void delete(Integer reviewId) {
        reviewMapper.delete(reviewId);
    }
} 