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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'koko',1,'hhhhh','123123',1,'2026-02-12 19:47:07','2026-02-27 22:26:31'),(2,'eiei',2,'aaaa','123456',1,'2026-02-13 15:01:57','2026-02-27 22:26:31'),(3,'koo',3,'hhhh','123123',1,'2026-02-11 14:45:22','2026-02-27 22:26:31'),(4,'kk',3,'hhh','123123',1,'2026-02-11 15:04:31','2026-02-27 22:26:31'),(5,'qqq',3,'qqq','123',1,'2026-02-13 16:02:00','2026-02-27 22:26:31'),(6,'yyyyy',3,'yyyyy','123',2,'2026-02-11 19:24:47','2026-02-27 22:26:31'),(7,'',3,'qqq@gmail.com','',2,'2026-02-12 18:40:54','2026-02-27 22:26:31'),(8,'asdf',3,'asd@gmail.com','',1,'2026-02-11 15:30:15','2026-02-27 22:26:31'),(9,'asdfg',3,'asdf@gmail.com','1234',1,'2026-02-11 15:32:01','2026-02-27 22:26:31'),(10,'qwerty',3,'asdfg@gmail.com','',1,'2026-02-11 15:46:51','2026-02-27 22:26:31'),(11,'qwertyu',3,'qwerty@gmail.com','',1,'2026-02-11 15:56:43','2026-02-27 22:26:31'),(12,'qwertyuiop',3,'qwertyuiop@gmail.com','',1,'2026-02-11 15:58:57','2026-02-27 22:26:31'),(13,'q123',3,'qQ123@gmail.com','',1,'2026-02-11 16:10:13','2026-02-27 22:26:31'),(14,'ER123',3,'lol123@gmail.com','Ahref123@',1,'2026-02-11 16:34:29','2026-02-27 22:26:31'),(15,'koshane',3,'asdfasdf@gmail.com','Shirt12!',1,'2026-03-19 01:58:01','2026-03-19 01:58:01'),(16,'HHHHHH',3,'larry@gmail.com','Koshane@11',1,'2026-04-02 07:09:15','2026-04-02 07:09:15'),(17,'Larry',3,'larry562005@gmail.com','Ko123123@',1,'2026-04-02 09:03:02','2026-04-02 09:03:02');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `characters`
--

