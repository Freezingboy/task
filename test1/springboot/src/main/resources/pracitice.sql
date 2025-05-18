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

 Date: 11/05/2025 18:04:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `manager_id` bigint(20) NULL DEFAULT NULL,
  `parent_id` bigint(20) NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '1', 1, NULL, 1, NULL, NULL);
INSERT INTO `department` VALUES (2, '2', 2, 1, 1, NULL, NULL);
INSERT INTO `department` VALUES (3, '财务部', 3, 1, 1, NULL, NULL);
INSERT INTO `department` VALUES (4, '4', 4, 3, 1, NULL, NULL);

-- ----------------------------
-- Table structure for emp_dep_relation
-- ----------------------------
DROP TABLE IF EXISTS `emp_dep_relation`;
CREATE TABLE `emp_dep_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `employee_id` bigint(20) NULL DEFAULT NULL,
  `department_id` bigint(20) NULL DEFAULT NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `department_id`(`department_id`) USING BTREE,
  INDEX `employee_id`(`employee_id`) USING BTREE,
  CONSTRAINT `emp_dep_relation_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `emp_dep_relation_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emp_dep_relation
-- ----------------------------
INSERT INTO `emp_dep_relation` VALUES (1, 1, 1, NULL, NULL, 1, NULL, NULL);
INSERT INTO `emp_dep_relation` VALUES (2, 2, 2, NULL, NULL, 1, NULL, NULL);
INSERT INTO `emp_dep_relation` VALUES (3, 3, 3, NULL, NULL, 1, NULL, NULL);
INSERT INTO `emp_dep_relation` VALUES (4, 4, 4, NULL, NULL, 1, NULL, NULL);

-- ----------------------------
-- Table structure for emp_pro_relation
-- ----------------------------
DROP TABLE IF EXISTS `emp_pro_relation`;
CREATE TABLE `emp_pro_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `employee_id` bigint(20) NULL DEFAULT NULL,
  `project_id` bigint(20) NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `project_id`(`project_id`) USING BTREE,
  INDEX `employee_id`(`employee_id`) USING BTREE,
  CONSTRAINT `emp_pro_relation_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `emp_pro_relation_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emp_pro_relation
-- ----------------------------

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `birth_date` date NULL DEFAULT NULL,
  `hire_date` date NULL DEFAULT NULL,
  `maneger_id` bigint(20) NULL DEFAULT NULL,
  `position` bigint(20) NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `position`(`position`) USING BTREE,
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`position`) REFERENCES `position` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, '韩磊', '男', '13599991111', 'hanlei@xm.com', '1991-01-01', '2008-08-08', NULL, 1, 1, '2008-08-08 00:00:00', '2018-08-08 12:00:01');
INSERT INTO `employee` VALUES (2, '张三', '男', '18688880169', 'zhangsan@xm.com', '1992-02-02', '2010-01-08', 1, 2, 1, '2025-04-29 17:21:00', '2025-05-23 17:21:04');
INSERT INTO `employee` VALUES (3, '3', NULL, NULL, '3', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee` VALUES (4, '4', NULL, NULL, '4', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee` VALUES (5, '5555', NULL, NULL, '5', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for position
-- ----------------------------
DROP TABLE IF EXISTS `position`;
CREATE TABLE `position`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of position
-- ----------------------------
INSERT INTO `position` VALUES (1, '1', 1, '2025-05-09 17:19:50', '2025-05-24 17:19:54');
INSERT INTO `position` VALUES (2, '2', 2, '2025-05-14 17:20:03', '2025-05-13 17:20:05');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `num` int(11) NULL DEFAULT NULL COMMENT '商品库存数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '1', 0);
INSERT INTO `product` VALUES ('11', '2', 2);
INSERT INTO `product` VALUES ('2', '2', 2);
INSERT INTO `product` VALUES ('4', '2', 100);

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `manager_id` bigint(20) NULL DEFAULT NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `gmt_created` datetime NULL DEFAULT NULL,
  `gmt_modify` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
