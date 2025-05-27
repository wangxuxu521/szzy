# Redis 和 MinIO 集成说明

本项目集成了 Redis 缓存和 MinIO 对象存储服务，用于提高系统性能和文件存储的可靠性。

## 项目架构

- **Redis**: 用于缓存资源信息，提高访问速度
- **MinIO**: 用于存储和管理上传的文件，支持高可用和扩展性

## 启动服务

### 使用 Docker Compose 启动

项目根目录下已提供 `docker-compose.yml` 文件，可以直接使用以下命令启动服务：

```bash
docker-compose up -d
```

这将启动：

- Redis 服务（端口 6379）
- MinIO 服务（API 端口 9000，控制台端口 9001）

### 手动安装

#### Redis 安装

1. 下载并安装 Redis: https://redis.io/download
2. 启动 Redis 服务：
   ```bash
   redis-server
   ```

#### MinIO 安装

1. 下载 MinIO: https://min.io/download
2. 启动 MinIO 服务：
   ```bash
   minio server /data --console-address ":9001"
   ```

## 配置说明

### Redis 配置

应用程序配置文件 `application.properties` 中的 Redis 相关配置：

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
spring.redis.timeout=5000
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-wait=-1
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

### MinIO 配置

应用程序配置文件 `application.properties` 中的 MinIO 相关配置：

```properties
minio.endpoint=http://127.0.0.1:9000
minio.accessKey=minioadmin
minio.secretKey=minioadmin
minio.bucket=resources
```

## 功能说明

### Redis 缓存功能

1. **资源信息缓存**：资源详情页面的数据会被缓存，减少数据库查询
2. **缓存过期时间**：默认缓存有效期为 1 小时
3. **缓存自动更新**：当资源信息更新时，相关缓存会自动失效

### MinIO 存储功能

1. **文件上传**：所有上传的文件会存储到 MinIO 中
2. **文件预览**：系统使用 MinIO 提供的临时 URL 进行文件预览
3. **文件下载**：通过 MinIO 提供高性能的文件下载服务
4. **Office 文件预览**：集成 Microsoft Office Online 进行 Office 文件预览

## 访问服务

- MinIO 控制台: http://localhost:9001 (用户名: minioadmin, 密码: minioadmin)
- 应用程序: http://localhost:8088

## 注意事项

1. 首次使用时，系统会自动在 MinIO 中创建名为 "resources" 的存储桶
2. 更新系统后，旧的文件仍会保留在本地文件系统中，建议手动迁移到 MinIO
3. Redis 和 MinIO 服务必须先于应用程序启动
