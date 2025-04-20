package com.example.springboot.mapper;

import com.example.springboot.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReviewMapper {
    Review findById(Integer reviewId);
    List<Review> findAll();
    List<Review> findByResourceId(Integer resourceId);
    List<Review> findByReviewerId(Integer reviewerId);
    void insert(Review review);
    void update(Review review);
    void delete(Integer reviewId);
} 