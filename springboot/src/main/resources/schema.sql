-- 创建公告表
CREATE TABLE IF NOT EXISTS `announcement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容',
  `type` int(11) DEFAULT '0' COMMENT '公告类型 0-普通公告，1-重要公告，2-紧急公告',
  `priority` int(11) DEFAULT '0' COMMENT '优先级，数字越大优先级越高',
  `publisher` varchar(255) DEFAULT NULL COMMENT '发布者',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` int(11) DEFAULT '0' COMMENT '状态 0-未发布，1-已发布',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- 添加一些示例公告数据
INSERT INTO `announcement` (`title`, `content`, `type`, `priority`, `publisher`, `publish_time`, `create_time`, `update_time`, `status`) VALUES
('系统上线通知', '尊敬的用户，我们的教学资源管理系统已正式上线，欢迎使用！', 1, 10, 'admin', NOW(), NOW(), NOW(), 1),
('资源上传指南', '请按照系统提示上传您的教学资源，支持多种文件格式。详细使用指南请查看帮助文档。', 0, 5, 'admin', NOW(), NOW(), NOW(), 1),
('系统维护通知', '系统将于本周日凌晨2:00-4:00进行维护升级，期间可能无法访问，请提前做好准备。', 2, 15, 'admin', NOW(), NOW(), NOW(), 1),
('教学资源分享活动', '欢迎参与本月的教学资源分享活动，上传优质资源即可获得积分奖励！', 0, 8, 'admin', NOW(), NOW(), NOW(), 1); 