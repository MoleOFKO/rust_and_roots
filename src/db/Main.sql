-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: rust_and_roots
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
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `account_name` varchar(255) NOT NULL,
  `user_type` varchar(5) NOT NULL,
  `email` varchar(255) NOT NULL,
  `passwords` varchar(255) NOT NULL,
  `account_stats` varchar(6) NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT (now()),
  `updated_date` timestamp NOT NULL DEFAULT (now()),
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'koko','admin','hhhhh','123123','active','2026-02-13 02:17:07','2026-02-28 04:56:31'),(2,'eiei','owner','aaaa','123456','active','2026-02-13 21:31:57','2026-02-28 04:56:31'),(3,'koo','user','hhhh','123123','active','2026-02-11 21:15:22','2026-02-28 04:56:31'),(4,'kk','user','hhh','123123','active','2026-02-11 21:34:31','2026-02-28 04:56:31'),(5,'qqq','user','qqq','123','active','2026-02-13 22:32:00','2026-02-28 04:56:31'),(6,'yyyyy','user','yyyyy','123','ban','2026-02-12 01:54:47','2026-02-28 04:56:31'),(7,'','user','qqq@gmail.com','','ban','2026-02-13 01:10:54','2026-02-28 04:56:31'),(8,'asdf','user','asd@gmail.com','','active','2026-02-11 22:00:15','2026-02-28 04:56:31'),(9,'asdfg','user','asdf@gmail.com','1234','active','2026-02-11 22:02:01','2026-02-28 04:56:31'),(10,'qwerty','user','asdfg@gmail.com','','active','2026-02-11 22:16:51','2026-02-28 04:56:31'),(11,'qwertyu','user','qwerty@gmail.com','','active','2026-02-11 22:26:43','2026-02-28 04:56:31'),(12,'qwertyuiop','user','qwertyuiop@gmail.com','','active','2026-02-11 22:28:57','2026-02-28 04:56:31'),(13,'q123','user','qQ123@gmail.com','','active','2026-02-11 22:40:13','2026-02-28 04:56:31'),(14,'ER123','user','lol123@gmail.com','Ahref123@','active','2026-02-11 23:04:29','2026-02-28 04:56:31'),(15,'koshane','user','asdfasdf@gmail.com','Shirt12!','active','2026-03-19 08:28:01','2026-03-19 08:28:01');
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
  KEY `account_id` (`account_id`),
  KEY `skin_id` (`skin_id`),
  CONSTRAINT `characters_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `characters_ibfk_2` FOREIGN KEY (`skin_id`) REFERENCES `skin` (`skin_id`)
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
  `map_name` varchar(20) NOT NULL,
  PRIMARY KEY (`map_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_map`
--

LOCK TABLES `game_map` WRITE;
/*!40000 ALTER TABLE `game_map` DISABLE KEYS */;
INSERT INTO `game_map` VALUES (1,'home_map'),(2,'Home_interior'),(3,'left_map'),(4,'right_map'),(5,'upper_map');
/*!40000 ALTER TABLE `game_map` ENABLE KEYS */;
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
  `stats_id` int DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `item_type` varchar(10) NOT NULL DEFAULT '',
  PRIMARY KEY (`item_id`),
  KEY `stats_id` (`stats_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`stats_id`) REFERENCES `stats` (`stats_id`)
) ENGINE=InnoDB AUTO_INCREMENT=347 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (235,'Wheat Seed',10,121,'wheat_seed.png',''),(236,'Sunflower Seed',10,122,'sunflower_seed.png',''),(237,'Pumpkin Seed',10,123,'pumpkin_seed.png',''),(238,'Pineapple Seed',10,124,'pineapple_seed.png',''),(239,'Onion Seed',10,125,'onion_seed.png',''),(240,'Green Eggplant Seed',10,126,'green_eggplant_seed.png',''),(241,'Grape Seed',10,127,'grape_seed.png',''),(242,'Cucumber Seed',10,128,'cucumber_seed.png',''),(243,'Chilli Seed',10,129,'chilli_seed.png',''),(244,'Cauliflower Seed',10,130,'cauliflower_seed.png',''),(245,'Broccoli Seed',10,131,'broccoli_seed.png',''),(246,'Beetroot Seed',10,132,'beetroot_seed.png',''),(247,'Bean Seed',10,133,'bean_seed.png',''),(248,'Pink Radish',50,22,'pink_radish.png',''),(249,'Patty Pan',100,23,'patty_pan.png',''),(250,'Onion',30,24,'onion.png',''),(251,'Long Beans',50,25,'long_beans.png',''),(252,'Lettuce',60,26,'lettuce.png',''),(253,'Ice Tuber',200,27,'ice_tuber.png',''),(254,'Hop cone',100,28,'hop_cone.png',''),(255,'Ham',500,29,'ham.png',''),(256,'Green Zucchili',80,30,'green_zucchili.png',''),(257,'Green Pea',40,31,'green_pea.png',''),(258,'Green Chili',20,32,'green_chilli.png',''),(259,'Gourd',45,33,'gourd.png',''),(260,'Garlic',30,34,'garlic.png',''),(261,'Fennel bulb',60,35,'fennel_bulb.png',''),(262,'Eggplant',40,36,'eggplant.png',''),(263,'DaiKon',30,37,'daikon.png',''),(264,'Cucumber',30,38,'cucumber.png',''),(265,'Corn',50,39,'corn.png',''),(266,'Chili pepper',40,40,'chili_pepper.png',''),(267,'Chicken Wing',400,41,'chicken_wing.png',''),(268,'Chicken thigh',470,42,'chicken_thigh.png',''),(269,'Cauliflower',70,43,'cauliflower.png',''),(270,'Carrot',40,44,'carrot.png',''),(271,'Cabbage',70,45,'cabbage.png',''),(272,'Broccoli',60,46,'broccoli.png',''),(273,'Bok Choy',40,47,'bok_choy.png',''),(274,'Bitter Melon',40,48,'bitter_melon.png',''),(275,'Beef',600,49,'beef.png',''),(276,'Beans',40,50,'beans.png',''),(277,'Bamboo Shoot',30,51,'bamboo_shoot.png',''),(278,'Asparagus',30,52,'asparagus.png',''),(279,'Antichoke',50,53,'artichoke.png',''),(280,'Bacon',100,54,'bacon.png',''),(281,'Bear Skin',50,55,'bear_skin.png',''),(282,'Blackened Meat',50,56,'blackened_meat.png',''),(283,'Blue Cloth',10,57,'blue_cloth.png',''),(284,'Bone',10,58,'bone.png',''),(285,'Dirty Rigs',10,59,'dirty_rags.png',''),(286,'Feathered Hide',10,60,'feathered_hide.png',''),(287,'Fish Meat',10,61,'fish_meat.png',''),(288,'Gilled Meat',10,62,'gilled_meat.png',''),(289,'Ham',20,63,'ham.png',''),(290,'Haunch',10,64,'haunch.png',''),(291,'Heart',10,65,'heart.png',''),(292,'Hooves',20,66,'hooves.png',''),(293,'Horn1',10,67,'horn1.png',''),(294,'Horn2',10,68,'horn2.png',''),(295,'Horn3',10,69,'horn3.png',''),(296,'Horn4',10,70,'horn4.png',''),(297,'Horn5',10,71,'horn5.png',''),(298,'Horn6',10,72,'horn6.png',''),(299,'Kidney',20,73,'kidney.png',''),(300,'Large Meat Chunk',10,74,'large_meat_chunk.png',''),(301,'Pork',10,75,'pork.png',''),(302,'Raw Ribs',10,76,'raw_ribs.png',''),(303,'Rotten Meat',10,77,'rotten_meat.png',''),(304,'Scarly Meat',10,78,'scaly_meat.png',''),(305,'Red Cloth',10,79,'red_cloth.png',''),(306,'Skull1',10,80,'skull1.png',''),(307,'Skull2',10,81,'skull2.png',''),(308,'Skull3',10,82,'skull3.png',''),(309,'Skull3',10,83,'skull4.png',''),(310,'Small Meat Chunk',10,84,'small_meat_chunk.png',''),(311,'Stringy Meat',10,85,'stringy_meat.png',''),(312,'Tongue',10,86,'tongue.png',''),(314,'roasted_harvest_plate',100,92,'roasted_harvest_plate.png',''),(315,'beef_stew',10,93,'beef_stew.png',''),(316,'garden_salad',100,94,'garden_salad.png',''),(317,'spicy_pork_stir-fry',100,95,'spicy_pork_stir-fry.png',''),(318,'steak_dinner',100,96,'steak_dinner.png',''),(319,'root_vegetable_beef_soup',100,97,'root_vegetable_beef_soup.png',''),(320,'spicy_eggplant_tomato',100,98,'spicy_eggplant_tomato.png',''),(321,'spicy_eggplant',100,99,'spicy_eggplant.png',''),(322,'root_vegetable_mix',100,100,'root_mix.png',''),(323,'roasted_pumpkin_mash',100,101,'roasted_pumpkin_mash.png',''),(324,'chicken_corn_soup',100,102,'chicken_corn_soup.png',''),(325,'pork_cabbage_stir-fry',100,103,'pork_cabbage_stir-fry.png',''),(326,'beef_potato_stew',100,104,'beef_potato_stew.png',''),(327,'pepper_steak',100,105,'pepper_steak.png',''),(328,'salad_leaves',100,106,'salad_leaves.png',''),(329,'fresh_salad',100,107,'fresh_salad.png',''),(330,'grilled_steak',100,108,'grilled_steak.png',''),(331,'tomato_slices',100,109,'tomato_slices.png',''),(332,'baked_potato',100,110,'baked_potato.png',''),(333,'grilled_ham',100,111,'grilled_ham.png',''),(334,'Tomato',50,9,'tomato.png',''),(335,'TintoBerry',50,10,'tintoberry.png',''),(336,'Sweet Potato',50,11,'sweet_potato.png',''),(337,'White Eggplant',50,12,'white_eggplant.png',''),(338,'Pumpkin',50,13,'pumpkin.png',''),(339,'Umbrella Cabbage',50,14,'umbrella_cabbage.png',''),(340,'Turnip',50,15,'turnip.png',''),(341,'Snow Berry',50,16,'snowberry.png',''),(342,'Rhubarb',50,17,'rhubarb.png',''),(343,'Red Bellpepper',50,18,'red_bellpepper.png',''),(344,'Radish',50,19,'radish.png',''),(345,'Potato',50,20,'potato.png',''),(346,'Pork ribs',50,21,'pork_ribs.png','');
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
  KEY `item_id` (`item_id`),
  CONSTRAINT `map_item_ibfk_1` FOREIGN KEY (`map_id`) REFERENCES `game_map` (`map_id`),
  CONSTRAINT `map_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`)
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
-- Table structure for table `save_game`
--

