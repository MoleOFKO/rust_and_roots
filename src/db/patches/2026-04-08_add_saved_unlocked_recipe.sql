CREATE TABLE IF NOT EXISTS `saved_unlocked_recipe` (
  `saved_id` int NOT NULL,
  `recipe_key` varchar(100) NOT NULL,
  PRIMARY KEY (`saved_id`, `recipe_key`),
  CONSTRAINT `fk_saved_unlocked_recipe_save`
    FOREIGN KEY (`saved_id`) REFERENCES `save_game` (`saved_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
