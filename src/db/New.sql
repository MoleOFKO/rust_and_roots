-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: rust_and_root
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_statuses`
--

DROP TABLE IF EXISTS `account_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_statuses` (
  `status_id` int NOT NULL AUTO_INCREMENT,
  `status_name` varchar(20) NOT NULL,
  PRIMARY KEY (`status_id`),
  UNIQUE KEY `uq_account_statuses_status_name` (`status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_statuses`
--

LOCK TABLES `account_statuses` WRITE;
/*!40000 ALTER TABLE `account_statuses` DISABLE KEYS */;
INSERT INTO `account_statuses` VALUES (1,'active'),(2,'ban');
/*!40000 ALTER TABLE `account_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `account_name` varchar(255) NOT NULL,
  `role_id` int NOT NULL,
  `email` varchar(255) NOT NULL,
  `passwords` varchar(255) NOT NULL,
  `status_id` int NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uq_accounts_email` (`email`),
  KEY `fk_accounts_role` (`role_id`),
  KEY `fk_accounts_status` (`status_id`),
  CONSTRAINT `fk_accounts_role` FOREIGN KEY (`role_id`) REFERENCES `user_roles` (`role_id`),
  CONSTRAINT `fk_accounts_status` FOREIGN KEY (`status_id`) REFERENCES `account_statuses` (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'koko',1,'hhhhh','123123',1,'2026-02-12 19:47:07','2026-02-27 22:26:31'),(2,'eiei',2,'aaaa','123456',1,'2026-02-13 15:01:57','2026-02-27 22:26:31'),(3,'koo',3,'hhhh','123123',1,'2026-02-11 14:45:22','2026-02-27 22:26:31'),(4,'kk',3,'hhh','123123',1,'2026-02-11 15:04:31','2026-02-27 22:26:31'),(5,'qqq',3,'qqq','123',1,'2026-02-13 16:02:00','2026-02-27 22:26:31'),(6,'yyyyy',3,'yyyyy','123',2,'2026-02-11 19:24:47','2026-02-27 22:26:31'),(7,'',3,'qqq@gmail.com','',2,'2026-02-12 18:40:54','2026-02-27 22:26:31'),(8,'asdf',3,'asd@gmail.com','',1,'2026-02-11 15:30:15','2026-02-27 22:26:31'),(9,'asdfg',3,'asdf@gmail.com','1234',1,'2026-02-11 15:32:01','2026-02-27 22:26:31'),(10,'qwerty',3,'asdfg@gmail.com','',1,'2026-02-11 15:46:51','2026-02-27 22:26:31'),(11,'qwertyu',3,'qwerty@gmail.com','',1,'2026-02-11 15:56:43','2026-02-27 22:26:31'),(12,'qwertyuiop',3,'qwertyuiop@gmail.com','',1,'2026-02-11 15:58:57','2026-02-27 22:26:31'),(13,'q123',3,'qQ123@gmail.com','',1,'2026-02-11 16:10:13','2026-02-27 22:26:31'),(14,'ER123',3,'lol123@gmail.com','Ahref123@',1,'2026-02-11 16:34:29','2026-02-27 22:26:31'),(15,'koshane',3,'asdfasdf@gmail.com','Shirt12!',1,'2026-03-19 01:58:01','2026-03-19 01:58:01'),(16,'HHHHHH',3,'larry@gmail.com','Koshane@11',1,'2026-04-02 07:09:15','2026-04-02 07:09:15'),(17,'Larry',3,'larry562005@gmail.com','Ko123123@',1,'2026-04-02 09:03:02','2026-04-02 09:03:02'),(18,'Yann',3,'yanaungbala5678@gmail.com','Koo123@@',1,'2026-04-07 09:24:04','2026-04-07 09:24:04'),(20,'KoShane!',3,'shanekhant23@gmail.com','Ko123123',1,'2026-04-08 15:51:12','2026-04-08 15:51:12'),(22,'q',3,'q','q',1,'2026-04-19 08:18:35','2026-04-19 08:18:35');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `characters`
--

DROP TABLE IF EXISTS `characters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `characters` (
  `character_id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `player_name` varchar(20) NOT NULL,
  `skin_id` int DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  KEY `idx_characters_account_id` (`account_id`),
  KEY `idx_characters_skin_id` (`skin_id`),
  CONSTRAINT `fk_characters_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `fk_characters_skin` FOREIGN KEY (`skin_id`) REFERENCES `skin` (`skin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `characters`
--

LOCK TABLES `characters` WRITE;
/*!40000 ALTER TABLE `characters` DISABLE KEYS */;
INSERT INTO `characters` VALUES (1,3,'Player1',NULL),(2,15,'koshane',NULL),(3,22,'q',NULL);
/*!40000 ALTER TABLE `characters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_map`
--

DROP TABLE IF EXISTS `game_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_map` (
  `map_id` int NOT NULL AUTO_INCREMENT,
  `map_name` varchar(50) NOT NULL,
  PRIMARY KEY (`map_id`),
  UNIQUE KEY `uq_game_map_map_name` (`map_name`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_map`
--

LOCK TABLES `game_map` WRITE;
/*!40000 ALTER TABLE `game_map` DISABLE KEYS */;
INSERT INTO `game_map` VALUES (6,'d_homemap'),(10,'d_undemap_right'),(7,'d_undermap'),(8,'d_undermap_left'),(9,'d_undermap_noone'),(11,'dhome_L_one_S_two'),(2,'Home_interior'),(1,'home_map'),(3,'left_map'),(12,'left_map_nofour'),(13,'left_map_noone'),(14,'left_map_noone_of_one'),(15,'left_map_nothree'),(16,'left_map_nothree_of_one'),(17,'left_map_notwo'),(18,'left_map_notwo_of_one'),(4,'right_map'),(19,'right_map_nofour'),(20,'right_map_noone'),(21,'right_map_noone_of_one'),(22,'right_map_nothree'),(23,'right_map_nothree_of_one'),(24,'right_map_notwo'),(25,'right_map_notwo_of_one'),(26,'right_map_notwo_of_two'),(5,'upper_map'),(27,'upper_map_nofour'),(28,'upper_map_nothree'),(29,'upper_map_notwo');
/*!40000 ALTER TABLE `game_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_stats`
--

DROP TABLE IF EXISTS `item_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_stats` (
  `item_stats_id` int NOT NULL AUTO_INCREMENT,
  `str_stat` int NOT NULL DEFAULT '0',
  `agi_stat` int NOT NULL DEFAULT '0',
  `dex_stat` int NOT NULL DEFAULT '0',
  `vit_stat` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `stamina` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_stats_id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_stats`
--

LOCK TABLES `item_stats` WRITE;
/*!40000 ALTER TABLE `item_stats` DISABLE KEYS */;
INSERT INTO `item_stats` VALUES (1,1,1,1,1,1,1),(2,0,0,0,0,0,0),(3,0,0,0,0,0,0),(4,0,0,0,0,0,0),(5,0,0,0,0,0,0),(6,0,0,0,0,0,0),(7,0,0,0,0,0,0),(8,0,0,0,0,0,0),(9,0,0,0,0,0,0),(10,0,0,0,0,0,0),(11,0,0,0,0,0,0),(12,0,0,0,0,0,0),(13,0,0,0,0,0,0),(14,0,0,0,0,0,0),(15,0,0,0,0,0,0),(16,0,0,0,0,0,0),(17,0,0,0,0,0,0),(18,0,0,0,0,0,0),(19,0,0,0,0,0,0),(20,0,0,0,0,0,0),(21,0,0,0,0,0,0),(22,0,0,0,0,0,0),(23,0,0,0,0,0,0),(24,0,0,0,0,0,0),(25,0,0,0,0,0,0),(26,0,0,0,0,0,0),(27,0,0,0,0,0,0),(28,0,0,0,0,0,0),(29,0,0,0,0,0,0),(30,0,0,0,0,0,0),(31,0,0,0,0,0,0),(32,0,0,0,0,0,0),(33,0,0,0,0,0,0),(34,0,0,0,0,0,0),(35,0,0,0,0,0,0),(36,0,0,0,0,0,0),(37,0,0,0,0,0,0),(38,0,0,0,0,0,0),(39,0,0,0,0,0,0),(40,0,0,0,0,0,0),(41,0,0,0,0,0,0),(42,0,0,0,0,0,0),(43,0,0,0,0,0,0),(44,0,0,0,0,0,0),(45,0,0,0,0,0,0),(46,0,0,0,0,0,0),(47,0,0,0,0,0,0),(48,0,0,0,0,0,0),(49,0,0,0,0,0,0),(50,0,0,0,0,0,0),(51,0,0,0,0,0,0),(52,0,0,0,0,0,0),(53,0,0,0,0,0,0),(54,0,0,0,0,0,0),(55,0,0,0,0,0,0),(56,0,0,0,0,0,0),(57,0,0,0,0,0,0),(58,0,0,0,0,0,0),(59,0,0,0,0,0,0),(60,0,0,0,0,0,0),(61,0,0,0,0,0,0),(62,0,0,0,0,0,0),(63,0,0,0,0,0,0),(64,0,0,0,0,0,0),(65,0,0,0,0,0,0),(66,0,0,0,0,0,0),(67,0,0,0,0,0,0),(68,0,0,0,0,0,0),(69,0,0,0,0,0,0),(70,0,0,0,0,0,0),(71,0,0,0,0,0,0),(72,0,0,0,0,0,0),(73,0,0,0,0,0,0),(74,0,0,0,0,0,0),(75,0,0,0,0,0,0),(76,0,0,0,0,0,0),(77,0,0,0,0,0,0),(78,0,0,0,0,0,0),(79,0,0,0,0,0,0),(80,0,0,0,0,0,0),(81,0,0,0,0,0,0),(82,0,0,0,0,0,0),(83,0,0,0,0,0,0),(84,0,0,0,0,0,0),(85,0,0,0,0,0,0),(86,0,0,0,0,0,0),(92,0,0,0,0,0,0),(93,0,0,0,0,0,0),(94,0,0,0,0,0,0),(95,0,0,0,0,0,0),(96,0,0,0,0,0,0),(97,0,0,0,0,0,0),(98,0,0,0,0,0,0),(99,0,0,0,0,0,0),(100,0,0,0,0,0,0),(101,0,0,0,0,0,0),(102,0,0,0,0,0,0),(103,0,0,0,0,0,0),(104,0,0,0,0,0,0),(105,0,0,0,0,0,0),(106,0,0,0,0,0,0),(107,0,0,0,0,0,0),(108,0,0,0,0,0,0),(109,0,0,0,0,0,0),(110,0,0,0,0,0,0),(111,0,0,0,0,0,0),(121,0,0,0,0,0,0),(122,0,0,0,0,0,0),(123,0,0,0,0,0,0),(124,0,0,0,0,0,0),(125,0,0,0,0,0,0),(126,0,0,0,0,0,0),(127,0,0,0,0,0,0),(128,0,0,0,0,0,0),(129,0,0,0,0,0,0),(130,0,0,0,0,0,0),(131,0,0,0,0,0,0),(132,0,0,0,0,0,0),(133,0,0,0,0,0,0),(134,0,0,0,0,0,0),(135,0,0,0,0,0,0),(136,0,0,0,0,0,0),(137,0,0,0,0,0,0),(138,0,0,0,0,0,0),(139,0,0,0,0,0,0),(140,0,0,0,0,0,0),(142,0,0,0,0,0,0),(143,0,0,0,0,0,0),(144,0,0,0,0,0,0),(145,0,0,0,0,0,0),(146,0,0,0,0,0,0),(147,0,0,0,0,0,0),(148,0,0,0,0,0,0),(149,0,0,0,0,0,0),(150,0,0,0,0,0,0),(151,0,0,0,0,0,0),(152,0,0,0,0,0,0),(153,0,0,0,0,0,0),(154,0,0,0,0,0,0);
/*!40000 ALTER TABLE `item_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_types`
--

DROP TABLE IF EXISTS `item_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_types` (
  `item_type_id` int NOT NULL AUTO_INCREMENT,
  `item_type_name` varchar(30) NOT NULL,
  PRIMARY KEY (`item_type_id`),
  UNIQUE KEY `uq_item_types_item_type_name` (`item_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_types`
--

LOCK TABLES `item_types` WRITE;
/*!40000 ALTER TABLE `item_types` DISABLE KEYS */;
INSERT INTO `item_types` VALUES (5,'crop'),(1,'edible'),(3,'equipment'),(6,'inedible'),(2,'recipe'),(4,'seed');
/*!40000 ALTER TABLE `item_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(40) NOT NULL,
  `price` int DEFAULT NULL,
  `item_stats_id` int DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `item_type_id` int DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  KEY `idx_items_item_stats_id` (`item_stats_id`),
  KEY `idx_items_item_type_id` (`item_type_id`),
  CONSTRAINT `fk_items_stats` FOREIGN KEY (`item_stats_id`) REFERENCES `item_stats` (`item_stats_id`),
  CONSTRAINT `fk_items_type` FOREIGN KEY (`item_type_id`) REFERENCES `item_types` (`item_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'Axe',10,121,'Axe.png',3),(2,'Pickaxe',10,122,'pickaxe.png',3),(3,'Sickle',10,123,'sickle.png',3),(4,'Fishing Rod',10,124,'fishing_rod.png',3),(5,'Onion Seed',10,125,'onion_seed.png',4),(6,'Green Eggplant Seed',10,126,'green_eggplant_seed.png',4),(7,'Grape Seed',10,127,'grape_seed.png',4),(8,'Cucumber Seed',10,128,'cucumber_seed.png',4),(9,'Chilli Seed',10,129,'chilli_seed.png',4),(10,'Cauliflower Seed',10,130,'cauliflower_seed.png',4),(11,'Broccoli Seed',10,131,'broccoli_seed.png',4),(12,'Beetroot Seed',10,132,'beetroot_seed.png',4),(13,'Bean Seed',10,133,'bean_seed.png',6),(14,'Pink Radish',50,22,'pink_radish.png',6),(15,'Patty Pan',100,23,'patty_pan.png',6),(16,'Onion',30,24,'onion.png',6),(17,'Long Beans',50,25,'long_beans.png',6),(18,'Lettuce',60,26,'lettuce.png',6),(19,'Ice Tuber',200,27,'ice_tuber.png',6),(20,'Hop cone',100,28,'hop_cone.png',6),(21,'Ham',500,29,'ham.png',6),(22,'Green Zucchili',80,30,'green_zucchili.png',6),(23,'Green Pea',40,31,'green_pea.png',6),(24,'Green Chili',20,32,'green_chilli.png',6),(25,'Gourd',45,33,'gourd.png',6),(26,'Garlic',30,34,'garlic.png',6),(27,'Fennel bulb',60,35,'fennel_bulb.png',6),(28,'Eggplant',40,36,'eggplant.png',6),(29,'DaiKon',30,37,'daikon.png',6),(30,'Cucumber',30,38,'cucumber.png',6),(31,'Corn',50,39,'corn.png',6),(32,'Chili pepper',40,40,'chili_pepper.png',6),(33,'Chicken Wing',400,41,'chicken_wing.png',6),(34,'Chicken thigh',470,42,'chicken_thigh.png',6),(35,'Cauliflower',70,43,'cauliflower.png',6),(36,'Carrot',40,44,'carrot.png',6),(37,'Cabbage',70,45,'cabbage.png',6),(38,'Broccoli',60,46,'broccoli.png',6),(39,'Bok Choy',40,47,'bok_choy.png',6),(40,'Bitter Melon',40,48,'bitter_melon.png',6),(41,'Beef',600,49,'beef.png',6),(42,'Beans',40,50,'beans.png',6),(43,'Bamboo Shoot',30,51,'bamboo_shoot.png',6),(44,'Asparagus',30,52,'asparagus.png',6),(45,'Antichoke',50,53,'artichoke.png',6),(46,'Bacon',100,54,'bacon.png',6),(47,'Bear Skin',50,55,'bear_skin.png',6),(48,'Blackened Meat',50,56,'blackened_meat.png',6),(49,'Blue Cloth',10,57,'blue_cloth.png',6),(50,'Bone',10,58,'bone.png',6),(51,'Dirty Rigs',10,59,'dirty_rags.png',6),(52,'Feathered Hide',10,60,'feathered_hide.png',6),(53,'Fish Meat',10,61,'fish_meat.png',6),(54,'Gilled Meat',10,62,'gilled_meat.png',6),(55,'Ham',20,63,'ham.png',6),(56,'Haunch',10,64,'haunch.png',6),(57,'Heart',10,65,'heart.png',6),(58,'Hooves',20,66,'hooves.png',6),(59,'Horn1',10,67,'horn1.png',6),(60,'Horn2',10,68,'horn2.png',6),(61,'Horn3',10,69,'horn3.png',6),(62,'Horn4',10,70,'horn4.png',6),(63,'Horn5',10,71,'horn5.png',6),(64,'Horn6',10,72,'horn6.png',6),(65,'Kidney',20,73,'kidney.png',6),(66,'Large Meat Chunk',10,74,'large_meat_chunk.png',6),(67,'Pork',10,75,'pork.png',6),(68,'Raw Ribs',10,76,'raw_ribs.png',6),(69,'Rotten Meat',10,77,'rotten_meat.png',6),(70,'Scarly Meat',10,78,'scaly_meat.png',6),(71,'Red Cloth',10,79,'red_cloth.png',6),(72,'Skull1',10,80,'skull1.png',6),(73,'Skull2',10,81,'skull2.png',6),(74,'Skull3',10,82,'skull3.png',6),(75,'Skull3',10,83,'skull4.png',6),(76,'Small Meat Chunk',10,84,'small_meat_chunk.png',6),(77,'Stringy Meat',10,85,'stringy_meat.png',6),(78,'Tongue',10,86,'tongue.png',6),(79,'roasted_harvest_plate',100,92,'roasted_harvest_plate.png',6),(80,'beef_stew',10,93,'beef_stew.png',6),(81,'garden_salad',100,94,'garden_salad.png',6),(82,'spicy_pork_stir-fry',100,95,'spicy_pork_stir-fry.png',6),(83,'steak_dinner',100,96,'steak_dinner.png',6),(84,'root_vegetable_beef_soup',100,97,'root_vegetable_beef_soup.png',6),(85,'spicy_eggplant_tomato',100,98,'spicy_eggplant_tomato.png',6),(86,'spicy_eggplant',100,99,'spicy_eggplant.png',6),(87,'root_vegetable_mix',100,100,'root_mix.png',6),(88,'roasted_pumpkin_mash',100,101,'roasted_pumpkin_mash.png',6),(89,'chicken_corn_soup',100,102,'chicken_corn_soup.png',6),(90,'pork_cabbage_stir-fry',100,103,'pork_cabbage_stir-fry.png',6),(91,'beef_potato_stew',100,104,'beef_potato_stew.png',6),(92,'pepper_steak',100,105,'pepper_steak.png',6),(93,'salad_leaves',100,106,'salad_leaves.png',6),(94,'fresh_salad',100,107,'fresh_salad.png',6),(95,'grilled_steak',100,108,'grilled_steak.png',6),(96,'tomato_slices',100,109,'tomato_slices.png',6),(97,'baked_potato',100,110,'baked_potato.png',6),(98,'grilled_ham',100,111,'grilled_ham.png',6),(99,'Tomato',50,9,'tomato.png',6),(100,'TintoBerry',50,10,'tintoberry.png',6),(101,'Sweet Potato',50,11,'sweet_potato.png',6),(102,'White Eggplant',50,12,'white_eggplant.png',6),(103,'Pumpkin',50,13,'pumpkin.png',6),(104,'Umbrella Cabbage',50,14,'umbrella_cabbage.png',6),(105,'Turnip',50,15,'turnip.png',6),(106,'Snow Berry',50,16,'snowberry.png',6),(107,'Rhubarb',50,17,'rhubarb.png',6),(108,'Red Bellpepper',50,18,'red_bellpepper.png',6),(109,'Radish',50,19,'radish.png',6),(110,'Potato',50,20,'potato.png',6),(111,'Pork ribs',50,21,'pork_ribs.png',6),(112,'Pumpkin Fruit',50,134,'pumpkin_fruit.png',1),(113,'Pineapple Fruit',50,135,'pineapple_fruit.png',1),(114,'Onion Fruit',50,136,'onion_fruit.png',1),(115,'Green Eggplant Fruit',50,137,'green_eggplant_fruit.png',1),(116,'grape_fruit',50,138,'grape_fruit.png',1),(117,'cauliflower_fruit',10,139,'cauliflower_fruit.png',1),(118,'chili_fruit',10,140,'chili_fruit.png',1),(120,'broccoli_fruit',10,142,'broccoli_fruit.png',1),(121,'beetroot_fruit',10,143,'beetroot_fruit.png',1),(122,'bean_fruit',10,144,'bean_fruit.png',1),(123,'Wheat Seed',10,145,'wheat_seed.png',4),(124,'Sunflower Seed',10,121,'sunflower_seed.png',4),(125,'Pumpkin Seed',10,147,'pumpkin_seed.png',4),(126,'Pineapple Seed',10,148,'pineapple_seed.png',4),(127,'Watering Can',10,149,'Watering_Can.png',3),(128,'banana_tree1',0,150,'banana_tree1.png',5),(129,'banana_tree2',0,151,'banana_tree2.png',5),(130,'bean_plant1',0,152,'bean_plant1.png',5),(131,'bean_plant2',0,153,'bean_plant2.png',5),(132,'bean_plant3',0,153,'bean_plant3.png',5),(133,'bean_plant4',0,153,'bean_plant4.png',5),(134,'beetroot_plant1',0,153,'beetroot_plant1.png',5),(135,'beetroot_plant2',0,153,'beetroot_plant2.png',5),(136,'beetroot_plant3',0,153,'beetroot_plant3.png',5),(137,'beetroot_plant4',0,153,'beetroot_plant4.png',5),(138,'blue_tree1',0,153,'blue_tree1.png',5),(139,'blue_tree2',0,153,'blue_tree2.png',5),(140,'broccoli_plant1',0,153,'broccoli_plant1.png',5),(141,'broccoli_plant2',0,153,'broccoli_plant2.png',5),(142,'broccoli_plant3',0,153,'broccoli_plant3.png',5),(143,'broccoli_plant4',0,153,'broccoli_plant4.png',5),(144,'carrot_plant1',0,153,'carrot_plant1.png',5),(145,'carrot_plant2',0,153,'carrot_plant2.png',5),(146,'carrot_plant3',0,153,'carrot_plant3.png',5),(147,'carrot_plant4',0,153,'carrot_plant4.png',5),(148,'cauliflower_plant1',0,153,'cauliflower_plant1.png',5),(149,'cauliflower_plant2',0,153,'cauliflower_plant2.png',5),(150,'cauliflower_plant3',0,153,'cauliflower_plant3.png',5),(151,'cauliflower_plant4',0,153,'cauliflower_plant4.png',5),(152,'chili_plant1',0,153,'chili_plant1.png',5),(153,'chili_plant2',0,153,'chili_plant2.png',5),(154,'chili_plant3',0,153,'chili_plant3.png',5),(155,'chili_plant4',0,153,'chili_plant4.png',5),(156,'coconut_tree1',0,153,'coconut_tree1.png',5),(157,'coconut_tree2',0,153,'coconut_tree2.png',5),(158,'cucumber_plant1',0,153,'cucumber_plant1.png',5),(159,'cucumber_plant2',0,153,'cucumber_plant2.png',5),(160,'cucumber_plant3',0,153,'cucumber_plant3.png',5),(161,'cucumber_plant4',0,153,'cucumber_plant4.png',5),(162,'grape_plant1',0,153,'grape_plant1.png',5),(163,'grape_plant2',0,153,'grape_plant2.png',5),(164,'grape_plant3',0,153,'grape_plant3.png',5),(165,'grape_plant4',0,153,'grape_plant4.png',5),(166,'green_eggplant_plant1',0,153,'green_eggplant_plant1.png',5),(167,'green_eggplant_plant2',0,153,'green_eggplant_plant2.png',5),(168,'green_eggplant_plant3',0,153,'green_eggplant_plant3.png',5),(169,'green_eggplant_plant4',0,153,'green_eggplant_plant4.png',5),(170,'mushroom_brown1',0,153,'mushroom_brown1.png',5),(171,'mushroom_brown2',0,153,'mushroom_brown2.png',5),(172,'mushroom_brown3',0,153,'mushroom_brown3.png',5),(173,'mushroom_brown4',0,153,'mushroom_brown4.png',5),(174,'mushroom_white1',0,153,'mushroom_white1.png',5),(175,'mushroom_white2',0,153,'mushroom_white2.png',5),(176,'mushroom_white3',0,153,'mushroom_white3.png',5),(177,'mushroom_white4',0,153,'mushroom_white4.png',5),(178,'pineapple_plant1',0,153,'pineapple_plant1.png',5),(179,'pineapple_plant2',0,153,'pineapple_plant2.png',5),(180,'pineapple_plant3',0,153,'pineapple_plant3.png',5),(181,'pineapple_plant4',0,153,'pineapple_plant4.png',5),(182,'pumpkin_plant1',0,153,'pumpkin_plant1.png',5),(183,'pumpkin_plant2',0,153,'pumpkin_plant2.png',5),(184,'pumpkin_plant3',0,153,'pumpkin_plant3.png',5),(185,'pumpkin_plant4',0,153,'pumpkin_plant4.png',5),(186,'red_tree1',0,153,'red_tree1.png',5),(187,'red_tree2',0,153,'red_tree2.png',5),(188,'sunflower_plant1',0,153,'sunflower_plant1.png',5),(189,'sunflower_plant2',0,153,'sunflower_plant2.png',5),(190,'sunflower_plant3',0,153,'sunflower_plant3.png',5),(191,'sunflower_plant4',0,153,'sunflower_plant4.png',5),(192,'tree_stumb',0,153,'tree_stumb.png',5),(193,'wheat_plant1',0,153,'wheat_plant1.png',5),(194,'wheat_plant2',0,153,'wheat_plant2.png',5),(195,'wheat_plant3',0,153,'wheat_plant3.png',5),(196,'wheat_plant4',0,153,'wheat_plant4.png',5),(197,'yellow_tree1',0,153,'yellow_tree1.png',5),(198,'yellow_tree2',0,153,'yellow_tree2.png',5),(199,'tree_log',10,153,'tree_log.png',6),(200,'blue_fruit',50,153,'blue_fruit.png',1),(201,'red_fruit',50,153,'red_fruit.png',1),(202,'yellow_fruit',50,153,'yellow_fruit.png',1),(203,'plant_fiber',10,153,'plant_fiber.png',6),(204,'rope',10,153,'rope.png',6),(205,'stick',10,153,'stick.png',6),(206,'Copper Ore',20,153,'copper_ore.png',6),(207,'Copper Ingot',40,153,'copper_ingot.png',6),(208,'Iron Ore',25,153,'iron_ore.png',6),(209,'Iron Ingot',50,153,'iron_ingot.png',6),(210,'Silver Ore',30,153,'silver_ore.png',6),(211,'Silver Ingot',60,153,'silver_ingot.png',6),(212,'Gold Ore',40,153,'gold_ore.png',6),(213,'Gold Ingot',80,153,'gold_ingot.png',6),(214,'Coal',15,153,'coal.png',6),(215,'Coal Brick',25,153,'coal_brick.png',6),(216,'tomato_slices_recipe',100,153,'tomato_slices_recipe.png',2),(217,'baked_potato_recipe',100,153,'baked_potato_recipe.png',2),(218,'grilled_steak_recipe',100,153,'grilled_steak_recipe.png',2),(219,'salad_leaves_recipe',100,153,'salad_leaves_recipe.png',2),(220,'fresh_salad_recipe',100,153,'fresh_leaves_recipe.png',2),(221,'beef_potato_stew_recipe',100,153,'beef_potato_stew_recipe.png',2),(222,'pepper_steak_recipe',100,153,'pepper_steak_recipe.png',2),(223,'pork_cabbage_stir_fry_recipe',100,153,'pork_cabbage_stir_fry.png',2),(224,'chicken_corn_soup_recipe',100,153,'chicken_corn_soup_recipe.png',2),(225,'roasted_pumpkin_mash_recipe',100,153,'roasted_pumpkin_mash_recipe.png',2),(226,'spicy_eggplant_recipe',100,153,'spicy_eggplant_recipe.png',2),(227,'root_mix_recipe',100,153,'root_mix_recipe.png',2),(228,'garden_salad_recipe',100,153,'garden_salad_recipe.png',2),(229,'beef_stew_recipe',100,153,'beef_stew_recipe.png',2),(230,'steak_dinner_recipe',100,153,'steak_dinner_recipe.png',2),(231,'spicy_pork_stir_fry_recipe',100,153,'spicy_pork_stir_fry_recipe.png',2),(232,'chicken_corn_soup_deluxe_recipe',100,153,'chicken_corn_soup_dulex_recipe.png',2),(233,'roasted_harvest_plate_recipe',100,153,'roasted_havest_plate_recipe.png',2),(234,'spicy_eggplant_tomato_recipe',100,153,'spicy_eggplant_tomato_recipe.png',2),(235,'root_vegetable_beef_soup_recipe',100,153,'root_vegetable_beef_soup_recipe.png',2),(236,'Adamantite Ore',60,153,'adamantite_ore.png',6),(237,'Adamantite Ingot',120,153,'adamantite_ingot.png',6),(238,'Mithril Ore',50,153,'mithril_ore.png',6),(239,'Mithril Ingot',100,153,'mithril_ingot.png',6),(240,'Orichalcum Ore',70,153,'orichalcum_ore.png',6),(241,'Orichalcum Ingot',140,153,'orichalcum_ingot.png',6),(242,'Hihiogane Ore',90,153,'hihiogane_ore.png',6),(243,'Hihiogane Ingot',180,153,'hihiogane_ingot.png',6),(244,'sand',5,153,'sand.png',6),(245,'stone',5,153,'stone.png',6),(246,'glass',10,153,'glass.png',6),(247,'cloth_bandage_blue',15,153,'cloth_bandage_blue.png',1),(248,'cloth_bandage_red',15,153,'cloth_bandage_red.png',1),(249,'water_bottle',20,153,'water_bottle.png',1),(250,'Iron Sword',200,153,'iron_sword.png',3),(251,'Iron Spear',180,153,'iron_spear.png',3),(252,'Iron Dagger',150,153,'iron_dagger.png',3),(253,'Iron Shovel',120,153,'iron_shovel.png',3),(254,'stone_after_mined',0,153,'stone_after_mined.png',5),(255,'stone_dungeon',0,153,'Stone_dungeon.png',5),(256,'stone_dungeon_after_mined',0,153,'stone_dungeon_after_mined.png',5),(257,'Steak',100,153,'Steak.png',6),(259,'chicken_corn_soup_deluxe',120,154,'chicken_corn_soup.png',1),(260,'stone_drop_item',5,153,'stone_drop_item.png',6),(261,'copper_drop_item',20,153,'copper_drop_item.png',6),(262,'iron_drop_item',25,153,'iron_drop_item.png',6),(263,'silver_drop_item',30,153,'silver_drop_item.png',6),(264,'gold_drop_item',40,153,'gold_drop_item.png',6),(265,'adamantite_drop_item',60,153,'adamantite_drop_item.png',6),(266,'orichalcum_drop_item',70,153,'orichalcum_drop_item.png',6),(267,'mithril_drop_item',50,153,'mithril_drop_item.png',6),(268,'hihiogane_drop_item',90,153,'hihiogane_drop_item.png',6),(269,'stone_dungeon_drop_item',5,153,'stone_dungeon_drop_item.png',6);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `map_item`
--

DROP TABLE IF EXISTS `map_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `map_item` (
  `map_id` int NOT NULL,
  `item_id` int NOT NULL,
  PRIMARY KEY (`map_id`,`item_id`),
  KEY `idx_map_item_item_id` (`item_id`),
  CONSTRAINT `fk_map_item_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`),
  CONSTRAINT `fk_map_item_map` FOREIGN KEY (`map_id`) REFERENCES `game_map` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `map_item`
--

LOCK TABLES `map_item` WRITE;
/*!40000 ALTER TABLE `map_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `map_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mobs`
--

DROP TABLE IF EXISTS `mobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mobs` (
  `mobs_id` int NOT NULL AUTO_INCREMENT,
  `mobs_name` varchar(100) NOT NULL,
  `mobs_type` varchar(50) NOT NULL,
  `str_stat` int NOT NULL DEFAULT '0',
  `agi_stat` int NOT NULL DEFAULT '0',
  `vit_stat` int NOT NULL DEFAULT '0',
  `dex_stat` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `stamina` int NOT NULL DEFAULT '0',
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mobs_id`),
  UNIQUE KEY `uq_mobs_mobs_type` (`mobs_type`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mobs`
--

LOCK TABLES `mobs` WRITE;
/*!40000 ALTER TABLE `mobs` DISABLE KEYS */;
INSERT INTO `mobs` VALUES (1,'Slime','slime',3,2,6,1,10,30,'mob/Slime/Slime1_Idle_with_shadow.png'),(2,'Poison Slime','poison_slime',6,4,12,2,20,60,'mob/Slime/Slime2_Idle_with_shadow.png'),(3,'Lava Slime','lava_slime',6,4,12,2,20,60,'mob/Slime/Slime3_Idle_with_shadow.png'),(4,'Orc','orc',4,3,3,1,20,25,'mob/Orc/orc1_idle_with_shadow.png'),(5,'Orc Berserker','orcb',12,9,9,3,60,75,'mob/Orc/orc3_idle_with_shadow.png');
/*!40000 ALTER TABLE `mobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_skills`
--

DROP TABLE IF EXISTS `player_skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_skills` (
  `saved_id` int NOT NULL,
  `skill_type_id` int NOT NULL,
  `skill_level` int NOT NULL DEFAULT '0',
  `skill_carry` decimal(4,3) NOT NULL DEFAULT '0.000',
  PRIMARY KEY (`saved_id`,`skill_type_id`),
  KEY `fk_player_skills_type` (`skill_type_id`),
  CONSTRAINT `fk_player_skills_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_player_skills_type` FOREIGN KEY (`skill_type_id`) REFERENCES `skill_types` (`skill_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_skills`
--

LOCK TABLES `player_skills` WRITE;
/*!40000 ALTER TABLE `player_skills` DISABLE KEYS */;
INSERT INTO `player_skills` VALUES (1,1,0,0.000),(1,2,0,0.000),(1,3,0,0.000),(1,4,0,0.000),(1,6,0,0.000),(3,1,0,0.000),(3,2,0,0.000),(3,3,0,0.000),(3,4,0,0.000),(3,6,0,0.000),(5,1,0,0.000),(5,2,0,0.000),(5,3,0,0.000),(5,4,0,0.000),(5,6,0,0.000),(12,1,0,0.000),(12,2,0,0.000),(12,3,0,0.000),(12,4,0,0.000),(12,6,0,0.000),(13,1,0,0.000),(13,2,0,0.000),(13,3,0,0.000),(13,4,0,0.000),(13,6,0,0.000),(14,1,0,0.000),(14,2,0,0.000),(14,3,0,0.000),(14,4,0,0.000),(14,6,0,0.000),(15,1,0,0.000),(15,2,0,0.000),(15,3,1,0.000),(15,4,0,0.000),(15,6,0,0.000);
/*!40000 ALTER TABLE `player_skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_stats`
--

DROP TABLE IF EXISTS `player_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_stats` (
  `player_stats_id` int NOT NULL AUTO_INCREMENT,
  `str_stat` int NOT NULL DEFAULT '0',
  `agi_stat` int NOT NULL DEFAULT '0',
  `dex_stat` int NOT NULL DEFAULT '0',
  `vit_stat` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '45',
  `stamina` int NOT NULL DEFAULT '50',
  PRIMARY KEY (`player_stats_id`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_stats`
--

LOCK TABLES `player_stats` WRITE;
/*!40000 ALTER TABLE `player_stats` DISABLE KEYS */;
INSERT INTO `player_stats` VALUES (88,1,1,1,1,45,50),(89,0,5,0,0,100,100),(90,0,5,0,0,100,100),(91,0,5,0,0,100,100),(112,1,1,1,1,45,50),(113,1,1,1,1,45,50),(114,1,1,1,1,45,50),(115,1,1,1,1,45,50),(116,1,1,1,1,45,50),(117,1,1,1,1,45,50),(119,1,19,1,20,46,75),(120,1,1,1,1,45,50),(121,1,1,1,1,45,50),(122,10,10,10,10,65,70);
/*!40000 ALTER TABLE `player_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `save_game`
--

DROP TABLE IF EXISTS `save_game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `save_game` (
  `saved_id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `saved_name` varchar(20) NOT NULL,
  `last_saved_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `current_map_id` int DEFAULT NULL,
  `player_stats_id` int DEFAULT NULL,
  `money` int NOT NULL DEFAULT '0',
  `saved_location_x` double NOT NULL,
  `saved_location_y` double NOT NULL,
  `exp` int NOT NULL DEFAULT '0',
  `selected_hotbar_index` int NOT NULL DEFAULT '0',
  `hotbar_visible` tinyint(1) NOT NULL DEFAULT '1',
  `world_name` varchar(50) NOT NULL DEFAULT '',
  `current_hp` int NOT NULL DEFAULT '45',
  `current_stamina` int NOT NULL DEFAULT '50',
  PRIMARY KEY (`saved_id`),
  KEY `idx_save_game_character_id` (`character_id`),
  KEY `idx_save_game_current_map_id` (`current_map_id`),
  KEY `idx_save_game_player_stats_id` (`player_stats_id`),
  CONSTRAINT `fk_save_game_character` FOREIGN KEY (`character_id`) REFERENCES `characters` (`character_id`),
  CONSTRAINT `fk_save_game_map` FOREIGN KEY (`current_map_id`) REFERENCES `game_map` (`map_id`),
  CONSTRAINT `fk_save_game_player_stats` FOREIGN KEY (`player_stats_id`) REFERENCES `player_stats` (`player_stats_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `save_game`
--

LOCK TABLES `save_game` WRITE;
/*!40000 ALTER TABLE `save_game` DISABLE KEYS */;
INSERT INTO `save_game` VALUES (1,1,'Save 1','2026-03-29 00:30:50',1,88,1000,160,135,100,0,1,'',45,50),(2,2,'Save 1','2026-03-21 00:16:36',1,89,1000,432,182,0,0,1,'ff',45,50),(3,1,'Save 2','2026-04-07 08:43:39',1,90,960,372.05253981210154,196.1040900741636,0,0,1,'world_3',45,90),(5,1,'Save 1','2026-03-27 09:11:54',1,112,1000,344,188,100,0,1,'asd',45,50),(12,1,'Save 4','2026-04-27 12:14:42',1,119,1000,318.59501284741305,224.80424425872192,0,0,1,'hh',22,74),(13,1,'Save 3','2026-03-28 21:56:33',1,120,1000,160,135,100,0,1,'me',45,50),(14,1,'Save 1','2026-04-07 08:29:38',1,121,90,431.5,181.5,100,0,1,'fgh',45,42),(15,3,'Save 1','2026-04-19 08:22:23',2,122,1000,276.8988982642226,103.28312010081284,4,0,1,'q',65,70);
/*!40000 ALTER TABLE `save_game` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_chest_item`
--

DROP TABLE IF EXISTS `saved_chest_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_chest_item` (
  `saved_id` int NOT NULL,
  `item_id` int NOT NULL,
  `qty` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`item_id`),
  KEY `idx_saved_chest_item_item_id` (`item_id`),
  CONSTRAINT `fk_saved_chest_item_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_chest_item_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_chest_item`
--

LOCK TABLES `saved_chest_item` WRITE;
/*!40000 ALTER TABLE `saved_chest_item` DISABLE KEYS */;
INSERT INTO `saved_chest_item` VALUES (12,1,1),(12,2,1),(12,3,1),(12,4,1),(12,5,1),(12,6,1),(13,2,1),(13,3,1),(14,3,1),(14,127,1),(15,203,1),(15,244,1),(15,245,1);
/*!40000 ALTER TABLE `saved_chest_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_farm_plot`
--

DROP TABLE IF EXISTS `saved_farm_plot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_farm_plot` (
  `saved_id` int NOT NULL,
  `map_key` varchar(50) NOT NULL,
  `plot_id` varchar(50) NOT NULL,
  `seed_item_id` int DEFAULT NULL,
  `planted_at` double NOT NULL DEFAULT '-1',
  `last_watered_at` double NOT NULL DEFAULT '-1',
  `accumulated_water_reduction` double NOT NULL DEFAULT '0',
  `total_growth_seconds` double NOT NULL DEFAULT '3600',
  `water_cooldown_seconds` double NOT NULL DEFAULT '900',
  `water_reduction_seconds` double NOT NULL DEFAULT '300',
  PRIMARY KEY (`saved_id`,`map_key`,`plot_id`),
  KEY `idx_saved_farm_plot_seed_item_id` (`seed_item_id`),
  CONSTRAINT `fk_saved_farm_plot_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_farm_plot_seed` FOREIGN KEY (`seed_item_id`) REFERENCES `items` (`item_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_farm_plot`
--

LOCK TABLES `saved_farm_plot` WRITE;
/*!40000 ALTER TABLE `saved_farm_plot` DISABLE KEYS */;
INSERT INTO `saved_farm_plot` VALUES (3,'Home_interior','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(3,'Home_interior','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(3,'home_map','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(3,'home_map','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(3,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(3,'home_map','Plot 9',NULL,-1,-1,0,900,300,60),(12,'home_map','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(12,'home_map','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(12,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 9',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 9',NULL,-1,-1,0,900,300,60),(14,'Home_interior','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(14,'Home_interior','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(14,'home_map','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(14,'home_map','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(14,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(14,'home_map','Plot 9',NULL,-1,-1,0,900,300,60),(15,'Home_interior','__farm_slot_sickle__',NULL,-1,-1,0,0,0,0),(15,'Home_interior','__farm_slot_watering_can__',NULL,-1,-1,0,0,0,0),(15,'home_map','__farm_slot_sickle__',3,-1,-1,0,0,0,0),(15,'home_map','__farm_slot_watering_can__',127,-1,-1,0,0,0,0),(15,'home_map','Plot 1',12,22936.410758399845,23679.257897999814,300,1800,600,300),(15,'home_map','Plot 10',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 11',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 12',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 13',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 14',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 15',12,22933.435631999855,23676.283267199815,300,1800,600,300),(15,'home_map','Plot 16',10,22944.950141999845,23683.871948399807,300,1800,600,300),(15,'home_map','Plot 17',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 18',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 19',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 2',10,22938.808120799855,23680.795003199815,300,1800,600,300),(15,'home_map','Plot 20',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 21',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 22',12,22932.281066999858,23675.130532799816,300,1800,600,300),(15,'home_map','Plot 23',10,22946.776432199837,23685.30701219981,300,1800,600,300),(15,'home_map','Plot 24',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 25',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 26',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 27',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 28',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 3',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 4',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 5',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 6',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 7',NULL,-1,-1,0,1800,600,300),(15,'home_map','Plot 8',12,22934.870549999854,23677.72439879981,300,1800,600,300),(15,'home_map','Plot 9',10,22941.10973699985,23682.33463559982,300,1800,600,300);
/*!40000 ALTER TABLE `saved_farm_plot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_hotbar`
--

DROP TABLE IF EXISTS `saved_hotbar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_hotbar` (
  `saved_id` int NOT NULL,
  `slot_index` int NOT NULL,
  `item_id` int DEFAULT NULL,
  `qty` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`slot_index`),
  KEY `idx_saved_hotbar_item_id` (`item_id`),
  CONSTRAINT `fk_saved_hotbar_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_saved_hotbar_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_hotbar`
--

LOCK TABLES `saved_hotbar` WRITE;
/*!40000 ALTER TABLE `saved_hotbar` DISABLE KEYS */;
INSERT INTO `saved_hotbar` VALUES (1,0,NULL,0),(1,1,NULL,0),(1,2,NULL,0),(1,3,NULL,0),(1,4,NULL,0),(2,0,NULL,0),(2,1,NULL,0),(2,2,NULL,0),(2,3,NULL,0),(2,4,NULL,0),(3,0,NULL,0),(3,1,NULL,0),(3,2,NULL,0),(3,3,NULL,0),(3,4,NULL,0),(5,0,NULL,0),(5,1,NULL,0),(5,2,NULL,0),(5,3,NULL,0),(5,4,NULL,0),(12,0,NULL,0),(12,1,NULL,0),(12,2,NULL,0),(12,3,NULL,0),(12,4,NULL,0),(13,0,NULL,0),(13,1,NULL,0),(13,2,NULL,0),(13,3,NULL,0),(13,4,NULL,0),(14,0,NULL,0),(14,1,NULL,0),(14,2,NULL,0),(14,3,NULL,0),(14,4,NULL,0),(15,0,1,1),(15,1,2,1),(15,2,NULL,0),(15,3,NULL,0),(15,4,NULL,0);
/*!40000 ALTER TABLE `saved_hotbar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_item`
--

DROP TABLE IF EXISTS `saved_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_item` (
  `saved_id` int NOT NULL,
  `item_id` int NOT NULL,
  `qty` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`item_id`),
  KEY `idx_saved_item_item_id` (`item_id`),
  CONSTRAINT `fk_saved_item_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`),
  CONSTRAINT `fk_saved_item_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_item`
--

LOCK TABLES `saved_item` WRITE;
/*!40000 ALTER TABLE `saved_item` DISABLE KEYS */;
INSERT INTO `saved_item` VALUES (2,4,1),(3,1,2),(3,6,1),(3,123,3),(5,54,1),(12,7,1),(12,8,1),(14,5,1);
/*!40000 ALTER TABLE `saved_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_mob`
--

DROP TABLE IF EXISTS `saved_mob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_mob` (
  `saved_id` int NOT NULL,
  `map_key` varchar(50) NOT NULL,
  `mob_id` varchar(50) NOT NULL,
  `mob_type` varchar(50) NOT NULL,
  `lived_days` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `max_hp` int NOT NULL DEFAULT '0',
  `sp` int NOT NULL DEFAULT '0',
  `max_sp` int NOT NULL DEFAULT '0',
  `str_stat` int NOT NULL DEFAULT '0',
  `agi_stat` int NOT NULL DEFAULT '0',
  `vit_stat` int NOT NULL DEFAULT '0',
  `dex_stat` int NOT NULL DEFAULT '0',
  `dead` tinyint(1) NOT NULL DEFAULT '0',
  `respawn_at_total_seconds` double NOT NULL DEFAULT '-1',
  `run_attack_cooldown_until_total_seconds` double NOT NULL DEFAULT '-1',
  `layout_x` double NOT NULL DEFAULT '0',
  `layout_y` double NOT NULL DEFAULT '0',
  `last_regen_total_seconds` double NOT NULL DEFAULT '-1',
  `last_updated_day_number` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`saved_id`,`map_key`,`mob_id`),
  CONSTRAINT `fk_saved_mob_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_mob`
--

LOCK TABLES `saved_mob` WRITE;
/*!40000 ALTER TABLE `saved_mob` DISABLE KEYS */;
INSERT INTO `saved_mob` VALUES (12,'right_map','lava_slime','lava_slime',0,20,20,65,65,7,5,14,2,0,-1,837706.3818121997,84.31321626935552,-76.24223689864772,837569.6959201997,10),(12,'right_map','orcb','orcb',0,64,64,74,75,14,9,12,3,0,-1,837758.7201793998,160.27697185052978,68.70047286040328,837569.6959201997,10),(12,'right_map','poison_slime','poison_slime',0,22,22,61,61,9,6,13,2,0,-1,837625.3884001994,-131.72040532512645,-86.63198190386576,837569.6959201997,10),(12,'upper_map','orc','orc',0,21,21,22,25,7,4,7,1,0,-1,838069.0284375774,219.41949688212142,-126.07958337701506,837838.806783517,10),(12,'upper_map','slime','slime',0,12,12,28,31,3,4,10,1,0,-1,837991.4949094411,-114.61013125788972,-104.6677864138239,837838.806783517,10);
/*!40000 ALTER TABLE `saved_mob` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_mob_drop`
--

DROP TABLE IF EXISTS `saved_mob_drop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_mob_drop` (
  `saved_id` int NOT NULL,
  `map_key` varchar(50) NOT NULL,
  `mob_id` varchar(50) NOT NULL,
  `drop_slot` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `spawned_at_total_seconds` double NOT NULL DEFAULT '0',
  `expires_at_total_seconds` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`map_key`,`mob_id`,`drop_slot`),
  KEY `idx_saved_mob_drop_item_id` (`item_id`),
  CONSTRAINT `fk_saved_mob_drop_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_mob_drop_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_mob_drop`
--

LOCK TABLES `saved_mob_drop` WRITE;
/*!40000 ALTER TABLE `saved_mob_drop` DISABLE KEYS */;
/*!40000 ALTER TABLE `saved_mob_drop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_shop_random_item`
--

DROP TABLE IF EXISTS `saved_shop_random_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_shop_random_item` (
  `saved_id` int NOT NULL,
  `slot_index` int NOT NULL,
  `item_id` int NOT NULL,
  PRIMARY KEY (`saved_id`,`slot_index`),
  CONSTRAINT `fk_saved_shop_random_item_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_shop_random_item`
--

LOCK TABLES `saved_shop_random_item` WRITE;
/*!40000 ALTER TABLE `saved_shop_random_item` DISABLE KEYS */;
INSERT INTO `saved_shop_random_item` VALUES (1,0,1),(1,1,2),(1,2,13),(1,3,6),(1,4,99),(1,5,199),(1,6,210),(1,7,83),(1,8,98),(1,9,212),(1,10,202),(1,11,96);
/*!40000 ALTER TABLE `saved_shop_random_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_shop_state`
--

DROP TABLE IF EXISTS `saved_shop_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_shop_state` (
  `saved_id` int NOT NULL,
  `random_day` int NOT NULL DEFAULT '-1',
  `stock_day` int NOT NULL DEFAULT '-1',
  PRIMARY KEY (`saved_id`),
  CONSTRAINT `fk_saved_shop_state_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_shop_state`
--

LOCK TABLES `saved_shop_state` WRITE;
/*!40000 ALTER TABLE `saved_shop_state` DISABLE KEYS */;
INSERT INTO `saved_shop_state` VALUES (1,1,1),(12,-1,-1),(13,-1,-1);
/*!40000 ALTER TABLE `saved_shop_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_shop_stock`
--

DROP TABLE IF EXISTS `saved_shop_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_shop_stock` (
  `saved_id` int NOT NULL,
  `item_id` int NOT NULL,
  `remaining_qty` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`item_id`),
  CONSTRAINT `fk_saved_shop_stock_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_shop_stock`
--

LOCK TABLES `saved_shop_stock` WRITE;
/*!40000 ALTER TABLE `saved_shop_stock` DISABLE KEYS */;
INSERT INTO `saved_shop_stock` VALUES (1,1,18),(1,2,18),(1,6,20),(1,13,20),(1,83,20),(1,96,20),(1,98,20),(1,99,20),(1,199,20),(1,202,20),(1,210,20),(1,212,20);
/*!40000 ALTER TABLE `saved_shop_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_smelt_ore_history`
--

DROP TABLE IF EXISTS `saved_smelt_ore_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_smelt_ore_history` (
  `saved_id` int NOT NULL,
  `source_item_id` int NOT NULL,
  `smelt_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`source_item_id`),
  KEY `idx_saved_smelt_ore_history_source_item_id` (`source_item_id`),
  CONSTRAINT `fk_saved_smelt_ore_history_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_smelt_ore_history_source_item` FOREIGN KEY (`source_item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_smelt_ore_history`
--

LOCK TABLES `saved_smelt_ore_history` WRITE;
/*!40000 ALTER TABLE `saved_smelt_ore_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `saved_smelt_ore_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_smelt_state`
--

DROP TABLE IF EXISTS `saved_smelt_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_smelt_state` (
  `saved_id` int NOT NULL,
  `point_cap` int NOT NULL DEFAULT '10',
  `points` double NOT NULL DEFAULT '10',
  `last_point_update_total_seconds` double NOT NULL DEFAULT '0',
  `exp_carry` double NOT NULL DEFAULT '0',
  `cap_carry` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`),
  CONSTRAINT `fk_saved_smelt_state_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_smelt_state`
--

LOCK TABLES `saved_smelt_state` WRITE;
/*!40000 ALTER TABLE `saved_smelt_state` DISABLE KEYS */;
INSERT INTO `saved_smelt_state` VALUES (12,10,10,838352.0016215944,0,0),(15,10,10,26091.277508999723,0,0);
/*!40000 ALTER TABLE `saved_smelt_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_time`
--

DROP TABLE IF EXISTS `saved_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_time` (
  `saved_id` int NOT NULL,
  `day_number` int NOT NULL DEFAULT '1',
  `total_seconds` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`),
  CONSTRAINT `fk_saved_time_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_time`
--

LOCK TABLES `saved_time` WRITE;
/*!40000 ALTER TABLE `saved_time` DISABLE KEYS */;
INSERT INTO `saved_time` VALUES (1,1,4.351699199999998),(2,1,10.469164200000012),(3,1,228.982360901002),(5,1,36.70162290000008),(12,10,838352.0016215944),(13,1,48.0978993),(14,1,21617.568197499975),(15,1,26091.277508999723);
/*!40000 ALTER TABLE `saved_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_tree_drop`
--

DROP TABLE IF EXISTS `saved_tree_drop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_tree_drop` (
  `saved_id` int NOT NULL,
  `map_key` varchar(50) NOT NULL,
  `tree_id` varchar(50) NOT NULL,
  `drop_slot` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `spawned_at_total_seconds` double NOT NULL DEFAULT '0',
  `expires_at_total_seconds` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`map_key`,`tree_id`,`drop_slot`),
  KEY `idx_saved_tree_drop_item_id` (`item_id`),
  CONSTRAINT `fk_saved_tree_drop_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_tree_drop_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_tree_drop`
--

LOCK TABLES `saved_tree_drop` WRITE;
/*!40000 ALTER TABLE `saved_tree_drop` DISABLE KEYS */;
/*!40000 ALTER TABLE `saved_tree_drop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_tree_resource`
--

DROP TABLE IF EXISTS `saved_tree_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_tree_resource` (
  `saved_id` int NOT NULL,
  `map_key` varchar(50) NOT NULL,
  `tree_id` varchar(50) NOT NULL,
  `tree_type` varchar(50) NOT NULL,
  `current_stage` int NOT NULL DEFAULT '2',
  `lived_days` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `max_hp` int NOT NULL DEFAULT '0',
  `vit` int NOT NULL DEFAULT '0',
  `respawn_at_total_seconds` double NOT NULL DEFAULT '-1',
  `last_updated_day_number` int NOT NULL DEFAULT '1',
  `drop_rate_pity` int NOT NULL DEFAULT '0',
  `drops_this_life` int NOT NULL DEFAULT '0',
  `damage_since_last_drop_roll` int NOT NULL DEFAULT '0',
  `last_hp_growth_total_seconds` double NOT NULL DEFAULT '-1',
  `damage_chunk_progress` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`saved_id`,`map_key`,`tree_id`),
  CONSTRAINT `fk_saved_tree_resource_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_tree_resource`
--

LOCK TABLES `saved_tree_resource` WRITE;
/*!40000 ALTER TABLE `saved_tree_resource` DISABLE KEYS */;
INSERT INTO `saved_tree_resource` VALUES (15,'left_map','stone','stone',1,0,0,0,0,67304.4546497998,1,0,2,0,-1,0),(15,'left_map','tree','blue',1,0,0,0,0,67952.25809939975,1,0,2,0,-1,0);
/*!40000 ALTER TABLE `saved_tree_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_tree_state`
--

DROP TABLE IF EXISTS `saved_tree_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_tree_state` (
  `saved_id` int NOT NULL,
  `map_key` varchar(40) NOT NULL,
  `tree_id` varchar(40) NOT NULL,
  `tree_type` varchar(20) NOT NULL DEFAULT 'BLUE',
  `stage` int NOT NULL DEFAULT '2',
  `lived_days` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `vit` int NOT NULL DEFAULT '0',
  `respawn_at_seconds` double NOT NULL DEFAULT '-1',
  `last_day_number` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`saved_id`,`map_key`,`tree_id`),
  CONSTRAINT `fk_saved_tree_state_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_tree_state`
--

LOCK TABLES `saved_tree_state` WRITE;
/*!40000 ALTER TABLE `saved_tree_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `saved_tree_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_unlocked_recipe`
--

DROP TABLE IF EXISTS `saved_unlocked_recipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saved_unlocked_recipe` (
  `saved_id` int NOT NULL,
  `recipe_key` varchar(100) NOT NULL,
  PRIMARY KEY (`saved_id`,`recipe_key`),
  CONSTRAINT `fk_saved_unlocked_recipe_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_unlocked_recipe`
--

LOCK TABLES `saved_unlocked_recipe` WRITE;
/*!40000 ALTER TABLE `saved_unlocked_recipe` DISABLE KEYS */;
INSERT INTO `saved_unlocked_recipe` VALUES (1,'rope'),(1,'stick'),(1,'tomato_slices'),(12,'rope'),(12,'salad_leaves'),(12,'stick'),(12,'water_bottle'),(15,'stick'),(15,'tomato_slices');
/*!40000 ALTER TABLE `saved_unlocked_recipe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skill_types`
--

DROP TABLE IF EXISTS `skill_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skill_types` (
  `skill_type_id` int NOT NULL AUTO_INCREMENT,
  `skill_name` varchar(30) NOT NULL,
  PRIMARY KEY (`skill_type_id`),
  UNIQUE KEY `uq_skill_types_skill_name` (`skill_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skill_types`
--

LOCK TABLES `skill_types` WRITE;
/*!40000 ALTER TABLE `skill_types` DISABLE KEYS */;
INSERT INTO `skill_types` VALUES (4,'combat'),(6,'cooking'),(3,'crafting'),(1,'farming'),(2,'gathering');
/*!40000 ALTER TABLE `skill_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skin`
--

DROP TABLE IF EXISTS `skin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skin` (
  `skin_id` int NOT NULL AUTO_INCREMENT,
  `skin_name` varchar(255) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`skin_id`),
  UNIQUE KEY `uq_skin_skin_name` (`skin_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skin`
--

LOCK TABLES `skin` WRITE;
/*!40000 ALTER TABLE `skin` DISABLE KEYS */;
/*!40000 ALTER TABLE `skin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spawn_point`
--

DROP TABLE IF EXISTS `spawn_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spawn_point` (
  `spawn_point_id` int NOT NULL AUTO_INCREMENT,
  `map_id` int NOT NULL,
  `spawn_x` int NOT NULL DEFAULT '0',
  `spawn_y` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`spawn_point_id`),
  KEY `idx_spawn_point_map_id` (`map_id`),
  CONSTRAINT `fk_spawn_point_map` FOREIGN KEY (`map_id`) REFERENCES `game_map` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spawn_point`
--

LOCK TABLES `spawn_point` WRITE;
/*!40000 ALTER TABLE `spawn_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `spawn_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(20) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_user_roles_role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,'admin'),(2,'owner'),(3,'user');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-27 18:45:17