DROP TABLE IF EXISTS `save_game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `save_game` (
  `saved_id` int NOT NULL AUTO_INCREMENT,
  `character_id` int NOT NULL,
  `saved_name` varchar(20) NOT NULL,
  `last_saved_date` timestamp NOT NULL DEFAULT (now()),
  `current_map_id` int DEFAULT NULL,
  `stats_id` int DEFAULT NULL,
  `skill_id` int DEFAULT NULL,
  `money` int NOT NULL DEFAULT '0',
  `saved_location_x` int NOT NULL,
  `saved_location_y` int NOT NULL,
  `exp` int NOT NULL,
  `selected_hotbar_index` int NOT NULL DEFAULT '0',
  `hotbar_visible` tinyint(1) NOT NULL DEFAULT '1',
  `world_name` varchar(50) NOT NULL DEFAULT '',
  `current_hp` int NOT NULL DEFAULT '45',
  `current_stamina` int NOT NULL DEFAULT '50',
  PRIMARY KEY (`saved_id`),
  KEY `character_id` (`character_id`),
  KEY `current_map_id` (`current_map_id`),
  KEY `stats_id` (`stats_id`),
  KEY `skill_id` (`skill_id`),
  CONSTRAINT `save_game_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`character_id`),
  CONSTRAINT `save_game_ibfk_2` FOREIGN KEY (`current_map_id`) REFERENCES `game_map` (`map_id`),
  CONSTRAINT `save_game_ibfk_3` FOREIGN KEY (`stats_id`) REFERENCES `stats` (`stats_id`),
  CONSTRAINT `save_game_ibfk_4` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `save_game`
