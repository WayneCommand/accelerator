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
)

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
)

SET FOREIGN_KEY_CHECKS = 1;
