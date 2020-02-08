/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50549
 Source Host           : 127.0.0.1:3306
 Source Schema         : springbatch

 Target Server Type    : MySQL
 Target Server Version : 50549
 File Encoding         : 65001

 Date: 08/02/2020 13:29:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '晓红', '1##@D', 16);
INSERT INTO `user` VALUES (2, '晓冰', '1@&', 17);
INSERT INTO `user` VALUES (3, '晓菲', '32##2', 20);
INSERT INTO `user` VALUES (4, '晓宇', 'sf52%￥', 20);
INSERT INTO `user` VALUES (5, '晓妨', 'sf52%￥', 20);

SET FOREIGN_KEY_CHECKS = 1;