--

LOCK TABLES `save_game` WRITE;
/*!40000 ALTER TABLE `save_game` DISABLE KEYS */;
INSERT INTO `save_game` VALUES (1,1,'Save 1','2026-03-29 07:00:50',1,88,2,100,160,135,100,0,1,'',45,50),(2,2,'Save 1','2026-03-21 06:46:36',1,89,NULL,0,432,182,0,0,1,'ff',45,50),(3,1,'Save 2','2026-03-20 07:36:13',1,90,NULL,0,160,135,0,0,1,'world_3',45,50),(5,1,'Save 1','2026-03-27 15:41:54',1,112,1,50,344,188,100,0,1,'asd',45,50),(12,1,'Save 4','2026-03-29 07:59:54',1,119,9,30,160,135,100,0,1,'qweqwe',45,50),(13,1,'Save 3','2026-03-29 04:26:33',1,120,10,0,160,135,100,0,1,'me',45,50);
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
  KEY `fk_saved_chest_item_item` (`item_id`),
  CONSTRAINT `fk_saved_chest_item_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_chest_item_save` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_chest_item`
--

LOCK TABLES `saved_chest_item` WRITE;
/*!40000 ALTER TABLE `saved_chest_item` DISABLE KEYS */;
INSERT INTO `saved_chest_item` VALUES (12,235,1),(12,236,1),(12,238,1),(12,239,1),(12,240,1),(13,235,1),(13,242,1);
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
INSERT INTO `saved_farm_plot` VALUES (1,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(1,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(2,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(3,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(5,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(12,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 1',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 10',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 11',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 12',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 13',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 14',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 15',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 16',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 17',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 18',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 19',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 2',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 20',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 21',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 22',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 23',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 24',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 25',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 26',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 27',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 28',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 29',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 3',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 30',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 31',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 32',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 33',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 34',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 35',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 36',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 37',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 38',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 39',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 4',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 40',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 41',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 42',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 43',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 44',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 45',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 5',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 6',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 7',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 8',NULL,-1,-1,0,3600,900,300),(13,'home_map','Plot 9',NULL,-1,-1,0,3600,900,300);
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
  KEY `fk_saved_hotbar_item` (`item_id`),
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
  KEY `item_id` (`item_id`),
  CONSTRAINT `saved_item_ibfk_1` FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`),
  CONSTRAINT `saved_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_item`
