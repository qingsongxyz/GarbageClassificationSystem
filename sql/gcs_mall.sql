/*
Navicat MySQL Data Transfer

Source Server         : docker_mysql_master
Source Server Version : 80023
Source Host           : 39.107.115.218:3306
Source Database       : gcs_mall

Target Server Type    : MYSQL
Target Server Version : 80023
File Encoding         : 65001

Date: 2023-05-08 13:38:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for good
-- ----------------------------
DROP TABLE IF EXISTS `good`;
CREATE TABLE `good` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '商品名称',
  `category_id` bigint DEFAULT NULL COMMENT '商品种类id',
  `image` varchar(255) NOT NULL COMMENT '商品图片',
  `score` int unsigned NOT NULL COMMENT '商品所需积分',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

-- ----------------------------
-- Table structure for good_category
-- ----------------------------
DROP TABLE IF EXISTS `good_category`;
CREATE TABLE `good_category` (
  `id` bigint NOT NULL COMMENT '主键',
  `category` varchar(50) NOT NULL COMMENT '商品种类',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品种类表';

-- ----------------------------
-- Table structure for storage
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `good_id` bigint NOT NULL COMMENT '商品id',
  `storage` int NOT NULL COMMENT '商品库存',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存表';

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
