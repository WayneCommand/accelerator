SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for u_device_token
-- ----------------------------
DROP TABLE IF EXISTS `u_device_token`;
CREATE TABLE `u_device_token`  (
  `u_id` int(11) NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '令牌',
  `device` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备',
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '位置',
  `first_time` datetime(0) NULL DEFAULT NULL COMMENT '第一次出现的时间',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后一次出现的时间',
  `active_count` int(11) NULL DEFAULT NULL COMMENT '活跃计数',
  `create_time` datetime(0) NOT NULL,
  `modify_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`u_id`) USING BTREE
)

-- ----------------------------
-- Table structure for u_user_account
-- ----------------------------
DROP TABLE IF EXISTS `u_user_account`;
CREATE TABLE `u_user_account`  (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `recovery_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '辅助邮箱',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
  `phone_to_login` int(1) NOT NULL COMMENT '手机登录标识',
  `two_step_verify` int(1) NOT NULL COMMENT '二步验证',
  `password_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '上次密码修改时间',
  `phone_modify_time` datetime(0) NULL DEFAULT NULL COMMENT '上次手机修改时间',
  `create_time` datetime(0) NOT NULL,
  `modify_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`u_id`) USING BTREE
) AUTO_INCREMENT = 3

-- ----------------------------
-- Records of u_user_account
-- ----------------------------
INSERT INTO `u_user_account` VALUES (2, 'shenlan', '{bcrypt}$2a$10$Fxzx2fhvSufRsTGksBeCvu5Y59LZECpDzvf/30YW0zOWx0w3nMkCy', NULL, NULL, NULL, 0, 0, NULL, NULL, '2020-05-12 10:18:11', NULL);

-- ----------------------------
-- Table structure for u_user_profile
-- ----------------------------
DROP TABLE IF EXISTS `u_user_profile`;
CREATE TABLE `u_user_profile`  (
  `u_id` int(11) NOT NULL COMMENT '用户唯一ID',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `theme` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主题',
  `create_time` datetime(0) NOT NULL,
  `modify_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`u_id`) USING BTREE
)

-- ----------------------------
-- Records of u_user_profile
-- ----------------------------
INSERT INTO `u_user_profile` VALUES (2, NULL, NULL, NULL, '2020-05-12 10:18:11', NULL);

SET FOREIGN_KEY_CHECKS = 1;
