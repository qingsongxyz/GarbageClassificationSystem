/*
Navicat MySQL Data Transfer

Source Server         : docker_mysql_master
Source Server Version : 80023
Source Host           : 39.107.115.218:3306
Source Database       : gcs_user

Target Server Type    : MYSQL
Target Server Version : 80023
File Encoding         : 65001

Date: 2023-05-08 13:39:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for broadcast
-- ----------------------------
DROP TABLE IF EXISTS `broadcast`;
CREATE TABLE `broadcast` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` longtext NOT NULL COMMENT '公告内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';

-- ----------------------------
-- Table structure for garbage_delivery
-- ----------------------------
DROP TABLE IF EXISTS `garbage_delivery`;
CREATE TABLE `garbage_delivery` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `total` int NOT NULL COMMENT '投递总积分',
  `station_address` varchar(255) NOT NULL COMMENT '投放垃圾站地址',
  `status` tinyint NOT NULL COMMENT '投递状态只能为0~3(0-预发放,1-正常, 2-错误, 3-异常)',
  `addition` varchar(255) DEFAULT NULL COMMENT '附加信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾投递表';

-- ----------------------------
-- Table structure for garbage_delivery_details
-- ----------------------------
DROP TABLE IF EXISTS `garbage_delivery_details`;
CREATE TABLE `garbage_delivery_details` (
  `id` bigint NOT NULL COMMENT '主键',
  `delivery_id` bigint NOT NULL COMMENT '垃圾投递表id',
  `garbage_name` varchar(50) NOT NULL COMMENT '垃圾名称',
  `garbage_image` varchar(255) NOT NULL COMMENT '垃圾图片',
  `garbage_category` varchar(50) NOT NULL COMMENT '垃圾分类',
  `garbage_unit` varchar(10) NOT NULL COMMENT '垃圾单位',
  `garbage_score` int NOT NULL COMMENT '垃圾每单位积分',
  `count` int NOT NULL COMMENT '垃圾单位总量',
  `sum` int NOT NULL COMMENT '垃圾总积分',
  `flag` tinyint NOT NULL COMMENT '投递垃圾是否分类正确',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='垃圾投递详情表';

-- ----------------------------
-- Table structure for market
-- ----------------------------
DROP TABLE IF EXISTS `market`;
CREATE TABLE `market` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';

-- ----------------------------
-- Table structure for market_details
-- ----------------------------
DROP TABLE IF EXISTS `market_details`;
CREATE TABLE `market_details` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `market_id` bigint NOT NULL,
  `good_id` bigint NOT NULL COMMENT '商品id',
  `good_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `good_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品图片',
  `good_score` int NOT NULL COMMENT '商品所需积分',
  `count` int NOT NULL COMMENT '商品数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车详情表';

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `sum` int NOT NULL COMMENT '订单所需总积分',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Table structure for order_details
-- ----------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `good_id` bigint NOT NULL COMMENT '主键',
  `good_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `good_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品图片',
  `good_score` int NOT NULL COMMENT '商品所需积分',
  `count` int NOT NULL COMMENT '商品数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单详情表';

-- ----------------------------
-- Table structure for path
-- ----------------------------
DROP TABLE IF EXISTS `path`;
CREATE TABLE `path` (
  `id` bigint NOT NULL COMMENT '主键',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限路径',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL COMMENT '主键',
  `role` varchar(50) NOT NULL COMMENT '角色英文名',
  `name` varchar(50) NOT NULL COMMENT '角色中文名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Table structure for role_path
-- ----------------------------
DROP TABLE IF EXISTS `role_path`;
CREATE TABLE `role_path` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint DEFAULT NULL COMMENT '角色id',
  `path_id` bigint NOT NULL COMMENT '权限id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限表';

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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `age` int DEFAULT NULL COMMENT '年龄',
  `gender` char(2) DEFAULT NULL COMMENT '性别',
  `signature` longtext COMMENT '个性签名',
  `image` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号',
  `openid` varchar(100) DEFAULT NULL COMMENT '用户对应微信的唯一标识',
  `alipayid` varchar(100) DEFAULT NULL COMMENT '用户对应支付宝的唯一标识',
  `score` bigint NOT NULL DEFAULT '0' COMMENT '积分',
  `account_locked` tinyint NOT NULL DEFAULT '0' COMMENT '用户是否被锁定',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `deleted` tinyint NOT NULL COMMENT '逻辑删除字段',
  `version` bigint NOT NULL COMMENT '乐观锁字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色表';
