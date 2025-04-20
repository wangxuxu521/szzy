-- 创建 user 表
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `name` VARCHAR(50) COMMENT '用户真实姓名',
    `role` VARCHAR(20) NOT NULL CHECK (`role` IN ('teacher', 'student', 'admin')),
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建 resource 表
CREATE TABLE IF NOT EXISTS `resource` (
    `resource_id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `file_path` VARCHAR(255) NOT NULL,
    `format` VARCHAR(20) NOT NULL,
    `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `download_count` INT NOT NULL DEFAULT 0,
    `rating` FLOAT NOT NULL DEFAULT 0,
    `uploader_id` INT NOT NULL,
    `review_status` VARCHAR(20) NOT NULL CHECK (`review_status` IN ('pending', 'approved', 'rejected')),
    `file_name` VARCHAR(255) COMMENT '文件原始名称',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `type` VARCHAR(50) COMMENT '资源类型:教学资源、教学案例、研究成果',
    `tags` TEXT COMMENT '标签，JSON格式字符串',
    FOREIGN KEY (`uploader_id`) REFERENCES `user`(`user_id`)
);

-- 创建 tag 表
CREATE TABLE IF NOT EXISTS `tag` (
    `tag_id` INT AUTO_INCREMENT PRIMARY KEY,
    `tag_name` VARCHAR(50) UNIQUE NOT NULL,
    `tag_type` VARCHAR(20) NOT NULL CHECK (`tag_type` IN ('theme', 'subject', 'format'))
);

-- 创建 resource_tag 表
CREATE TABLE IF NOT EXISTS `resource_tag` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `resource_id` INT NOT NULL,
    `tag_id` INT NOT NULL,
    FOREIGN KEY (`resource_id`) REFERENCES `resource`(`resource_id`),
    FOREIGN KEY (`tag_id`) REFERENCES `tag`(`tag_id`)
);

-- 创建 review 表
CREATE TABLE IF NOT EXISTS `review` (
    `review_id` INT AUTO_INCREMENT PRIMARY KEY,
    `resource_id` INT NOT NULL,
    `reviewer_id` INT NOT NULL,
    `review_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `review_opinion` TEXT,
    `review_result` VARCHAR(20) NOT NULL CHECK (`review_result` IN ('approved', 'rejected')),
    FOREIGN KEY (`resource_id`) REFERENCES `resource`(`resource_id`),
    FOREIGN KEY (`reviewer_id`) REFERENCES `user`(`user_id`)
);

-- 创建 user_action 表
CREATE TABLE IF NOT EXISTS `user_action` (
    `action_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `resource_id` INT NOT NULL,
    `action_type` VARCHAR(20) NOT NULL CHECK (`action_type` IN ('download', 'favorite', 'comment')),
    `action_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `comment_content` TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`),
    FOREIGN KEY (`resource_id`) REFERENCES `resource`(`resource_id`)
);    

-- 插入用户数据
INSERT INTO `user` (`username`, `password`, `name`, `role`, `create_time`)
VALUES 
('admin_user', '123456', '系统管理员', 'admin', NOW()),
('teacher_user', '123456', '张教授', 'teacher', NOW()),
('student_user', '123456', '李同学', 'student', NOW());

-- 插入资源数据
INSERT INTO `resource` (`title`, `description`, `file_path`, `format`, `upload_time`, `download_count`, `rating`, `uploader_id`, `review_status`)
VALUES 
('思政课程资源1', '这是一个关于思政的课程资源', '/resources/course1.pdf', 'PDF', NOW(), 0, 0, 2, 'approved'),
('思政课程资源2', '另一个思政课程资源', '/resources/course2.mp4', 'MP4', NOW(), 0, 0, 2, 'pending');

-- 插入标签数据
INSERT INTO `tag` (`tag_name`, `tag_type`)
VALUES 
('爱国主义', 'theme'),
('工科', 'subject'),
('PDF', 'format');

-- 插入资源标签关联数据
INSERT INTO `resource_tag` (`resource_id`, `tag_id`)
VALUES 
(1, 1),
(1, 2),
(1, 3);

-- 插入审核数据
INSERT INTO `review` (`resource_id`, `reviewer_id`, `review_time`, `review_opinion`, `review_result`)
VALUES 
(1, 1, NOW(), '审核通过', 'approved');

-- 插入用户行为数据
INSERT INTO `user_action` (`user_id`, `resource_id`, `action_type`, `action_time`, `comment_content`)
VALUES 
(3, 1, 'download', NOW(), NULL),
(3, 1, 'comment', NOW(), '这个资源很有用');

-- 更新已有数据
UPDATE `resource` SET 
  `file_name` = SUBSTRING_INDEX(`file_path`, '/', -1),
  `type` = '教学资源' 
WHERE `file_name` IS NULL;

-- 从resource_tag关联表导入标签数据（可选）
-- 这会将已有的标签关联转换为JSON格式存储在resource表中
UPDATE `resource` r 
SET r.`tags` = (
  SELECT CONCAT('[', GROUP_CONCAT('"', t.`tag_name`, '"'), ']')
  FROM `resource_tag` rt 
  JOIN `tag` t ON rt.`tag_id` = t.`tag_id`
  WHERE rt.`resource_id` = r.`resource_id`
  GROUP BY rt.`resource_id`
);    