--

LOCK TABLES `saved_item` WRITE;
/*!40000 ALTER TABLE `saved_item` DISABLE KEYS */;
INSERT INTO `saved_item` VALUES (2,241,1),(3,235,1),(3,242,1),(5,235,1),(12,237,1),(12,242,1);
/*!40000 ALTER TABLE `saved_item` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `saved_unlocked_recipe` ENABLE KEYS */;
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
INSERT INTO `saved_time` VALUES (1,1,4.351699199999998),(2,1,10.469164200000012),(3,1,140.5822159009999),(5,1,36.70162290000008),(12,1,48.608027000000085),(13,1,48.0978993);
/*!40000 ALTER TABLE `saved_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skill`
--

DROP TABLE IF EXISTS `skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skill` (
  `skill_id` int NOT NULL AUTO_INCREMENT,
  `farming` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `gathering` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `crafting` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `fishing` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `battling` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  `cooking` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY (`skill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skill`
--

LOCK TABLES `skill` WRITE;
/*!40000 ALTER TABLE `skill` DISABLE KEYS */;
INSERT INTO `skill` VALUES (1,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(2,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(3,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(4,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(5,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(6,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(7,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(9,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000),(10,0000000000,0000000000,0000000000,0000000000,0000000000,0000000000);
/*!40000 ALTER TABLE `skill` ENABLE KEYS */;
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
  PRIMARY KEY (`skin_id`)
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
  PRIMARY KEY (`spawn_point_id`),
  KEY `map_id` (`map_id`),
  CONSTRAINT `spawn_point_ibfk_1` FOREIGN KEY (`map_id`) REFERENCES `game_map` (`map_id`)
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
-- Table structure for table `stats`
--

DROP TABLE IF EXISTS `stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stats` (
  `stats_id` int NOT NULL AUTO_INCREMENT,
  `stats_type` varchar(20) NOT NULL DEFAULT 'item',
  `str` int NOT NULL DEFAULT '0',
  `agi` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `vit` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `stamina` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`stats_id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stats`
--

LOCK TABLES `stats` WRITE;
/*!40000 ALTER TABLE `stats` DISABLE KEYS */;
INSERT INTO `stats` VALUES (1,'item',1,1,1,1,1,1),(2,'item',0,0,0,0,0,0),(3,'item',0,0,0,0,0,0),(4,'item',0,0,0,0,0,0),(5,'item',0,0,0,0,0,0),(6,'item',0,0,0,0,0,0),(7,'item',0,0,0,0,0,0),(8,'item',0,0,0,0,0,0),(9,'item',0,0,0,0,0,0),(10,'item',0,0,0,0,0,0),(11,'item',0,0,0,0,0,0),(12,'item',0,0,0,0,0,0),(13,'item',0,0,0,0,0,0),(14,'item',0,0,0,0,0,0),(15,'item',0,0,0,0,0,0),(16,'item',0,0,0,0,0,0),(17,'item',0,0,0,0,0,0),(18,'item',0,0,0,0,0,0),(19,'item',0,0,0,0,0,0),(20,'item',0,0,0,0,0,0),(21,'item',0,0,0,0,0,0),(22,'item',0,0,0,0,0,0),(23,'item',0,0,0,0,0,0),(24,'item',0,0,0,0,0,0),(25,'item',0,0,0,0,0,0),(26,'item',0,0,0,0,0,0),(27,'item',0,0,0,0,0,0),(28,'item',0,0,0,0,0,0),(29,'item',0,0,0,0,0,0),(30,'item',0,0,0,0,0,0),(31,'item',0,0,0,0,0,0),(32,'item',0,0,0,0,0,0),(33,'item',0,0,0,0,0,0),(34,'item',0,0,0,0,0,0),(35,'item',0,0,0,0,0,0),(36,'item',0,0,0,0,0,0),(37,'item',0,0,0,0,0,0),(38,'item',0,0,0,0,0,0),(39,'item',0,0,0,0,0,0),(40,'item',0,0,0,0,0,0),(41,'item',0,0,0,0,0,0),(42,'item',0,0,0,0,0,0),(43,'item',0,0,0,0,0,0),(44,'item',0,0,0,0,0,0),(45,'item',0,0,0,0,0,0),(46,'item',0,0,0,0,0,0),(47,'item',0,0,0,0,0,0),(48,'item',0,0,0,0,0,0),(49,'item',0,0,0,0,0,0),(50,'item',0,0,0,0,0,0),(51,'item',0,0,0,0,0,0),(52,'item',0,0,0,0,0,0),(53,'item',0,0,0,0,0,0),(54,'item',0,0,0,0,0,0),(55,'item',0,0,0,0,0,0),(56,'item',0,0,0,0,0,0),(57,'item',0,0,0,0,0,0),(58,'item',0,0,0,0,0,0),(59,'item',0,0,0,0,0,0),(60,'item',0,0,0,0,0,0),(61,'item',0,0,0,0,0,0),(62,'item',0,0,0,0,0,0),(63,'item',0,0,0,0,0,0),(64,'item',0,0,0,0,0,0),(65,'item',0,0,0,0,0,0),(66,'item',0,0,0,0,0,0),(67,'item',0,0,0,0,0,0),(68,'item',0,0,0,0,0,0),(69,'item',0,0,0,0,0,0),(70,'item',0,0,0,0,0,0),(71,'item',0,0,0,0,0,0),(72,'item',0,0,0,0,0,0),(73,'item',0,0,0,0,0,0),(74,'item',0,0,0,0,0,0),(75,'item',0,0,0,0,0,0),(76,'item',0,0,0,0,0,0),(77,'item',0,0,0,0,0,0),(78,'item',0,0,0,0,0,0),(79,'item',0,0,0,0,0,0),(80,'item',0,0,0,0,0,0),(81,'item',0,0,0,0,0,0),(82,'item',0,0,0,0,0,0),(83,'item',0,0,0,0,0,0),(84,'item',0,0,0,0,0,0),(85,'item',0,0,0,0,0,0),(86,'item',0,0,0,0,0,0),(88,'player_save',1,1,1,1,45,50),(89,'player_save',0,5,0,0,100,100),(90,'player_save',0,5,0,0,100,100),(91,'player_save',0,5,0,0,100,100),(92,'item',0,0,0,0,0,0),(93,'item',0,0,0,0,0,0),(94,'item',0,0,0,0,0,0),(95,'item',0,0,0,0,0,0),(96,'item',0,0,0,0,0,0),(97,'item',0,0,0,0,0,0),(98,'item',0,0,0,0,0,0),(99,'item',0,0,0,0,0,0),(100,'item',0,0,0,0,0,0),(101,'item',0,0,0,0,0,0),(102,'item',0,0,0,0,0,0),(103,'item',0,0,0,0,0,0),(104,'item',0,0,0,0,0,0),(105,'item',0,0,0,0,0,0),(106,'item',0,0,0,0,0,0),(107,'item',0,0,0,0,0,0),(108,'item',0,0,0,0,0,0),(109,'item',0,0,0,0,0,0),(110,'item',0,0,0,0,0,0),(111,'item',0,0,0,0,0,0),(112,'player_save',1,1,1,1,45,50),(113,'player_save',1,1,1,1,45,50),(114,'player_save',1,1,1,1,45,50),(115,'player_save',1,1,1,1,45,50),(116,'player_save',1,1,1,1,45,50),(117,'player_save',1,1,1,1,45,50),(119,'player_save',1,1,1,1,45,50),(120,'player_save',1,1,1,1,45,50),(121,'item',0,0,0,0,0,0),(122,'item',0,0,0,0,0,0),(123,'item',0,0,0,0,0,0),(124,'item',0,0,0,0,0,0),(125,'item',0,0,0,0,0,0),(126,'item',0,0,0,0,0,0),(127,'item',0,0,0,0,0,0),(128,'item',0,0,0,0,0,0),(129,'item',0,0,0,0,0,0),(130,'item',0,0,0,0,0,0),(131,'item',0,0,0,0,0,0),(132,'item',0,0,0,0,0,0),(133,'item',0,0,0,0,0,0);
/*!40000 ALTER TABLE `stats` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-30 13:43:25
