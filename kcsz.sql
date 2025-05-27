/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.43-log : Database - kcsz
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`kcsz` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `kcsz`;

/*Table structure for table `announcement` */

DROP TABLE IF EXISTS `announcement`;

CREATE TABLE `announcement` (
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

/*Data for the table `announcement` */

insert  into `announcement`(`id`,`title`,`content`,`type`,`priority`,`publisher`,`publish_time`,`create_time`,`update_time`,`status`) values 
(1,'系统上线通知','尊敬的用户，我们的教学资源管理系统已正式上线，欢迎使用！',1,10,'admin','2025-05-25 14:48:29','2025-05-25 14:48:29','2025-05-25 14:48:29',1),
(2,'资源上传指南','请按照系统提示上传您的教学资源，支持多种文件格式。详细使用指南请查看帮助文档。',0,5,'admin','2025-05-25 14:48:29','2025-05-25 14:48:29','2025-05-25 14:48:29',1),
(3,'系统维护通知','系统将于本周日凌晨2:00-4:00进行维护升级，期间可能无法访问，请提前做好准备。',2,15,'admin','2025-05-25 14:48:29','2025-05-25 14:48:29','2025-05-25 14:48:29',1),
(4,'教学资源分享活动','欢迎参与本月的教学资源分享活动，上传优质资源即可获得积分奖励！',0,8,'admin','2025-05-25 14:48:29','2025-05-25 14:48:29','2025-05-25 14:48:29',1);

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `parent_id` int(11) DEFAULT NULL COMMENT '父评论ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  PRIMARY KEY (`comment_id`),
  KEY `idx_resource` (`resource_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源评论表';

/*Data for the table `comment` */

insert  into `comment`(`comment_id`,`resource_id`,`user_id`,`content`,`parent_id`,`create_time`,`status`) values 
(1,5,1,'11111',NULL,'2025-05-27 04:22:14',1),
(2,20,1,'234',NULL,'2025-05-27 04:25:45',1),
(3,21,14,'11111',NULL,'2025-05-27 04:29:15',1);

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `course_id` int(11) NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `type_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`course_id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_course_type` FOREIGN KEY (`type_id`) REFERENCES `type` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `course` */

insert  into `course`(`course_id`,`course_name`,`description`,`type_id`,`teacher_id`,`create_time`,`status`,`cover_image`) values 
(1,'计算机技术','计算机技术',1,2,'2025-04-21 15:31:35',1,NULL),
(2,'通信技术',NULL,2,2,'2025-04-21 15:41:10',1,'uploads/courses/1c246b5b-d81b-4246-ae18-0a8ed4d01ac0.png'),
(3,'计算机网络基础','本课程介绍计算机网络的基础知识和原理',1,2,'2025-04-21 15:43:01',1,NULL),
(4,'人工智能导论','介绍人工智能的基本概念和应用',3,5,'2025-04-21 15:43:01',1,NULL),
(5,'通信原理','通信系统的基本原理与设计方法',2,11,'2025-04-21 15:43:01',1,NULL);

/*Table structure for table `resource` */

DROP TABLE IF EXISTS `resource`;

CREATE TABLE `resource` (
  `resource_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源标题',
  `description` text COLLATE utf8mb4_unicode_ci,
  `file_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `format` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件格式',
  `type_id` int(11) DEFAULT NULL COMMENT '资源类型ID',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `download_count` int(11) NOT NULL DEFAULT '0' COMMENT '下载次数',
  `rating` float NOT NULL DEFAULT '0' COMMENT '评分',
  `uploader_id` int(11) NOT NULL,
  `review_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件原始名称',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源类型:教学资源、教学案例、研究成果',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '标签，JSON格式字符串',
  PRIMARY KEY (`resource_id`),
  KEY `uploader_id` (`uploader_id`),
  KEY `idx_review_status` (`review_status`),
  KEY `fk_resource_type` (`type_id`),
  CONSTRAINT `fk_resource_type` FOREIGN KEY (`type_id`) REFERENCES `type` (`type_id`) ON DELETE SET NULL,
  CONSTRAINT `resource_ibfk_1` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

/*Data for the table `resource` */

insert  into `resource`(`resource_id`,`title`,`description`,`file_path`,`format`,`type_id`,`upload_time`,`update_time`,`download_count`,`rating`,`uploader_id`,`review_status`,`file_name`,`file_size`,`view_count`,`type`,`tags`) values 
(3,'123456','adfsd','uploads/resources/9a4cf6be-a85a-4096-b225-32ec65f132db.pdf','.pdf',NULL,'2025-04-20 21:36:40',NULL,2,0,1,'approved','基于B_S的思政课程资源共享平台设计与实现_孙媛媛.pdf',190971,5,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(4,'shiping','dsaff','uploads/resources/3e667b40-d8d7-4724-be5c-e4ce704cb681.mp4','.mp4',NULL,'2025-04-20 22:02:26',NULL,8,0,1,'approved','1.mp4',5478601,8,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(5,'dfasd','dsaf','uploads/resources/a4381347-0ca1-4c38-b248-cbd9a8f50a1f.mp4','.mp4',NULL,'2025-04-20 23:42:38','2025-05-27 04:22:03',1,0,1,'approved','11650702040740.mp4',5478601,6,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(13,'阿斯顿发生','爱的色放','uploads/resources/45e41c6c-87a5-41a1-98ba-2201a39cc095.pdf','.pdf',NULL,'2025-05-25 15:23:52',NULL,0,0,1,'approved','系统上传版-2006020320_杨杰.pdf',15748596,0,'计算机','[\"工科\",\"PDF\",\"爱国主义\"]'),
(14,'大三房','的说法','uploads/resources/630eaf64-beed-48d7-bd9e-ff8f7dd60fa3.pdf','.pdf',NULL,'2025-05-25 15:24:32',NULL,0,0,1,'rejected','系统上传版-2006020320_杨杰.pdf',15748596,0,'计算机','[\"工科\",\"PDF\"]'),
(15,'addasf','阿道夫','uploads/resources/a927af3a-0068-4909-96b4-52eaf55c3e60.docx','.docx',NULL,'2025-05-25 15:42:40',NULL,0,0,1,'approved','系统上传版-2006020320-杨杰.docx',6097121,0,'通信','[\"爱国主义\"]'),
(16,'的撒','答复','uploads/resources/2d1d53c7-5a96-4dd6-8523-b10f2d4d0eb3.docx','.docx',NULL,'2025-05-25 16:29:53',NULL,0,0,1,'approved','论文.docx',51482,0,'人工智能',NULL),
(17,'的撒','答复','uploads/resources/e41e5157-377c-4cc6-9cf6-15afb88dbfb2.docx','.docx',NULL,'2025-05-25 16:29:55',NULL,0,0,1,'approved','论文.docx',51482,0,'人工智能',NULL),
(18,'大地','阿达','uploads/resources/d3b9689d-2c45-44c4-8557-93966dc6d9a7.docx','.docx',NULL,'2025-05-27 00:42:49','2025-05-27 04:44:13',0,0,1,'approved','系统上传版-2006020320-杨杰.docx',6097121,3,'人工智能',NULL),
(19,'fada','adsf','uploads/resources/5644a1c8-3dde-4fb4-873e-2d1bdc657ec1.docx','.docx',NULL,'2025-05-27 00:57:26',NULL,0,0,1,'approved','毕业设计.docx',142932,1,'通信',NULL),
(20,'阿迪斯','爱的色放','uploads/resources/f103f486-a1e7-40ad-acef-c5ea62aa9033.doc','.doc',NULL,'2025-05-27 01:29:15','2025-05-27 04:25:37',1,4,14,'approved','500AACF2C1C43A02DFBCB0CF5FA_17ACDF2F_59810.doc',366608,1,'计算机',NULL),
(21,'阿达','发的','uploads/resources/ac93d5d3-3975-49c7-8e14-aeacbdbd7204.doc','.doc',NULL,'2025-05-27 03:26:57','2025-05-27 04:55:11',2,5,14,'pending','1_500AACF2C1C43A02DFBCB0CF5FA_17ACDF2F_59810.doc',366608,31,'人工智能',NULL),
(22,'通信','通信资源','uploads/resources/610a179f-3a6d-43f2-92c5-20321a44b0ec.doc','.doc',NULL,'2025-05-27 03:46:12','2025-05-27 04:36:29',0,5,14,'pending','500AACF2C1C43A02DFBCB0CF5FA_17ACDF2F_59810.doc',366608,2,'通信',NULL);

/*Table structure for table `resource_favorite` */

DROP TABLE IF EXISTS `resource_favorite`;

CREATE TABLE `resource_favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_resource` (`user_id`,`resource_id`),
  KEY `idx_resource` (`resource_id`),
  CONSTRAINT `fk_favorite_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源收藏表';

/*Data for the table `resource_favorite` */

insert  into `resource_favorite`(`id`,`user_id`,`resource_id`,`create_time`) values 
(1,14,22,'2025-05-27 04:36:23'),
(6,14,21,'2025-05-27 04:47:01');

/*Table structure for table `resource_rating` */

DROP TABLE IF EXISTS `resource_rating`;

CREATE TABLE `resource_rating` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `rating` double NOT NULL COMMENT '评分值（0-5）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_user` (`resource_id`,`user_id`),
  KEY `idx_resource` (`resource_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_rating_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rating_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源评分表';

/*Data for the table `resource_rating` */

insert  into `resource_rating`(`id`,`resource_id`,`user_id`,`rating`,`create_time`,`update_time`) values 
(1,21,14,5,'2025-05-27 04:55:05','2025-05-27 04:55:05');

/*Table structure for table `resource_tag` */

DROP TABLE IF EXISTS `resource_tag`;

CREATE TABLE `resource_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `tag_id` int(11) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_tag` (`resource_id`,`tag_id`),
  KEY `resource_id` (`resource_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `fk_rt_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`) ON DELETE CASCADE,
  CONSTRAINT `resource_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源标签关联表';

/*Data for the table `resource_tag` */

/*Table structure for table `review` */

DROP TABLE IF EXISTS `review`;

CREATE TABLE `review` (
  `review_id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `reviewer_id` int(11) NOT NULL COMMENT '审核人ID',
  `review_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `review_opinion` text COLLATE utf8mb4_unicode_ci,
  `review_result` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`review_id`),
  KEY `resource_id` (`resource_id`),
  KEY `reviewer_id` (`reviewer_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`),
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源审核表';

/*Data for the table `review` */

/*Table structure for table `system_config` */

DROP TABLE IF EXISTS `system_config`;

CREATE TABLE `system_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `system_config` */

insert  into `system_config`(`id`,`config_key`,`config_value`,`description`,`create_time`,`update_time`) values 
(10,'register_open','true','是否开放注册','2025-05-25 14:55:43','2025-05-27 01:31:26'),
(11,'review_required','true','是否需要审核上传的资源','2025-05-25 14:55:43','2025-05-27 01:31:27'),
(12,'enable_comments','true','是否启用评论功能','2025-05-25 14:55:43','2025-05-27 01:31:27'),
(13,'upload_size_limit','23','上传文件大小限制(MB)','2025-05-25 14:55:43','2025-05-25 14:59:30'),
(14,'allowed_file_types','pdf,doc,docx,ppt,pptx,xls,xlsx,txt,jpg,jpeg,png,gif,mp4,mp3,zip,rar','允许上传的文件类型','2025-05-25 14:55:43','2025-05-25 14:59:30');

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `tag_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

/*Data for the table `tag` */

insert  into `tag`(`tag_id`,`tag_name`,`tag_type`,`create_time`,`update_time`) values 
(1,'爱国主义','theme','2025-05-27 01:47:50',NULL),
(2,'工科','subject','2025-05-27 01:47:50',NULL),
(3,'PDF','format','2025-05-27 01:47:50',NULL),
(4,'爱国教育','subject','2025-05-27 01:47:50',NULL),
(6,'计算机','subject','2025-05-27 01:47:50',NULL),
(7,'word','format','2025-05-27 01:47:50',NULL);

/*Table structure for table `type` */

DROP TABLE IF EXISTS `type`;

CREATE TABLE `type` (
  `type_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型描述',
  `parent_id` int(11) DEFAULT NULL COMMENT '父类型ID',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `uk_type_name` (`type_name`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_type_parent` FOREIGN KEY (`parent_id`) REFERENCES `type` (`type_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源类型表';

/*Data for the table `type` */

insert  into `type`(`type_id`,`type_name`,`description`,`parent_id`,`sort_order`,`create_time`,`update_time`,`status`) values 
(1,'计算机','计算机',NULL,0,'2025-04-21 15:10:15',NULL,1),
(2,'通信','通信',NULL,0,'2025-04-21 15:10:20',NULL,1),
(3,'人工智能','人工智能',NULL,0,'2025-04-21 15:10:39',NULL,1),
(4,'嵌入式','嵌入式',NULL,0,'2025-05-25 15:37:24',NULL,1),
(5,'计算机科学与技术','',1,0,'2025-05-27 04:19:27','2025-05-27 04:23:54',1);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户真实姓名',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像路径',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `user` */

insert  into `user`(`user_id`,`username`,`password`,`name`,`email`,`phone`,`role`,`status`,`avatar`,`last_login_time`,`create_time`,`update_time`) values 
(1,'admin_user','$2a$10$g7EFHxa0PPKCCQBDynJk0u2np7qsH3cu8anj3H/1BgPPDck7WP006','姓名1',NULL,NULL,'admin',1,NULL,'2025-05-27 03:49:00','2025-04-19 18:31:11','2025-05-27 03:48:58'),
(2,'teacher_user','123456','姓名2',NULL,NULL,'teacher',1,NULL,NULL,'2025-04-19 18:31:11',NULL),
(3,'student_user','123456','姓名2',NULL,NULL,'student',1,NULL,NULL,'2025-04-19 18:31:11',NULL),
(5,'daf','$2a$10$bGfL7VS3LCr7pzAV8Hmdm..vcprCHUyzXVk00EzKtLwBYDqQxHUVq','admin_userdasf ',NULL,NULL,'teacher',1,NULL,NULL,'2025-04-21 04:22:42',NULL),
(6,'dafda','$2a$10$ZJKFg.VgE//vOwPdaLBjteteXs.Gnik7g364zKdPSsDWBPM0FTi2K','admin_userdasf ',NULL,NULL,'teacher',1,NULL,NULL,'2025-04-21 04:22:54',NULL),
(8,'zhansan','$2a$10$YEopNjEhoYZIiJi4VRO/YejUETAFIJrluyD1HJTUmf6cV52Sbzt9q','zhansan',NULL,NULL,'student',1,NULL,NULL,'2025-04-21 04:24:57',NULL),
(9,'dafasfd','$2a$10$LSWrYKzyQ7uoX6hMznvKw.IydvAQe8i7a7NK53lOXDwQxBFLS4vJC','admin',NULL,NULL,'student',1,NULL,NULL,'2025-04-21 04:31:26',NULL),
(10,'dafdsa','$2a$10$TF.llI/hvMpUY/z//AMhE.oZADa6.ytqXbfxpi0KKCgDJx54i0y.m','dafd ',NULL,NULL,'student',1,NULL,NULL,'2025-04-21 04:31:49',NULL),
(11,'lisi','$2a$10$SbEMejZY8oG0LpeAtLdwrO5TIc90OMICmRJDRoV3a6XjnIObKhMXy','lisi',NULL,NULL,'teacher',1,NULL,NULL,'2025-04-21 04:33:29',NULL),
(12,'王旭旭','$2a$10$qleUfTZwezk3qhKfayInweuaiKc4GkF8RwRSNbQfb1OzyxsmgeEsy','wangxuxu',NULL,NULL,'student',1,NULL,NULL,'2025-05-27 01:21:10',NULL),
(13,'admin','123456','admin',NULL,NULL,'admin',1,NULL,NULL,'2025-05-27 01:23:21',NULL),
(14,'老师1','$2a$10$c05XaTHxAH4kjy2H9erR.uk1ECu5WkJAAdbJUM/ipyTvdZVB5XUaG','老师1',NULL,NULL,'teacher',1,NULL,'2025-05-27 04:28:53','2025-05-27 01:28:08','2025-05-27 04:28:51'),
(17,'test1','$2a$10$v36vKBVQW6Yk7rSAxWyhFOQ3I4sq9gdJnrfD1MBIMpjF5inL0PKPK','test1',NULL,NULL,'student',1,NULL,'2025-05-27 02:54:20','2025-05-27 02:42:11','2025-05-27 02:54:18'),
(18,'test2','$2a$10$RNidEvbh6tqlOFMVKt6IMOVpf0K.aKcmJch2FKraqm6J3lUqmOO46','test2',NULL,NULL,'student',1,NULL,'2025-05-27 02:50:24','2025-05-27 02:44:59','2025-05-27 02:50:22'),
(19,'老师2','$2a$10$KDCTedtmB6r8AQYc1g5UjO5qrNHCLMcIXaqvcgBXk/SKO8KLWtqqe','老师2',NULL,NULL,'teacher',1,NULL,'2025-05-27 02:55:13','2025-05-27 02:54:54','2025-05-27 02:55:11');

/*Table structure for table `user_action` */

DROP TABLE IF EXISTS `user_action`;

CREATE TABLE `user_action` (
  `action_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `resource_id` int(11) NOT NULL COMMENT '资源ID',
  `action_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `action_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `comment_content` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`action_id`),
  KEY `user_id` (`user_id`),
  KEY `resource_id` (`resource_id`),
  KEY `idx_action_type` (`action_type`),
  CONSTRAINT `fk_ua_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`) ON DELETE CASCADE,
  CONSTRAINT `user_action_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作记录表';

/*Data for the table `user_action` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
