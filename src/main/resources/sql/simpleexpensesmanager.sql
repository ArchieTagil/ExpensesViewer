/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : simpleexpensesmanager

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 04/04/2023 14:48:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for expenses
-- ----------------------------
DROP TABLE IF EXISTS `expenses`;
CREATE TABLE `expenses`  (
  `expences_id` int NOT NULL AUTO_INCREMENT,
  `date` datetime(0) NULL DEFAULT NULL,
  `dedit_wallet_id` int NULL DEFAULT NULL,
  `expences_category_id` int NULL DEFAULT NULL,
  `amount` int NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`expences_id`) USING BTREE,
  INDEX `debet_wallet_id`(`dedit_wallet_id`) USING BTREE,
  INDEX `expenses_category_id`(`expences_category_id`) USING BTREE,
  CONSTRAINT `debet_wallet_id` FOREIGN KEY (`dedit_wallet_id`) REFERENCES `wallets_list` (`wallet_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `expenses_category_id` FOREIGN KEY (`expences_category_id`) REFERENCES `expenses_category` (`expenses_category_id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expenses
-- ----------------------------
INSERT INTO `expenses` VALUES (1, '2023-04-01 14:17:17', 1, 1, 400, 'Колбаса и сыр');
INSERT INTO `expenses` VALUES (2, '2023-04-02 14:18:22', 1, 2, 150, 'Кефир и молоко');

-- ----------------------------
-- Table structure for expenses_category
-- ----------------------------
DROP TABLE IF EXISTS `expenses_category`;
CREATE TABLE `expenses_category`  (
  `expenses_category_id` int NOT NULL AUTO_INCREMENT,
  `expenses_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`expenses_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expenses_category
-- ----------------------------
INSERT INTO `expenses_category` VALUES (1, 'Продукты');
INSERT INTO `expenses_category` VALUES (2, '');
INSERT INTO `expenses_category` VALUES (3, 'Авто');
INSERT INTO `expenses_category` VALUES (4, 'Одежда');
INSERT INTO `expenses_category` VALUES (5, 'Бытовая техника / мебель');
INSERT INTO `expenses_category` VALUES (6, NULL);

-- ----------------------------
-- Table structure for income
-- ----------------------------
DROP TABLE IF EXISTS `income`;
CREATE TABLE `income`  (
  `income_id` int NOT NULL AUTO_INCREMENT,
  `date` datetime(0) NULL DEFAULT NULL,
  `wallet_id` int NULL DEFAULT NULL,
  `income_category_id` int NULL DEFAULT NULL,
  `amount` int NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`income_id`) USING BTREE,
  INDEX `wallet_id`(`wallet_id`) USING BTREE,
  INDEX `income_category_id`(`income_category_id`) USING BTREE,
  CONSTRAINT `income_category_id` FOREIGN KEY (`income_category_id`) REFERENCES `income_category` (`income_category_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `wallet_id` FOREIGN KEY (`wallet_id`) REFERENCES `wallets_list` (`wallet_id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of income
-- ----------------------------
INSERT INTO `income` VALUES (2, '2023-04-01 15:28:32', 1, 1, 17500, 'Зарплата');

-- ----------------------------
-- Table structure for income_category
-- ----------------------------
DROP TABLE IF EXISTS `income_category`;
CREATE TABLE `income_category`  (
  `income_category_id` int NOT NULL AUTO_INCREMENT,
  `income_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`income_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of income_category
-- ----------------------------
INSERT INTO `income_category` VALUES (1, 'Зарплата');
INSERT INTO `income_category` VALUES (2, 'Подработка');
INSERT INTO `income_category` VALUES (3, 'Доход от майнинга');

-- ----------------------------
-- Table structure for movements
-- ----------------------------
DROP TABLE IF EXISTS `movements`;
CREATE TABLE `movements`  (
  `movements_id` int NOT NULL AUTO_INCREMENT,
  `date` datetime(0) NULL DEFAULT NULL,
  `wallet_debit_id` int NULL DEFAULT NULL,
  `wallet_credit_id` int NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`movements_id`) USING BTREE,
  INDEX `movements_ibfk_debit`(`wallet_debit_id`) USING BTREE,
  INDEX `movements_ibfk_credit`(`wallet_credit_id`) USING BTREE,
  CONSTRAINT `movements_ibfk_credit` FOREIGN KEY (`wallet_credit_id`) REFERENCES `wallets_list` (`wallet_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `movements_ibfk_debit` FOREIGN KEY (`wallet_debit_id`) REFERENCES `wallets_list` (`wallet_id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movements
-- ----------------------------

-- ----------------------------
-- Table structure for wallets_list
-- ----------------------------
DROP TABLE IF EXISTS `wallets_list`;
CREATE TABLE `wallets_list`  (
  `wallet_id` int NOT NULL AUTO_INCREMENT,
  `wallet_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `wallet_balance` int NULL DEFAULT NULL,
  PRIMARY KEY (`wallet_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallets_list
-- ----------------------------
INSERT INTO `wallets_list` VALUES (1, 'Карта Сбербанка', 12000);
INSERT INTO `wallets_list` VALUES (2, 'Карта Альфабанк', 35000);
INSERT INTO `wallets_list` VALUES (3, 'Наличные', 2000);
INSERT INTO `wallets_list` VALUES (4, 'Счёт в банке', 150000);

SET FOREIGN_KEY_CHECKS = 1;
