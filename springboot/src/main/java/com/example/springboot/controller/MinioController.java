package com.example.springboot.controller;

import com.example.springboot.service.MinioService;
import com.example.springboot.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MinIO控制器
 */
@RestController
@RequestMapping("/minio")
public class MinioController {
    private static final Logger log = LoggerFactory.getLogger(MinioController.class);

    @Autowired
    private MinioService minioService;

    @Value("${minio.bucket}")
    private String defaultBucket;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + fileExtension;

            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 上传文件
            String fileUrl = minioService.uploadFile(defaultBucket, objectName, file, contentType);

            // 返回文件信息
            Map<String, String> data = new HashMap<>();
            data.put("fileName", originalFilename);
            data.put("fileUrl", fileUrl);
            data.put("objectName", objectName);
            data.put("fileSize", String.valueOf(file.getSize()));
            data.put("contentType", contentType);

            return Result.success(data);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return Result.error("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{objectName}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String objectName, 
                                                       @RequestParam(required = false) String fileName) {
        try {
            // 获取文件流
            InputStream inputStream = minioService.getObject(defaultBucket, objectName);
            
            // 设置文件名，如果未提供则使用对象名
            String downloadFileName = fileName != null ? fileName : objectName;
            String encodedFileName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
            
            // 返回文件
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("下载文件失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 预览文件
     */
    @GetMapping("/preview/{objectName}")
    public ResponseEntity<InputStreamResource> preview(@PathVariable String objectName) {
        try {
            // 获取文件流
            InputStream inputStream = minioService.getObject(defaultBucket, objectName);
            
            // 获取文件类型
            String contentType = "application/octet-stream";
            if (objectName.contains(".")) {
                String fileExtension = objectName.substring(objectName.lastIndexOf(".") + 1).toLowerCase();
                contentType = getContentTypeByExtension(fileExtension);
            }
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            // 返回文件
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("预览文件失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取文件预览URL
     */
        @GetMapping("/url/{objectName}")
    public Result getUrl(@PathVariable String objectName,
                       @RequestParam(required = false) Integer expires) {
        try {
            String url = minioService.getPresignedObjectUrl(defaultBucket, objectName, expires);
            return Result.success(url);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return Result.error("获取文件URL失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{objectName}")
    public Result delete(@PathVariable String objectName) {
        try {
            boolean result = minioService.removeObject(defaultBucket, objectName);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return Result.error("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件扩展名获取ContentType
     */
    private String getContentTypeByExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mpeg";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }
} 