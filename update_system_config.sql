-- 更新系统配置表的脚本
-- 如果你的数据库中已经有system_config表，运行此脚本来更新数据

USE `kcsz`;

-- 清空原有配置
DELETE FROM `system_config`;

-- 插入新的配置（只保留功能设置和上传设置）
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
-- 功能设置
('register_open', 'true', '是否开放注册'),
('review_required', 'true', '是否需要审核上传的资源'),
('enable_comments', 'true', '是否启用评论功能'),
-- 上传设置
('upload_size_limit', '20', '上传文件大小限制(MB)'),
('allowed_file_types', 'pdf,doc,docx,ppt,pptx,xls,xlsx,txt,jpg,jpeg,png,gif,mp4,mp3,zip,rar', '允许上传的文件类型');

-- 修改update_time字段，添加自动更新
ALTER TABLE `system_config` 
MODIFY COLUMN `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 查询验证
SELECT * FROM `system_config`; 