package com.example.springboot.controller;

import com.example.springboot.entity.Resource;
import com.example.springboot.service.ResourceService;
import com.example.springboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 资源管理控制器
 */
@RestController
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    // 文件上传根目录，从配置中获取
    @Value("${file.upload.dir:uploads/resources/}")
    private String uploadDir;

    // 获取资源详情
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource != null) {
                // 增加浏览次数
                resourceService.increaseViewCount(id);
                return Result.success(resource);
            } else {
                return Result.error("资源不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取资源详情失败: " + e.getMessage());
        }
    }

    // 获取资源列表
    @GetMapping
    public Result findAll() {
        try {
            List<Resource> resources = resourceService.findAll();
            return Result.success(resources);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取资源列表失败: " + e.getMessage());
        }
    }

    // 搜索资源
    @GetMapping("/search")
    public Result searchResources(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String type) {
        try {
            List<Resource> resources = resourceService.searchResources(keyword, type);
            return Result.success(resources);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索资源失败: " + e.getMessage());
        }
    }

    // 获取热门资源
    @GetMapping("/hot")
    public Result getHotResources(@RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<Resource> hotResources = resourceService.findHotResources(limit);
            return Result.success(hotResources);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取热门资源失败: " + e.getMessage());
        }
    }

    // 上传资源
    @PostMapping("/upload")
    public Result uploadResource(@RequestParam("file") MultipartFile file,
                                @RequestParam("title") String title,
                                @RequestParam("type") String type,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "tags", required = false) String tags) {
        try {
            // 检查目录是否存在，不存在则创建
            File directory = new File(uploadDir);
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
            String filePath = uploadDir + newFilename;

            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath);

            // 创建资源记录
            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setType(type);
            resource.setDescription(description);
            resource.setFilePath(filePath);
            resource.setFileName(originalFilename);
            resource.setFileSize(file.getSize());
            resource.setFormat(fileExtension);
            resource.setUploadTime(new Date());
            resource.setDownloadCount(0);
            resource.setViewCount(0);
            resource.setRating(0.0F);
            
            // 当前登录用户ID，实际应从会话中获取
            // 这里假设从会话或安全上下文中获取
            Integer currentUserId = 1; // 临时硬编码，实际应该是动态获取的
            resource.setUploaderId(currentUserId);
            
            resource.setReviewStatus("pending");
            
            // 保存标签信息
            if (tags != null && !tags.isEmpty()) {
                resource.setTags(tags);
                // 在service层处理标签的保存
            }

        resourceService.save(resource);

            return Result.success(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
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
                                @RequestParam("type") String type,
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
    public ResponseEntity<FileSystemResource> downloadResource(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.findById(id);
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // 增加下载次数
            resourceService.increaseDownloadCount(id);

            // 获取文件
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
               isVideoFile(fileExtension);
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
            
            // 获取文件
            File file = new File(resource.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 获取文件扩展名
            String fileName = resource.getFileName();
            String fileExtension = "";
            if (fileName != null && fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 