package com.example.springboot.entity;

import java.util.Date;

/**
 * 系统公告实体类
 */
public class Announcement {
    
    private Integer id;             // 公告ID
    private String title;           // 公告标题
    private String content;         // 公告内容
    private Integer type;           // 公告类型 0-普通公告，1-重要公告，2-紧急公告
    private Integer priority;       // 优先级，数字越大优先级越高
    private String publisher;       // 发布者
    private Date publishTime;       // 发布时间
    private Date createTime;        // 创建时间
    private Date updateTime;        // 更新时间
    private Integer status;         // 状态 0-未发布，1-已发布
    
    // Getter and Setter
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public Date getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
} 