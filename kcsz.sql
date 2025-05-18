/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.42-log : Database - kcsz
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`kcsz` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `kcsz`;

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
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `file_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `format` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `download_count` int(11) NOT NULL DEFAULT '0',
  `rating` float NOT NULL DEFAULT '0',
  `uploader_id` int(11) NOT NULL,
  `review_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件原始名称',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源类型:教学资源、教学案例、研究成果',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '标签，JSON格式字符串',
  PRIMARY KEY (`resource_id`),
  KEY `uploader_id` (`uploader_id`),
  CONSTRAINT `resource_ibfk_1` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `resource` */

insert  into `resource`(`resource_id`,`title`,`description`,`file_path`,`format`,`upload_time`,`download_count`,`rating`,`uploader_id`,`review_status`,`file_name`,`file_size`,`view_count`,`type`,`tags`) values 
(3,'123456','adfsd','uploads/resources/9a4cf6be-a85a-4096-b225-32ec65f132db.pdf','.pdf','2025-04-20 21:36:40',2,0,1,'approved','基于B_S的思政课程资源共享平台设计与实现_孙媛媛.pdf',190971,4,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(4,'shiping','dsaff','uploads/resources/3e667b40-d8d7-4724-be5c-e4ce704cb681.mp4','.mp4','2025-04-20 22:02:26',8,0,1,'approved','1.mp4',5478601,8,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(5,'dfasd','dsaf','uploads/resources/a4381347-0ca1-4c38-b248-cbd9a8f50a1f.mp4','.mp4','2025-04-20 23:42:38',1,0,1,'approved','11650702040740.mp4',5478601,4,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]');

/*Table structure for table `resource_tag` */

DROP TABLE IF EXISTS `resource_tag`;

CREATE TABLE `resource_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `resource_id` (`resource_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `resource_tag_ibfk_1` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`),
  CONSTRAINT `resource_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `resource_tag` */

/*Table structure for table `review` */

DROP TABLE IF EXISTS `review`;

CREATE TABLE `review` (
  `review_id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` int(11) NOT NULL,
  `reviewer_id` int(11) NOT NULL,
  `review_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `review_opinion` text COLLATE utf8mb4_unicode_ci,
  `review_result` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`review_id`),
  KEY `resource_id` (`resource_id`),
  KEY `reviewer_id` (`reviewer_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`),
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `review` */

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tag_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `tag` */

insert  into `tag`(`tag_id`,`tag_name`,`tag_type`) values 
(1,'爱国主义','theme'),
(2,'工科','subject'),
(3,'PDF','format'),
(4,'爱国教育','subject'),
(6,'计算机','subject'),
(7,'word','format');

/*Table structure for table `type` */

DROP TABLE IF EXISTS `type`;

CREATE TABLE `type` (
  `type_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `uk_type_name` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `type` */

insert  into `type`(`type_id`,`type_name`,`description`,`create_time`,`status`) values 
(1,'计算机','计算机','2025-04-21 15:10:15',1),
(2,'通信','通信','2025-04-21 15:10:20',1),
(3,'人工智能','人工智能','2025-04-21 15:10:39',1);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户真实姓名',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `user` */

insert  into `user`(`user_id`,`username`,`password`,`name`,`role`,`create_time`) values 
(1,'admin_user','123456','姓名1','admin','2025-04-19 18:31:11'),
(2,'teacher_user','123456','姓名2','teacher','2025-04-19 18:31:11'),
(3,'student_user','123456','姓名2','student','2025-04-19 18:31:11'),
(5,'daf','$2a$10$bGfL7VS3LCr7pzAV8Hmdm..vcprCHUyzXVk00EzKtLwBYDqQxHUVq','admin_userdasf ','teacher','2025-04-21 04:22:42'),
(6,'dafda','$2a$10$ZJKFg.VgE//vOwPdaLBjteteXs.Gnik7g364zKdPSsDWBPM0FTi2K','admin_userdasf ','teacher','2025-04-21 04:22:54'),
(8,'zhansan','$2a$10$YEopNjEhoYZIiJi4VRO/YejUETAFIJrluyD1HJTUmf6cV52Sbzt9q','zhansan','student','2025-04-21 04:24:57'),
(9,'dafasfd','$2a$10$LSWrYKzyQ7uoX6hMznvKw.IydvAQe8i7a7NK53lOXDwQxBFLS4vJC','admin','student','2025-04-21 04:31:26'),
(10,'dafdsa','$2a$10$TF.llI/hvMpUY/z//AMhE.oZADa6.ytqXbfxpi0KKCgDJx54i0y.m','dafd ','student','2025-04-21 04:31:49'),
(11,'lisi','$2a$10$SbEMejZY8oG0LpeAtLdwrO5TIc90OMICmRJDRoV3a6XjnIObKhMXy','lisi','teacher','2025-04-21 04:33:29');

/*Table structure for table `user_action` */

DROP TABLE IF EXISTS `user_action`;

CREATE TABLE `user_action` (
  `action_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  `action_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `action_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `comment_content` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`action_id`),
  KEY `user_id` (`user_id`),
  KEY `resource_id` (`resource_id`),
  CONSTRAINT `user_action_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `user_action_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `user_action` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
-- 创建系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('site_title', '课程思政资源管理系统', '网站标题'),
('site_description', '为教师和学生提供优质的课程思政资源', '网站描述'),
('upload_size_limit', '20', '上传文件大小限制(MB)'),
('allowed_file_types', 'pdf,doc,docx,ppt,pptx,xls,xlsx,txt,jpg,jpeg,png,gif,mp4,mp3,zip,rar', '允许上传的文件类型'),
('system_announcement', '欢迎使用课程思政资源管理系统', '系统公告'),
('contact_email', 'admin@example.com', '联系邮箱'),
('review_required', 'true', '是否需要审核上传的资源'),
('enable_comments', 'true', '是否启用评论功能'),
('register_open', 'true', '是否开放注册'); 