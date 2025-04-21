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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `course` */

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `resource` */

insert  into `resource`(`resource_id`,`title`,`description`,`file_path`,`format`,`upload_time`,`download_count`,`rating`,`uploader_id`,`review_status`,`file_name`,`file_size`,`view_count`,`type`,`tags`) values 
(1,'思政课程资源1','这是一个关于思政的课程资源','/resources/course1.pdf','PDF','2025-04-19 18:31:11',2,0,2,'approved','course1.pdf',NULL,0,'计算机','[\"爱国主义\",\"工科\",\"PDF\"]'),
(2,'思政课程资源2','另一个思政课程资源','/resources/course2.mp4','MP4','2025-04-19 18:31:11',0,0,2,'approved','course2.mp4',NULL,1,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(3,'123456','adfsd','uploads/resources/9a4cf6be-a85a-4096-b225-32ec65f132db.pdf','.pdf','2025-04-20 21:36:40',2,0,1,'approved','基于B_S的思政课程资源共享平台设计与实现_孙媛媛.pdf',190971,4,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(4,'shiping','dsaff','uploads/resources/3e667b40-d8d7-4724-be5c-e4ce704cb681.mp4','.mp4','2025-04-20 22:02:26',8,0,1,'approved','1.mp4',5478601,6,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(5,'dfasd','dsaf','uploads/resources/a4381347-0ca1-4c38-b248-cbd9a8f50a1f.mp4','.mp4','2025-04-20 23:42:38',1,0,1,'approved','11650702040740.mp4',5478601,3,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(6,'shipingziyaun','dsaf','uploads/resources/14aaba27-5031-488f-9f87-1b485bad85f0.mp4','.mp4','2025-04-21 01:55:45',1,0,1,'approved','21650702318642.mp4',3894184,3,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(7,'tupian','asdf','uploads/resources/73bb1399-04fd-4df6-8c82-2e966b39aa93.jpg','.jpg','2025-04-21 02:03:10',10,0,1,'approved','gaoxiaodongtai12.jpg',29168,10,'计算机','[\"工科\",\"PDF\",\"爱国教育\"]'),
(8,'图片','122','uploads/resources/82409604-a467-49db-a68d-04db6ca8e442.jpg','.jpg','2025-04-21 03:05:24',0,0,1,'pending','gaoxiaodongtai12.jpg',29168,5,'通信','[\"计算机\",\"爱国主义\"]'),
(9,'word','dad','uploads/resources/82caec11-eabd-4f08-9ad9-c14cb4845fdc.docx','.docx','2025-04-21 03:09:57',1,0,1,'pending','开题报告模板.docx',21088,2,'通信','[\"爱国主义\"]'),
(10,'doc','dsa','uploads/resources/26227a1c-444f-4908-81d2-c2d69c99145c.docx','.docx','2025-04-21 03:10:56',2,0,1,'pending','开题报告模板.docx',21088,10,'通信','[\"计算机\"]'),
(11,'PDf','sadf','uploads/resources/7c8db2d8-3e20-450b-8831-8022bbc5fe2c.pdf','.pdf','2025-04-21 03:15:20',0,0,1,'pending','计0219王旭旭_课程思政资源管理系统设计与实现.pdf',177459,7,'通信','[\"PDF\"]');

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

insert  into `resource_tag`(`id`,`resource_id`,`tag_id`) values 
(1,1,1),
(2,1,2),
(3,1,3);

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

insert  into `review`(`review_id`,`resource_id`,`reviewer_id`,`review_time`,`review_opinion`,`review_result`) values 
(1,1,1,'2025-04-19 18:31:11','审核通过','approved');

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

insert  into `user_action`(`action_id`,`user_id`,`resource_id`,`action_type`,`action_time`,`comment_content`) values 
(1,3,1,'download','2025-04-19 18:31:11',NULL),
(2,3,1,'comment','2025-04-19 18:31:11','这个资源很有用');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
