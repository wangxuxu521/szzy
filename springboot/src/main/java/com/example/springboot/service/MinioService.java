package com.example.springboot.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO服务接口
 */
public interface MinioService {

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param file 文件
     * @param contentType 内容类型
     * @return 文件访问路径
     */
    String uploadFile(String bucketName, String objectName, MultipartFile file, String contentType);

    /**
     * 获取文件流
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件流
     */
    InputStream getObject(String bucketName, String objectName);

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    boolean removeObject(String bucketName, String objectName);

    /**
     * 获取文件外链
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 过期时间（以秒为单位），默认7天
     * @return 文件外链
     */
    String getPresignedObjectUrl(String bucketName, String objectName, Integer expires);

    /**
     * 检查存储桶是否存在
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     */
    void makeBucket(String bucketName);
} 