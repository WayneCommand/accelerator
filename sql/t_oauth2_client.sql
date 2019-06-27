/*
 Navicat MySQL Data Transfer

 Source Server         : MariaDB_localhost
 Source Server Type    : MariaDB
 Source Server Version : 100315
 Source Host           : localhost:3306
 Source Schema         : mycloud

 Target Server Type    : MariaDB
 Target Server Version : 100315
 File Encoding         : 65001

 Date: 29/05/2019 22:15:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_oauth2_client
-- ----------------------------
DROP TABLE IF EXISTS `t_oauth2_client`;
CREATE TABLE `t_oauth2_client`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `client_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端名称',
  `client_secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端密钥',
  `callback_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '回调地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `clinet_id_unique`(`client_id`) USING BTREE COMMENT '客户端ID的唯一索引'
) ENGINE = Aria AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin PAGE_CHECKSUM = 1 ROW_FORMAT = Page;

SET FOREIGN_KEY_CHECKS = 1;
