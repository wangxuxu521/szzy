package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 系统公告实体
 */
@Data
public class Announcement {
    
    private Integer id;
    
    // 公告标题
    private String title;
    
    // 公告内容
    private String content;
    
    // 公告类型（0-普通公告，1-重要公告，2-紧急公告）
    private Integer type;
    
    // 优先级（数字越大优先级越高）
    private Integer priority;
    
    // 发布者
    private String publisher;
    
    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
    
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    // 状态（0-未发布，1-已发布）
    private Integer status;
} 