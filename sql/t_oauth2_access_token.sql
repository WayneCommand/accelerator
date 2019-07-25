/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 100315
 Source Host           : localhost:3306
 Source Schema         : mycloud

 Target Server Type    : MySQL
 Target Server Version : 100315
 File Encoding         : 65001

 Date: 25/07/2019 17:06:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_oauth2_access_token
-- ----------------------------
DROP TABLE IF EXISTS `t_oauth2_access_token`;
CREATE TABLE `t_oauth2_access_token`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `access_token` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '颁发的token',
  `u_id` int(11) NOT NULL COMMENT '系统内的用户id',
  `client_primary_id` int(11) NOT NULL,
  `expired_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = Aria CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Page;

SET FOREIGN_KEY_CHECKS = 1;
