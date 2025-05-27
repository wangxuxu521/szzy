package com.example.springboot.service;

import com.example.springboot.entity.Course;
import com.example.springboot.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseMapper courseMapper;
    
    public Course findById(Integer courseId) {
        return courseMapper.findById(courseId);
    }
    
    public List<Course> findAll() {
        return courseMapper.findAll();
    }
    
    public List<Course> findByTypeId(Integer typeId) {
        return courseMapper.findByTypeId(typeId);
    }
    
    public List<Course> findByTeacherId(Integer teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }
    
    public List<Course> searchCourses(String keyword, Integer typeId) {
        return courseMapper.searchCourses(keyword, typeId);
    }
    
    public void save(Course course) {
        if (course.getStatus() == null) {
            course.setStatus(1); // 默认状态为启用
        }
        
        if (course.getCourseId() == null) {
            courseMapper.insert(course);
        } else {
            courseMapper.update(course);
        }
    }
    
    public void delete(Integer courseId) {
        courseMapper.delete(courseId);
    }

    /**
     * 统计教师的课程数量
     * @param teacherId 教师ID
     * @return 课程数量
     */
    public int countByTeacherId(Integer teacherId) {
        return courseMapper.countByTeacherId(teacherId);
    }
} 