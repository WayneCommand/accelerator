-- ----------------------------
-- Table structure for u_device_token
-- ----------------------------
DROP TABLE IF EXISTS `u_device_token`;
CREATE TABLE `u_device_token`  (
   `u_id` int(11) NOT NULL,
   `token` varchar(255) NOT NULL,
   `device_id` varchar(255) NULL DEFAULT NULL,
   `device_type` varchar(255) NULL DEFAULT NULL,
   `device_model` varchar(255) NULL DEFAULT NULL,
   `device_name` varchar(255) NULL DEFAULT NULL,
   `device_version` varchar(255) NULL DEFAULT NULL,
   `device_system` varchar(255) NULL DEFAULT NULL,
   `ip` varchar(255) NULL DEFAULT NULL,
   `location` varchar(255) NULL DEFAULT NULL,
   `location_country` varchar(255) NULL DEFAULT NULL,
   `location_region` varchar(255) NULL DEFAULT NULL,
   `location_city` varchar(255) NULL DEFAULT NULL,
   `first_time` datetime(0) NOT NULL,
   `last_time` datetime(0) NULL DEFAULT NULL,
   `active_count` int(11) NULL DEFAULT NULL,
   `create_time` datetime(0) NOT NULL,
   `modify_time` datetime(0) NULL DEFAULT NULL
);
-- ----------------------------
-- Table structure for u_user_account
-- ----------------------------
DROP TABLE IF EXISTS `u_user_account`;
CREATE TABLE `u_user_account`  (
   `u_id` integer primary key autoincrement,
   `account` varchar(255) NOT NULL,
   `password` varchar(255) NOT NULL,
   `email` varchar(255) NULL DEFAULT NULL,
   `recovery_email` varchar(255) NULL DEFAULT NULL,
   `recovery_phone` varchar(255) NULL DEFAULT NULL ,
   `phone_to_login` int(1) NOT NULL ,
   `two_step_verify` int(1) NOT NULL ,
   `password_modify_time` datetime(0) NULL DEFAULT NULL,
   `phone_modify_time` datetime(0) NULL DEFAULT NULL,
   `create_time` datetime(0) NOT NULL,
   `modify_time` datetime(0) NULL DEFAULT NULL
);


-- ----------------------------
-- Table structure for u_user_profile
-- ----------------------------
DROP TABLE IF EXISTS `u_user_profile`;
CREATE TABLE `u_user_profile`  (
   `u_id` int(11) NOT NULL,
   `nickname` varchar(255) NULL DEFAULT NULL,
   `avatar` varchar(255) NULL DEFAULT NULL,
   `email` varchar(255) NULL DEFAULT NULL,
   `phone` varchar(50) NULL DEFAULT NULL,
   `theme` varchar(255) NULL DEFAULT NULL,
   `create_time` datetime(0) NOT NULL,
   `modify_time` datetime(0) NULL DEFAULT NULL,
   PRIMARY KEY (`u_id`)
);