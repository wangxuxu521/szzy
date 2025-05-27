package com.example.springboot.controller;

import com.example.springboot.entity.Resource;
import com.example.springboot.entity.ResourceRating;
import com.example.springboot.entity.User;
import com.example.springboot.service.ResourceService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.ResourceRatingService;
import com.example.springboot.service.MinioService;
import com.example.springboot.service.RedisService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

/**
 * 资源管理控制器
 */
@RestController
@RequestMapping("/resources")
public class ResourceController {
    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ResourceRatingService resourceRatingService;
    
    @Autowired
    private MinioService minioService;
    
    @Autowired
    private RedisService redisService;
    
    // Minio存储桶
    @Value("${minio.bucket}")
    private String minioBucket;

    // 文件上传根目录，从配置中获取
    @Value("${file.upload.dir:uploads/resources/}")
    private String uploadDir;
    
    // 缓存过期时间(秒)
    private static final long CACHE_EXPIRE_TIME = 3600; // 1小时

    // 获取资源详情
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        try {
            // 尝试从缓存获取
            String cacheKey = "resource:detail:" + id;
            Resource resource = redisService.get(cacheKey, Resource.class);
            
            if (resource == null) {
                // 缓存未命中，从数据库获取
                resource = resourceService.findById(id);
                if (resource != null) {
                    // 增加浏览次数
                    resourceService.increaseViewCount(id);
                    // 存入缓存
                    redisService.set(cacheKey, resource, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
                } else {
                    return Result.error("资源不存在");
                }
            }
            
            return Result.success(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取资源详情失败: " + e.getMessage());
        }
    }

    // 获取资源列表
    @GetMapping
    public Result findAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            log.info("获取资源列表 - 类型: {}, 类型ID: {}, 关键词: {}, 页码: {}, 每页数量: {}", type, typeId, query, page, size);
            List<Resource> resources;
            int total = 0;
            
            if ((type != null && !type.isEmpty()) || typeId != null || (query != null && !query.isEmpty())) {
                // 按查询条件过滤
                resources = resourceService.searchResources(query, type, typeId, page, size);
                // 获取总数
                total = resourceService.countResources(query, type, typeId);
            } else {
                // 获取分页后的资源列表
                resources = resourceService.findByPage(page, size);
                // 获取总数
                total = resourceService.countTotalResources();
            }
            
            // 封装分页信息
            Map<String, Object> result = new HashMap<>();
            result.put("data", resources);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (int) Math.ceil((double) total / size));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取资源列表失败", e);
            return Result.error("获取资源列表失败: " + e.getMessage());
        }
    }

    // 搜索资源
    @GetMapping("/search")
    public Result searchResources(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) Integer typeId) {
        try {
            log.info("搜索资源 - 关键词: {}, 类型: {}, 类型ID: {}", keyword, type, typeId);
            List<Resource> resources = resourceService.searchResources(keyword, type, typeId);
            log.info("搜索结果数量: {}", resources.size());
            return Result.success(resources);
        } catch (Exception e) {
            log.error("搜索资源失败", e);
            return Result.error("搜索资源失败: " + e.getMessage());
        }
    }

    // 获取热门资源
    @GetMapping("/hot")
    public Result getHotResources(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        try {
            List<Resource> hotResources = resourceService.findHotResources(limit);
            return Result.success(hotResources);
        } catch (Exception e) {
            log.error("获取热门资源失败", e);
            return Result.error("获取热门资源失败: " + e.getMessage());
        }
    }

    // 获取资源类型列表
    @GetMapping("/types")
    public Result getResourceTypes() {
        try {
            List<String> types = resourceService.findAllTypes();
            return Result.success(types);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取资源类型列表失败: " + e.getMessage());
        }
    }

    // 上传资源
    @PostMapping("/upload")
    public Result uploadResource(@RequestParam("file") MultipartFile file,
                                @RequestParam("title") String title,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "typeId", required = false) Integer typeId,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "tags", required = false) String tags) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = UUID.randomUUID().toString() + fileExtension;
            
            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 上传文件到MinIO
            String fileUrl = minioService.uploadFile(minioBucket, objectName, file, contentType);

            // 创建资源记录
            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setType(type);
            resource.setTypeId(typeId);
            resource.setDescription(description);
            resource.setFilePath(objectName); // 存储MinIO对象名
            resource.setFileName(originalFilename);
            resource.setFileSize(file.getSize());
            resource.setFormat(fileExtension);
            resource.setUploadTime(new Date());
            resource.setDownloadCount(0);
            resource.setViewCount(0);
            resource.setRating(0.0F);
            
            // 从安全上下文中获取当前登录用户ID
            Integer currentUserId;
            String uploaderName = "未知用户";
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                // 通过用户名查找用户ID
                User currentUser = userService.findByUsername(username);
                if (currentUser != null) {
                    currentUserId = currentUser.getUserId();
                    uploaderName = currentUser.getName();
                    log.info("当前上传用户: ID={}, 姓名={}", currentUserId, uploaderName);
                } else {
                    // 如果找不到用户，使用默认ID
                    currentUserId = 1;
                    log.warn("无法获取当前登录用户信息，使用默认用户ID: {}", currentUserId);
                }
            } catch (Exception e) {
                // 发生异常时使用默认ID
                currentUserId = 1;
                log.error("获取当前登录用户信息时出错，使用默认用户ID: {}", currentUserId, e);
            }
            resource.setUploaderId(currentUserId);
            resource.setUploaderName(uploaderName);
            
            resource.setReviewStatus("pending");
            
            // 保存标签信息
            if (tags != null && !tags.isEmpty()) {
                resource.setTags(tags);
                // 在service层处理标签的保存
            }

        resourceService.save(resource);

            return Result.success(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存资源失败: " + e.getMessage());
        }
    }

    // 更新资源
    @PutMapping("/{id}")
    public Result updateResource(@PathVariable Integer id,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam("title") String title,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "typeId", required = false) Integer typeId,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "tags", required = false) String tags) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }

            // 更新文件（如果有新文件上传）
            if (file != null && !file.isEmpty()) {
                // 删除旧文件
                try {
                    Path oldFilePath = Paths.get(resource.getFilePath());
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    // 记录错误但继续执行
                    System.err.println("删除旧文件失败: " + e.getMessage());
                }

                // 保存新文件
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFilename = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadDir + newFilename;

                Path targetPath = Paths.get(filePath);
                Files.copy(file.getInputStream(), targetPath);

                resource.setFilePath(filePath);
                resource.setFileName(originalFilename);
                resource.setFileSize(file.getSize());
                resource.setFormat(fileExtension);
            }

            // 更新其他信息
            resource.setTitle(title);
            resource.setType(type);
            resource.setTypeId(typeId);
            resource.setDescription(description);
            
            // 更新标签
            if (tags != null) {
                resource.setTags(tags);
                // 在service层处理标签的更新
            }

            resourceService.update(resource);

            return Result.success(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新资源失败: " + e.getMessage());
        }
    }

    // 删除资源
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }

            // 删除文件
            try {
                Path filePath = Paths.get(resource.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 记录错误但继续执行
                System.err.println("删除文件失败: " + e.getMessage());
            }

            // 删除数据库记录
        resourceService.delete(id);

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除资源失败: " + e.getMessage());
        }
    }

    // 下载资源
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadResource(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // 增加下载次数
            resourceService.increaseDownloadCount(id);
            
            try {
                // 从MinIO获取文件流
                InputStream inputStream = minioService.getObject(minioBucket, resource.getFilePath());
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                
                // 返回文件
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));
            } catch (Exception e) {
                log.error("从MinIO获取文件失败，尝试从本地获取", e);
                
                // 如果从MinIO获取失败，尝试从本地文件系统获取
                File file = new File(resource.getFilePath());
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
                
                // 返回文件
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new FileSystemResource(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 检查资源是否支持预览
    @GetMapping("/preview-support/{id}")
    public Result checkPreviewSupport(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }
            
            String fileName = resource.getFileName();
            String fileExtension = "";
            if (fileName != null && fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }
            
            // 检查文件类型是否支持预览
            boolean supported = isSupportedForPreview(fileExtension);
            String fileType = getFileTypeFromExtension(fileExtension);
            
            return Result.success(new PreviewSupportInfo(supported, fileType));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("检查预览支持失败: " + e.getMessage());
        }
    }
    
    // 辅助类：预览支持信息
    private static class PreviewSupportInfo {
        private boolean supported;
        private String fileType;
        
        public PreviewSupportInfo(boolean supported, String fileType) {
            this.supported = supported;
            this.fileType = fileType;
        }
        
        // 确保字段可以被正确序列化
        public boolean isSupported() {
            return supported;
        }
        
        public void setSupported(boolean supported) {
            this.supported = supported;
        }
        
        public String getFileType() {
            return fileType;
        }
        
        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }
    
    // 判断文件类型是否支持预览
    private boolean isSupportedForPreview(String fileExtension) {
        return isPdfFile(fileExtension) || isImageFile(fileExtension) || 
               isTextFile(fileExtension) || isOfficeFile(fileExtension) ||
               isVideoFile(fileExtension) || isAudioFile(fileExtension);
    }
    
    // 获取文件类型分类
    private String getFileTypeFromExtension(String extension) {
        if (isPdfFile(extension)) {
            return "pdf";
        } else if (isImageFile(extension)) {
            return "image";
        } else if (isTextFile(extension)) {
            return "text";
        } else if (isOfficeFile(extension)) {
            return "office";
        } else if (isVideoFile(extension)) {
            return "video";
        } else if (isAudioFile(extension)) {
            return "audio";
        }
        return "other";
    }
    
    // 判断是否为PDF文件
    private boolean isPdfFile(String fileExtension) {
        return "pdf".equals(fileExtension);
    }
    
    // 判断是否为图片文件
    private boolean isImageFile(String fileExtension) {
        return fileExtension != null && 
               (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || 
                fileExtension.equals("png") || fileExtension.equals("gif") || 
                fileExtension.equals("bmp") || fileExtension.equals("webp"));
    }
    
    // 判断是否为文本文件
    private boolean isTextFile(String fileExtension) {
        return fileExtension != null && 
               (fileExtension.equals("txt") || fileExtension.equals("log") || 
                fileExtension.equals("json") || fileExtension.equals("xml") || 
                fileExtension.equals("html") || fileExtension.equals("css") || 
                fileExtension.equals("js") || fileExtension.equals("md"));
    }
    
    // 判断是否为Office文件
    private boolean isOfficeFile(String fileExtension) {
        return fileExtension != null && 
               (fileExtension.equals("doc") || fileExtension.equals("docx") || 
                fileExtension.equals("xls") || fileExtension.equals("xlsx") || 
                fileExtension.equals("ppt") || fileExtension.equals("pptx"));
    }
    
    // 判断是否为视频文件
    private boolean isVideoFile(String fileExtension) {
        return fileExtension != null && 
               (fileExtension.equals("mp4") || fileExtension.equals("webm") || 
                fileExtension.equals("ogg") || fileExtension.equals("avi") || 
                fileExtension.equals("mov") || fileExtension.equals("wmv") ||
                fileExtension.equals("flv") || fileExtension.equals("mkv"));
    }
    
    // 判断是否为音频文件
    private boolean isAudioFile(String fileExtension) {
        if (fileExtension == null) return false;
        return fileExtension.equalsIgnoreCase("mp3") || 
               fileExtension.equalsIgnoreCase("wav") ||
               fileExtension.equalsIgnoreCase("ogg") ||
               fileExtension.equalsIgnoreCase("aac") ||
               fileExtension.equalsIgnoreCase("flac");
    }
    
    // 获取图片文件的Content-Type
    private String getImageContentType(String fileExtension) {
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            default:
                return "image/jpeg";
        }
    }
    
    // 获取视频文件的Content-Type
    private String getVideoContentType(String fileExtension) {
        switch (fileExtension) {
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "ogg":
                return "video/ogg";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "flv":
                return "video/x-flv";
            case "mkv":
                return "video/x-matroska";
            default:
                return "video/mp4";
        }
    }

    // 获取音频文件的Content-Type
    private String getAudioContentType(String fileExtension) {
        if (fileExtension == null) return "audio/mpeg";
        
        switch(fileExtension.toLowerCase()) {
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "ogg":
                return "audio/ogg";
            case "aac":
                return "audio/aac";
            case "flac":
                return "audio/flac";
            default:
                return "audio/mpeg";
        }
    }

    // 资源预览
    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewResource(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 增加浏览次数
            resourceService.increaseViewCount(id);
            
            // 获取文件扩展名
            String fileName = resource.getFileName();
            String fileExtension = "";
            if (fileName != null && fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }
            
            try {
                // 从MinIO获取文件流
                InputStream inputStream = minioService.getObject(minioBucket, resource.getFilePath());
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                
                // 根据文件类型设置不同的Content-Type
                if (isPdfFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
                } else if (isImageFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getImageContentType(fileExtension));
                } else if (isTextFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
                } else if (isVideoFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getVideoContentType(fileExtension));
                } else if (isAudioFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getAudioContentType(fileExtension));
                } else {
                    // 对于不支持直接预览的文件类型，使用下载方式处理
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"");
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                }
                
                // 返回文件
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));
            } catch (Exception e) {
                log.error("从MinIO获取文件失败，尝试从本地获取", e);
                
                // 如果从MinIO获取失败，尝试从本地文件系统获取
                File file = new File(resource.getFilePath());
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                
                // 根据文件类型设置不同的Content-Type
                if (isPdfFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
                } else if (isImageFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getImageContentType(fileExtension));
                } else if (isTextFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
                } else if (isVideoFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getVideoContentType(fileExtension));
                } else if (isAudioFile(fileExtension)) {
                    headers.add(HttpHeaders.CONTENT_TYPE, getAudioContentType(fileExtension));
                } else {
                    // 对于不支持直接预览的文件类型，使用下载方式处理
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"");
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                }
                
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
                
                // 返回文件
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new FileSystemResource(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 修改Office文件预览API
    @GetMapping("/office-preview/{id}")
    public ResponseEntity<String> previewOfficeFile(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 增加浏览次数
            resourceService.increaseViewCount(id);
            
            // 获取文件扩展名
            String fileName = resource.getFileName();
            String fileExtension = "";
            if (fileName != null && fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            }
            
            // 检查是否为Office文件
            if (!isOfficeFile(fileExtension)) {
                return ResponseEntity.badRequest().body("不支持的文件类型");
            }
            
            // 构建预览URL（使用MinIO的临时URL）
            String fileUrl = minioService.getPresignedObjectUrl(minioBucket, resource.getFilePath(), 3600);
            
            // 对URL进行编码
            String encodedFileUrl = java.net.URLEncoder.encode(fileUrl, "UTF-8");
            
            // 使用Office Online Viewer
            String officeOnlineUrl = "https://view.officeapps.live.com/op/view.aspx?src=" + encodedFileUrl;
            
            // 返回预览页面或重定向到Office Online
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            
            // 创建一个简单的HTML页面嵌入Office Online Viewer
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Office文档预览</title>\n" +
                    "    <style>\n" +
                    "        body, html { margin: 0; padding: 0; height: 100%; overflow: hidden; }\n" +
                    "        #office-frame { width: 100%; height: 100%; border: none; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <iframe id=\"office-frame\" src=\"" + officeOnlineUrl + "\"></iframe>\n" +
                    "</body>\n" +
                    "</html>";
            
            return ResponseEntity.ok(htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("预览失败: " + e.getMessage());
        }
    }

    // 更新资源审核状态
    @PutMapping("/{id}/review-status")
    public Result updateReviewStatus(@PathVariable Integer id, @RequestBody Map<String, String> requestBody) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return Result.error("资源不存在");
            }
            
            String reviewStatus = requestBody.get("reviewStatus");
            if (reviewStatus == null || reviewStatus.isEmpty()) {
                return Result.error("审核状态不能为空");
            }
            
            // 验证状态值
            if (!Arrays.asList("pending", "approved", "rejected").contains(reviewStatus)) {
                return Result.error("无效的审核状态");
            }
            
            // 更新审核状态
            resource.setReviewStatus(reviewStatus);
            resourceService.update(resource);
            
            return Result.success(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新审核状态失败: " + e.getMessage());
        }
    }

    /**
     * 对资源进行评分
     */
    @PostMapping("/{id}/rate")
    public Result rateResource(@PathVariable Integer id, @RequestBody Map<String, Object> ratingData) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法评分");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 获取评分值
            Double rating = Double.parseDouble(ratingData.get("rating").toString());
            if (rating < 0 || rating > 5) {
                return Result.error("评分值必须在0-5之间");
            }

            // 保存评分
            resourceRatingService.rateResource(id, user.getUserId(), rating);
            
            // 获取更新后的资源
            Resource resource = resourceService.findById(id);
            
            return Result.success(resource);
        } catch (NumberFormatException e) {
            log.error("评分值格式错误", e);
            return Result.error("评分值格式错误");
        } catch (Exception e) {
            log.error("评分失败", e);
            return Result.error("评分失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户对资源的评分
     */
    @GetMapping("/{id}/user-rating")
    public Result getUserResourceRating(@PathVariable Integer id) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("未登录，无法获取评分信息");
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 获取用户评分
            ResourceRating userRating = resourceRatingService.getUserResourceRating(id, user.getUserId());
            
            if (userRating != null) {
                return Result.success(userRating.getRating());
            } else {
                return Result.success(0); // 用户未评分
            }
        } catch (Exception e) {
            log.error("获取用户评分失败", e);
            return Result.error("获取用户评分失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取资源的所有评分
     */
    @GetMapping("/{id}/ratings")
    public Result getResourceRatings(@PathVariable Integer id) {
        try {
            List<ResourceRating> ratings = resourceRatingService.getResourceRatings(id);
            return Result.success(ratings);
        } catch (Exception e) {
            log.error("获取资源评分列表失败", e);
            return Result.error("获取资源评分列表失败: " + e.getMessage());
        }
    }
} 