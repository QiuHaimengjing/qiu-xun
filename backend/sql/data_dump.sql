-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: qiu
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `tag_name` varchar(256) NOT NULL COMMENT '标签名称',
  `user_id` bigint DEFAULT NULL COMMENT '上传标签的用户id',
  `parent_id` bigint DEFAULT NULL COMMENT '父标签id',
  `is_parent` tinyint NOT NULL DEFAULT '0' COMMENT '0 - 不是，1 - 父标签',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0 1（默认值0）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_name_index` (`tag_name`),
  KEY `parent_id_index` (`parent_id`),
  KEY `user_id_index` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'方向',1867903931975606274,NULL,1,'2024-12-14 21:37:49','2024-12-14 21:37:49',0),(2,'前端',1867903931975606274,1,0,'2024-12-14 21:38:10','2024-12-14 21:38:10',0),(3,'后端',1867903931975606274,1,0,'2024-12-14 21:38:16','2024-12-14 21:38:16',0),(4,'身份',1867787725041987585,NULL,1,'2024-12-14 22:06:28','2024-12-14 22:06:28',0),(5,'大学',1867787725041987585,4,0,'2024-12-14 22:06:41','2024-12-14 22:06:41',0),(6,'职场人',1867787725041987585,4,0,'2024-12-14 22:07:03','2024-12-14 22:07:03',0),(7,'研究生',1867787725041987585,4,0,'2024-12-14 22:07:12','2024-12-14 22:07:12',0);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '队伍id',
  `name` varchar(256) NOT NULL COMMENT '队伍名称',
  `description` varchar(1024) DEFAULT NULL COMMENT '队伍描述',
  `max_num` int NOT NULL DEFAULT '1' COMMENT '最大人数',
  `current_num` int NOT NULL DEFAULT '0' COMMENT '当前人数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `leader` bigint NOT NULL COMMENT '创建人id',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0-公开，1-私有，2-加密',
  `password` varchar(512) DEFAULT NULL COMMENT '队伍密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0 1（默认值0）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='队伍';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (1,'牛马小队','牛牛马马，一年又一年',3,2,'2025-12-14 21:06:00',1867787725041987585,0,'','2024-12-14 21:06:47','2024-12-14 21:28:20',0),(2,'141部队','说点什么呢',5,1,'2025-12-14 21:28:00',1867903931975606274,0,'','2024-12-14 21:29:00','2024-12-14 21:29:00',0);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_account` varchar(256) DEFAULT NULL COMMENT '账号',
  `username` varchar(256) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(1024) DEFAULT NULL COMMENT '头像',
  `gender` tinyint DEFAULT NULL COMMENT '性别，1-男，0-女',
  `user_password` varchar(512) NOT NULL COMMENT '密码',
  `salt` varchar(256) DEFAULT NULL COMMENT '加密盐',
  `phone` varchar(128) DEFAULT NULL COMMENT '电话',
  `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `profile` varchar(1024) DEFAULT NULL COMMENT '用户简介',
  `tags` varchar(512) DEFAULT NULL COMMENT '用户标签（json字符串：["Java", "男"]）',
  `user_role` tinyint NOT NULL DEFAULT '0' COMMENT '角色 0-普通用户 1-管理员',
  `user_status` int NOT NULL DEFAULT '0' COMMENT '状态，0正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0 1（默认值0）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1867903931975606275 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1867787725041987585,'qiuqiu','邱海梦旌',NULL,1,'391f8d740e5c560c2f69100f247318e1dd265a4d6bf0a61b030abb3347211d9e','8uA5wnlhAwRWi9HTaAvyqg==','12345678912','1234@mail.com','啊啊啊','[\"前端\"]',0,0,'2024-12-14 12:24:30','2024-12-14 22:04:39',0),(1867903931975606274,'zzzz','吱吱吱吱',NULL,1,'15cc6f8f825995f5f185ee53ee51b9f1cbff6cb95e98842a6fecfbd462a3a1a3','Lnc3kZwqQvp+zNYJnKGhWQ==','12345676543','12438@mail.com','嗨想我了吗','[\"前端\",\"后端\"]',0,0,'2024-12-14 20:06:16','2024-12-14 22:03:01',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_team`
--

DROP TABLE IF EXISTS `user_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户队伍关系id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `team_id` bigint NOT NULL COMMENT '队伍id',
  `is_leader` tinyint NOT NULL DEFAULT '0' COMMENT '是否为创建者（0否，1是）',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0 1（默认值0）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户队伍关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_team`
--

LOCK TABLES `user_team` WRITE;
/*!40000 ALTER TABLE `user_team` DISABLE KEYS */;
INSERT INTO `user_team` VALUES (1,1867787725041987585,1,1,'2024-12-14 21:06:47','2024-12-14 21:06:47','2024-12-14 21:06:47',0),(2,1867903931975606274,1,0,'2024-12-14 21:26:35','2024-12-14 21:26:35','2024-12-14 21:28:06',0),(3,1867903931975606274,2,1,'2024-12-14 21:29:00','2024-12-14 21:29:00','2024-12-14 21:29:00',0);
/*!40000 ALTER TABLE `user_team` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-15 13:41:25
