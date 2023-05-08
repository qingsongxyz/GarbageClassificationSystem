/*
Navicat MySQL Data Transfer

Source Server         : docker_mysql_master
Source Server Version : 80023
Source Host           : 39.107.115.218:3306
Source Database       : gcs_garbage

Target Server Type    : MYSQL
Target Server Version : 80023
File Encoding         : 65001

Date: 2023-05-08 13:38:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for garbage
-- ----------------------------
DROP TABLE IF EXISTS `garbage`;
CREATE TABLE `garbage` (
  `id` bigint NOT NULL COMMENT '垃圾表主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '垃圾名称',
  `category_id` bigint NOT NULL COMMENT '垃圾分类id',
  `unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '垃圾单位',
  `score` int NOT NULL COMMENT '每一个单位的积分',
  `image` varchar(255) NOT NULL COMMENT '图片',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾信息表';

-- ----------------------------
-- Table structure for garbage_category
-- ----------------------------
DROP TABLE IF EXISTS `garbage_category`;
CREATE TABLE `garbage_category` (
  `id` bigint NOT NULL COMMENT '垃圾分类主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类简介',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾分类表';
