-- ----------------------------
-- Table structure for t_oauth2_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_access_token`;
CREATE TABLE `oauth2_access_token`  (
  `id` integer primary key autoincrement,
  `access_token` varchar(128) NOT NULL,
  `u_id` int(11) NOT NULL,
  `client_primary_id` int(11) NOT NULL,
  `expired_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL
)

-- ----------------------------
-- Table structure for t_oauth2_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_clients`;
CREATE TABLE `oauth2_clients`  (
  `id` integer primary key autoincrement,
  `client_id` varchar(50) NOT NULL UNIQUE,
  `client_name` varchar(50) NOT NULL,
  `client_secret` varchar(100) NOT NULL,
  `callback_url` varchar(255) NOT NULL,
  `logo` varchar(255) NULL DEFAULT NULL,
  `description` varchar(255) NULL DEFAULT NULL,
  `create_time` datetime null default null,
  `modify_time` datetime null default null
)