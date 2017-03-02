/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50540
Source Host           : localhost:3306
Source Database       : amlegend

Target Server Type    : MYSQL
Target Server Version : 50540
File Encoding         : 65001

Date: 2017-03-02 17:51:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for attack_event
-- ----------------------------
DROP TABLE IF EXISTS `attack_event`;
CREATE TABLE `attack_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `failure_time` datetime DEFAULT NULL COMMENT '被攻击时间',
  `lat` varchar(255) DEFAULT NULL COMMENT '纬度',
  `lot` varchar(255) DEFAULT NULL COMMENT '经度',
  `killer_id` bigint(20) DEFAULT NULL COMMENT '杀手id',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for attack_location
-- ----------------------------
DROP TABLE IF EXISTS `attack_location`;
CREATE TABLE `attack_location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `lat` varchar(255) DEFAULT NULL COMMENT '纬度',
  `lot` varchar(255) DEFAULT NULL COMMENT '经度',
  `flash_time` datetime DEFAULT NULL COMMENT '刷新时间',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `mark_id` varchar(255) DEFAULT NULL COMMENT '手机ime码',
  `user_name` varchar(255) DEFAULT '陌路人甲' COMMENT '用户昵称',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `register_lat` varchar(255) DEFAULT NULL COMMENT '注册纬度',
  `register_lot` varchar(255) DEFAULT NULL COMMENT '注册经度',
  `score` int(10) DEFAULT '0' COMMENT '总成绩',
  `failure` int(10) DEFAULT '0' COMMENT '被攻击总数',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
