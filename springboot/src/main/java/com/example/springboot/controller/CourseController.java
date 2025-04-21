package com.example.springboot.controller;

import com.example.springboot.entity.Course;
import com.example.springboot.service.CourseService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    
    // 文件上传路径
    private static final String UPLOAD_DIR = "uploads/courses/";
    
    // 获取课程详情
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        try {
            Course course = courseService.findById(id);
            if (course != null) {
                return Result.success(course);
            } else {
                return Result.error("课程不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课程详情失败: " + e.getMessage());
        }
    }
    
    // 获取所有课程
    @GetMapping
    public Result findAll() {
        try {
            return Result.success(courseService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
    
    // 根据类型获取课程
    @GetMapping("/type/{typeId}")
    public Result findByTypeId(@PathVariable Integer typeId) {
        try {
            return Result.success(courseService.findByTypeId(typeId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
    
    // 根据教师获取课程
    @GetMapping("/teacher/{teacherId}")
    public Result findByTeacherId(@PathVariable Integer teacherId) {
        try {
            return Result.success(courseService.findByTeacherId(teacherId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
    
    // 搜索课程
    @GetMapping("/search")
    public Result searchCourses(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer typeId,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<Course> courses = courseService.searchCourses(keyword, typeId);
            
            // 创建分页数据结构
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", courses);
            pageData.put("total", courses.size());
            
            return Result.success(pageData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索课程失败: " + e.getMessage());
        }
    }
    
    // 添加课程
    @PostMapping
    public Result save(@RequestParam(value = "file", required = false) MultipartFile file,
                      @RequestParam("courseName") String courseName,
                      @RequestParam("typeId") Integer typeId,
                      @RequestParam("teacherId") Integer teacherId,
                      @RequestParam(value = "description", required = false) String description,
                      @RequestParam(value = "courseId", required = false) Integer courseId) {
        try {
            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseName(courseName);
            course.setTypeId(typeId);
            course.setTeacherId(teacherId);
            course.setDescription(description);
            course.setStatus(1);
            
            // 处理封面图片上传
            if (file != null && !file.isEmpty()) {
                // 确保目录存在
                File directory = new File(UPLOAD_DIR);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                
                // 生成唯一文件名
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFilename = UUID.randomUUID().toString() + fileExtension;
                String filePath = UPLOAD_DIR + newFilename;
                
                // 保存文件
                Path targetPath = Paths.get(filePath);
                Files.copy(file.getInputStream(), targetPath);
                
                course.setCoverImage(filePath);
            }
            
            courseService.save(course);
            return Result.success(course);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存课程失败: " + e.getMessage());
        }
    }
    
    // 删除课程
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            Course course = courseService.findById(id);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 删除封面图片
            if (course.getCoverImage() != null && !course.getCoverImage().isEmpty()) {
                try {
                    Path filePath = Paths.get(course.getCoverImage());
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    // 记录但不中断操作
                    System.err.println("删除封面图片失败: " + e.getMessage());
                }
            }
            
            courseService.delete(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除课程失败: " + e.getMessage());
        }
    }
} 