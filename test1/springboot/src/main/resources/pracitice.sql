/*
 Navicat Premium Data Transfer

 Source Server         : locolhost
 Source Server Type    : MySQL
 Source Server Version : 50713 (5.7.13)
 Source Host           : localhost:3306
 Source Schema         : pracitice

 Target Server Type    : MySQL
 Target Server Version : 50713 (5.7.13)
 File Encoding         : 65001

 Date: 18/05/2025 15:45:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `warn_id` int(11) NULL DEFAULT NULL COMMENT '规则编号',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `battery_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池类型',
  `warn_rule` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警规则数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule
-- ----------------------------
INSERT INTO `rule` VALUES (1, 1, '电压差报警', '三元电池', '5|3|1|0.6|0.2');
INSERT INTO `rule` VALUES (2, 1, '电压差报警', '铁锂电池', '2|1|0.7|0.4|0.2');
INSERT INTO `rule` VALUES (3, 2, '电流差报警', '三元电池', '3|1|0.2');
INSERT INTO `rule` VALUES (4, 2, '电流差报警', '铁锂电池', '1|0.5|0.2');

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `vid` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '汽车id',
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '车架编号',
  `battery_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池类型',
  `total_mil` int(11) NULL DEFAULT NULL COMMENT '总里程',
  `bh_state` int(11) NULL DEFAULT NULL COMMENT '电池健康状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_vid`(`vid`) USING BTREE COMMENT '对vid进行唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vehicle
-- ----------------------------
INSERT INTO `vehicle` VALUES ('576c57b6ae5c44f0', 1, '三元电池', 100, 100);
INSERT INTO `vehicle` VALUES ('67fc2bf4926c4766', 2, '铁锂电池', 600, 95);
INSERT INTO `vehicle` VALUES ('b63a6e4550f04a2d', 3, '三元电池', 300, 98);

-- ----------------------------
-- Table structure for warn_message
-- ----------------------------
DROP TABLE IF EXISTS `warn_message`;
CREATE TABLE `warn_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '警告信息id',
  `car_id` int(11) NULL DEFAULT NULL COMMENT '车架编号',
  `battery_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池类型',
  `warn_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '警告名',
  `warn_level` int(11) NULL DEFAULT NULL COMMENT '警告等级',
  `signal_id` int(11) NULL DEFAULT NULL COMMENT '信号id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warn_message
-- ----------------------------
INSERT INTO `warn_message` VALUES (7, 1, '三元电池', '电压差报警', 0, 32);
INSERT INTO `warn_message` VALUES (8, 2, '铁锂电池', '电流差报警', 2, 33);
INSERT INTO `warn_message` VALUES (9, 3, '三元电池', '电压差报警', 2, 34);
INSERT INTO `warn_message` VALUES (10, 3, '三元电池', '电流差报警', 2, 34);
INSERT INTO `warn_message` VALUES (11, 3, '三元电池', '电压差报警', 0, 35);
INSERT INTO `warn_message` VALUES (12, 3, '三元电池', '电压差报警', 0, 36);
INSERT INTO `warn_message` VALUES (13, 1, '三元电池', '电压差报警', 0, 37);
INSERT INTO `warn_message` VALUES (14, 2, '铁锂电池', '电流差报警', 2, 38);
INSERT INTO `warn_message` VALUES (15, 3, '三元电池', '电压差报警', 2, 39);
INSERT INTO `warn_message` VALUES (16, 3, '三元电池', '电流差报警', 2, 39);

-- ----------------------------
-- Table structure for warn_signal
-- ----------------------------
DROP TABLE IF EXISTS `warn_signal`;
CREATE TABLE `warn_signal`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '信号id',
  `car_id` int(11) NOT NULL COMMENT '车架编号',
  `warn_id` int(11) NULL DEFAULT NULL COMMENT '规则编号（不传的话遍历所有规则）',
  `cwsignal` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '信号',
  `signal_state` int(11) NULL DEFAULT NULL COMMENT '处理状态 1代表处理 0代表未处理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warn_signal
-- ----------------------------
INSERT INTO `warn_signal` VALUES (32, 1, 1, '{\"Mx\":12.0,\"Mi\":0.6}', 1);
INSERT INTO `warn_signal` VALUES (33, 2, 2, '{\"Ix\":12.0,\"Ii\":11.7}', 1);
INSERT INTO `warn_signal` VALUES (34, 3, NULL, '{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}', 1);
INSERT INTO `warn_signal` VALUES (36, 3, 1, '{\"Mx\":12.0,\"Mi\":0.6}', 1);
INSERT INTO `warn_signal` VALUES (37, 1, 1, '{\"Mx\":12.0,\"Mi\":0.6}', 1);
INSERT INTO `warn_signal` VALUES (38, 2, 2, '{\"Ix\":12.0,\"Ii\":11.7}', 1);
INSERT INTO `warn_signal` VALUES (39, 3, NULL, '{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}', 1);
INSERT INTO `warn_signal` VALUES (41, 3, 1, '{\"Mx\":12.0,\"Mi\":0.6}', 0);

SET FOREIGN_KEY_CHECKS = 1;
