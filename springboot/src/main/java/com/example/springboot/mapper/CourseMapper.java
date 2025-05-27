package com.example.springboot.mapper;

import com.example.springboot.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    /**
     * 根据ID查询课程
     */
    Course findById(Integer courseId);
    
    /**
     * 查询所有课程
     */
    List<Course> findAll();
    
    /**
     * 按类型查询课程
     */
    List<Course> findByTypeId(Integer typeId);
    
    /**
     * 按教师查询课程
     */
    List<Course> findByTeacherId(Integer teacherId);
    
    /**
     * 添加课程
     */
    void insert(Course course);
    
    /**
     * 更新课程
     */
    void update(Course course);
    
    /**
     * 删除课程
     */
    void delete(Integer courseId);
    
    /**
     * 搜索课程
     */
    List<Course> searchCourses(@Param("keyword") String keyword, @Param("typeId") Integer typeId);
    
    /**
     * 统计教师的课程数量
     * @param teacherId 教师ID
     * @return 课程数量
     */
    int countByTeacherId(Integer teacherId);
} 