LOCK TABLES `characters` WRITE;
/*!40000 ALTER TABLE `characters` DISABLE KEYS */;
INSERT INTO `characters` VALUES (1,3,'Player1',NULL),(2,15,'koshane',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_map`
--

LOCK TABLES `game_map` WRITE;
/*!40000 ALTER TABLE `game_map` DISABLE KEYS */;
INSERT INTO `game_map` VALUES (2,'Home_interior'),(1,'home_map'),(3,'left_map'),(4,'right_map'),(5,'upper_map');
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
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_stats`
--

LOCK TABLES `item_stats` WRITE;
/*!40000 ALTER TABLE `item_stats` DISABLE KEYS */;
INSERT INTO `item_stats` VALUES (1,1,1,1,1,1,1),(2,0,0,0,0,0,0),(3,0,0,0,0,0,0),(4,0,0,0,0,0,0),(5,0,0,0,0,0,0),(6,0,0,0,0,0,0),(7,0,0,0,0,0,0),(8,0,0,0,0,0,0),(9,0,0,0,0,0,0),(10,0,0,0,0,0,0),(11,0,0,0,0,0,0),(12,0,0,0,0,0,0),(13,0,0,0,0,0,0),(14,0,0,0,0,0,0),(15,0,0,0,0,0,0),(16,0,0,0,0,0,0),(17,0,0,0,0,0,0),(18,0,0,0,0,0,0),(19,0,0,0,0,0,0),(20,0,0,0,0,0,0),(21,0,0,0,0,0,0),(22,0,0,0,0,0,0),(23,0,0,0,0,0,0),(24,0,0,0,0,0,0),(25,0,0,0,0,0,0),(26,0,0,0,0,0,0),(27,0,0,0,0,0,0),(28,0,0,0,0,0,0),(29,0,0,0,0,0,0),(30,0,0,0,0,0,0),(31,0,0,0,0,0,0),(32,0,0,0,0,0,0),(33,0,0,0,0,0,0),(34,0,0,0,0,0,0),(35,0,0,0,0,0,0),(36,0,0,0,0,0,0),(37,0,0,0,0,0,0),(38,0,0,0,0,0,0),(39,0,0,0,0,0,0),(40,0,0,0,0,0,0),(41,0,0,0,0,0,0),(42,0,0,0,0,0,0),(43,0,0,0,0,0,0),(44,0,0,0,0,0,0),(45,0,0,0,0,0,0),(46,0,0,0,0,0,0),(47,0,0,0,0,0,0),(48,0,0,0,0,0,0),(49,0,0,0,0,0,0),(50,0,0,0,0,0,0),(51,0,0,0,0,0,0),(52,0,0,0,0,0,0),(53,0,0,0,0,0,0),(54,0,0,0,0,0,0),(55,0,0,0,0,0,0),(56,0,0,0,0,0,0),(57,0,0,0,0,0,0),(58,0,0,0,0,0,0),(59,0,0,0,0,0,0),(60,0,0,0,0,0,0),(61,0,0,0,0,0,0),(62,0,0,0,0,0,0),(63,0,0,0,0,0,0),(64,0,0,0,0,0,0),(65,0,0,0,0,0,0),(66,0,0,0,0,0,0),(67,0,0,0,0,0,0),(68,0,0,0,0,0,0),(69,0,0,0,0,0,0),(70,0,0,0,0,0,0),(71,0,0,0,0,0,0),(72,0,0,0,0,0,0),(73,0,0,0,0,0,0),(74,0,0,0,0,0,0),(75,0,0,0,0,0,0),(76,0,0,0,0,0,0),(77,0,0,0,0,0,0),(78,0,0,0,0,0,0),(79,0,0,0,0,0,0),(80,0,0,0,0,0,0),(81,0,0,0,0,0,0),(82,0,0,0,0,0,0),(83,0,0,0,0,0,0),(84,0,0,0,0,0,0),(85,0,0,0,0,0,0),(86,0,0,0,0,0,0),(92,0,0,0,0,0,0),(93,0,0,0,0,0,0),(94,0,0,0,0,0,0),(95,0,0,0,0,0,0),(96,0,0,0,0,0,0),(97,0,0,0,0,0,0),(98,0,0,0,0,0,0),(99,0,0,0,0,0,0),(100,0,0,0,0,0,0),(101,0,0,0,0,0,0),(102,0,0,0,0,0,0),(103,0,0,0,0,0,0),(104,0,0,0,0,0,0),(105,0,0,0,0,0,0),(106,0,0,0,0,0,0),(107,0,0,0,0,0,0),(108,0,0,0,0,0,0),(109,0,0,0,0,0,0),(110,0,0,0,0,0,0),(111,0,0,0,0,0,0),(121,0,0,0,0,0,0),(122,0,0,0,0,0,0),(123,0,0,0,0,0,0),(124,0,0,0,0,0,0),(125,0,0,0,0,0,0),(126,0,0,0,0,0,0),(127,0,0,0,0,0,0),(128,0,0,0,0,0,0),(129,0,0,0,0,0,0),(130,0,0,0,0,0,0),(131,0,0,0,0,0,0),(132,0,0,0,0,0,0),(133,0,0,0,0,0,0),(134,0,0,0,0,0,0),(135,0,0,0,0,0,0),(136,0,0,0,0,0,0),(137,0,0,0,0,0,0),(138,0,0,0,0,0,0),(139,0,0,0,0,0,0),(140,0,0,0,0,0,0),(142,0,0,0,0,0,0),(143,0,0,0,0,0,0),(144,0,0,0,0,0,0),(145,0,0,0,0,0,0),(146,0,0,0,0,0,0),(147,0,0,0,0,0,0),(148,0,0,0,0,0,0),(149,0,0,0,0,0,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_types`
--

LOCK TABLES `item_types` WRITE;
/*!40000 ALTER TABLE `item_types` DISABLE KEYS */;
INSERT INTO `item_types` VALUES (1,'unknown');
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
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'Axe',10,121,'Axe.png',1),(2,'Pickaxe',10,122,'pickaxe.png',1),(3,'Sickle',10,123,'sickle.png',1),(4,'Fishing Rod',10,124,'fishing_rod.png',1),(5,'Onion Seed',10,125,'onion_seed.png',1),(6,'Green Eggplant Seed',10,126,'green_eggplant_seed.png',1),(7,'Grape Seed',10,127,'grape_seed.png',1),(8,'Cucumber Seed',10,128,'cucumber_seed.png',1),(9,'Chilli Seed',10,129,'chilli_seed.png',1),(10,'Cauliflower Seed',10,130,'cauliflower_seed.png',1),(11,'Broccoli Seed',10,131,'broccoli_seed.png',1),(12,'Beetroot Seed',10,132,'beetroot_seed.png',1),(13,'Bean Seed',10,133,'bean_seed.png',1),(14,'Pink Radish',50,22,'pink_radish.png',1),(15,'Patty Pan',100,23,'patty_pan.png',1),(16,'Onion',30,24,'onion.png',1),(17,'Long Beans',50,25,'long_beans.png',1),(18,'Lettuce',60,26,'lettuce.png',1),(19,'Ice Tuber',200,27,'ice_tuber.png',1),(20,'Hop cone',100,28,'hop_cone.png',1),(21,'Ham',500,29,'ham.png',1),(22,'Green Zucchili',80,30,'green_zucchili.png',1),(23,'Green Pea',40,31,'green_pea.png',1),(24,'Green Chili',20,32,'green_chilli.png',1),(25,'Gourd',45,33,'gourd.png',1),(26,'Garlic',30,34,'garlic.png',1),(27,'Fennel bulb',60,35,'fennel_bulb.png',1),(28,'Eggplant',40,36,'eggplant.png',1),(29,'DaiKon',30,37,'daikon.png',1),(30,'Cucumber',30,38,'cucumber.png',1),(31,'Corn',50,39,'corn.png',1),(32,'Chili pepper',40,40,'chili_pepper.png',1),(33,'Chicken Wing',400,41,'chicken_wing.png',1),(34,'Chicken thigh',470,42,'chicken_thigh.png',1),(35,'Cauliflower',70,43,'cauliflower.png',1),(36,'Carrot',40,44,'carrot.png',1),(37,'Cabbage',70,45,'cabbage.png',1),(38,'Broccoli',60,46,'broccoli.png',1),(39,'Bok Choy',40,47,'bok_choy.png',1),(40,'Bitter Melon',40,48,'bitter_melon.png',1),(41,'Beef',600,49,'beef.png',1),(42,'Beans',40,50,'beans.png',1),(43,'Bamboo Shoot',30,51,'bamboo_shoot.png',1),(44,'Asparagus',30,52,'asparagus.png',1),(45,'Antichoke',50,53,'artichoke.png',1),(46,'Bacon',100,54,'bacon.png',1),(47,'Bear Skin',50,55,'bear_skin.png',1),(48,'Blackened Meat',50,56,'blackened_meat.png',1),(49,'Blue Cloth',10,57,'blue_cloth.png',1),(50,'Bone',10,58,'bone.png',1),(51,'Dirty Rigs',10,59,'dirty_rags.png',1),(52,'Feathered Hide',10,60,'feathered_hide.png',1),(53,'Fish Meat',10,61,'fish_meat.png',1),(54,'Gilled Meat',10,62,'gilled_meat.png',1),(55,'Ham',20,63,'ham.png',1),(56,'Haunch',10,64,'haunch.png',1),(57,'Heart',10,65,'heart.png',1),(58,'Hooves',20,66,'hooves.png',1),(59,'Horn1',10,67,'horn1.png',1),(60,'Horn2',10,68,'horn2.png',1),(61,'Horn3',10,69,'horn3.png',1),(62,'Horn4',10,70,'horn4.png',1),(63,'Horn5',10,71,'horn5.png',1),(64,'Horn6',10,72,'horn6.png',1),(65,'Kidney',20,73,'kidney.png',1),(66,'Large Meat Chunk',10,74,'large_meat_chunk.png',1),(67,'Pork',10,75,'pork.png',1),(68,'Raw Ribs',10,76,'raw_ribs.png',1),(69,'Rotten Meat',10,77,'rotten_meat.png',1),(70,'Scarly Meat',10,78,'scaly_meat.png',1),(71,'Red Cloth',10,79,'red_cloth.png',1),(72,'Skull1',10,80,'skull1.png',1),(73,'Skull2',10,81,'skull2.png',1),(74,'Skull3',10,82,'skull3.png',1),(75,'Skull3',10,83,'skull4.png',1),(76,'Small Meat Chunk',10,84,'small_meat_chunk.png',1),(77,'Stringy Meat',10,85,'stringy_meat.png',1),(78,'Tongue',10,86,'tongue.png',1),(79,'roasted_harvest_plate',100,92,'roasted_harvest_plate.png',1),(80,'beef_stew',10,93,'beef_stew.png',1),(81,'garden_salad',100,94,'garden_salad.png',1),(82,'spicy_pork_stir-fry',100,95,'spicy_pork_stir-fry.png',1),(83,'steak_dinner',100,96,'steak_dinner.png',1),(84,'root_vegetable_beef_soup',100,97,'root_vegetable_beef_soup.png',1),(85,'spicy_eggplant_tomato',100,98,'spicy_eggplant_tomato.png',1),(86,'spicy_eggplant',100,99,'spicy_eggplant.png',1),(87,'root_vegetable_mix',100,100,'root_mix.png',1),(88,'roasted_pumpkin_mash',100,101,'roasted_pumpkin_mash.png',1),(89,'chicken_corn_soup',100,102,'chicken_corn_soup.png',1),(90,'pork_cabbage_stir-fry',100,103,'pork_cabbage_stir-fry.png',1),(91,'beef_potato_stew',100,104,'beef_potato_stew.png',1),(92,'pepper_steak',100,105,'pepper_steak.png',1),(93,'salad_leaves',100,106,'salad_leaves.png',1),(94,'fresh_salad',100,107,'fresh_salad.png',1),(95,'grilled_steak',100,108,'grilled_steak.png',1),(96,'tomato_slices',100,109,'tomato_slices.png',1),(97,'baked_potato',100,110,'baked_potato.png',1),(98,'grilled_ham',100,111,'grilled_ham.png',1),(99,'Tomato',50,9,'tomato.png',1),(100,'TintoBerry',50,10,'tintoberry.png',1),(101,'Sweet Potato',50,11,'sweet_potato.png',1),(102,'White Eggplant',50,12,'white_eggplant.png',1),(103,'Pumpkin',50,13,'pumpkin.png',1),(104,'Umbrella Cabbage',50,14,'umbrella_cabbage.png',1),(105,'Turnip',50,15,'turnip.png',1),(106,'Snow Berry',50,16,'snowberry.png',1),(107,'Rhubarb',50,17,'rhubarb.png',1),(108,'Red Bellpepper',50,18,'red_bellpepper.png',1),(109,'Radish',50,19,'radish.png',1),(110,'Potato',50,20,'potato.png',1),(111,'Pork ribs',50,21,'pork_ribs.png',1),(112,'Pumpkin Fruit',50,134,'pumpkin_fruit.png',1),(113,'Pineapple Fruit',50,135,'pineapple_fruit.png',1),(114,'Onion Fruit',50,136,'onion_fruit.png',1),(115,'Green Eggplant Fruit',50,137,'green_eggplant_fruit.png',1),(116,'grape_fruit',50,138,'grape_fruit.png',1),(117,'cauliflower_fruit',10,139,'cauliflower_fruit.png',1),(118,'chili_fruit',10,140,'chili_fruit.png',1),(120,'broccoli_fruit',10,142,'broccoli_fruit.png',1),(121,'beetroot_fruit',10,143,'beetroot_fruit.png',1),(122,'bean_fruit',10,144,'bean_fruit.png',1),(123,'Wheat Seed',10,145,'wheat_seed.png',1),(124,'Sunflower Seed',10,121,'sunflower_seed.png',1),(125,'Pumpkin Seed',10,147,'pumpkin_seed.png',1),(126,'Pineapple Seed',10,148,'pineapple_seed.png',1),(127,'Watering Can',10,149,'Watering_Can.png',1);
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
-- Table structure for table `player_skills`
--

DROP TABLE IF EXISTS `player_skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_skills` (
  `saved_id` int NOT NULL,
  `skill_type_id` int NOT NULL,
  `skill_level` int NOT NULL DEFAULT '0',
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
INSERT INTO `player_skills` VALUES (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(5,1,0),(5,2,0),(5,3,0),(5,4,0),(5,5,0),(5,6,0),(12,1,0),(12,2,0),(12,3,0),(12,4,0),(12,5,0),(12,6,0),(13,1,0),(13,2,0),(13,3,0),(13,4,0),(13,5,0),(13,6,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_stats`
--

LOCK TABLES `player_stats` WRITE;
/*!40000 ALTER TABLE `player_stats` DISABLE KEYS */;
INSERT INTO `player_stats` VALUES (88,1,1,1,1,45,50),(89,0,5,0,0,100,100),(90,0,5,0,0,100,100),(91,0,5,0,0,100,100),(112,1,1,1,1,45,50),(113,1,1,1,1,45,50),(114,1,1,1,1,45,50),(115,1,1,1,1,45,50),(116,1,1,1,1,45,50),(117,1,1,1,1,45,50),(119,1,1,1,1,45,50),(120,1,1,1,1,45,50);
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `save_game`
--

LOCK TABLES `save_game` WRITE;
/*!40000 ALTER TABLE `save_game` DISABLE KEYS */;
INSERT INTO `save_game` VALUES (1,1,'Save 1','2026-03-29 00:30:50',1,88,1000,160,135,100,0,1,'',45,50),(2,2,'Save 1','2026-03-21 00:16:36',1,89,1000,432,182,0,0,1,'ff',45,50),(3,1,'Save 2','2026-03-20 01:06:13',1,90,1000,160,135,0,0,1,'world_3',45,50),(5,1,'Save 1','2026-03-27 09:11:54',1,112,1000,344,188,100,0,1,'asd',45,50),(12,1,'Save 4','2026-03-30 12:30:47',1,119,1000,160,135,100,0,1,'hh',45,45),(13,1,'Save 3','2026-03-28 21:56:33',1,120,1000,160,135,100,0,1,'me',45,50);
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
INSERT INTO `saved_chest_item` VALUES (12,1,1),(12,2,1),(12,3,1),(12,4,1),(12,5,1),(12,6,1),(13,2,1),(13,3,1);
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
  `total_growth_seconds` double NOT NULL DEFAULT '900',
  `water_cooldown_seconds` double NOT NULL DEFAULT '300',
  `water_reduction_seconds` double NOT NULL DEFAULT '60',
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
INSERT INTO `saved_farm_plot` VALUES (12,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(12,'home_map','Plot 9',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 1',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 10',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 11',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 12',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 13',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 14',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 15',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 16',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 17',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 18',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 19',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 2',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 20',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 21',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 22',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 23',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 24',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 25',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 26',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 27',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 28',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 29',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 3',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 30',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 31',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 32',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 33',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 34',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 35',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 36',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 37',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 38',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 39',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 4',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 40',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 41',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 42',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 43',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 44',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 45',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 5',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 6',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 7',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 8',NULL,-1,-1,0,900,300,60),(13,'home_map','Plot 9',NULL,-1,-1,0,900,300,60);
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
INSERT INTO `saved_hotbar` VALUES (1,0,NULL,0),(1,1,NULL,0),(1,2,NULL,0),(1,3,NULL,0),(1,4,NULL,0),(2,0,NULL,0),(2,1,NULL,0),(2,2,NULL,0),(2,3,NULL,0),(2,4,NULL,0),(3,0,NULL,0),(3,1,NULL,0),(3,2,NULL,0),(3,3,NULL,0),(3,4,NULL,0),(5,0,NULL,0),(5,1,NULL,0),(5,2,NULL,0),(5,3,NULL,0),(5,4,NULL,0),(12,0,NULL,0),(12,1,NULL,0),(12,2,NULL,0),(12,3,NULL,0),(12,4,NULL,0),(13,0,NULL,0),(13,1,NULL,0),(13,2,NULL,0),(13,3,NULL,0),(13,4,NULL,0);
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
INSERT INTO `saved_item` VALUES (2,4,1),(3,1,1),(3,6,1),(5,54,1),(12,7,1),(12,8,1);
/*!40000 ALTER TABLE `saved_item` ENABLE KEYS */;
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
INSERT INTO `saved_time` VALUES (1,1,4.351699199999998),(2,1,10.469164200000012),(3,1,140.5822159009999),(5,1,36.70162290000008),(12,1,57.23213539999994),(13,1,48.0978993);
/*!40000 ALTER TABLE `saved_time` ENABLE KEYS */;
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
INSERT INTO `skill_types` VALUES (5,'battling'),(6,'cooking'),(3,'crafting'),(1,'farming'),(4,'fishing'),(2,'gathering');
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

-- Dump completed on 2026-04-03 20:14:57
