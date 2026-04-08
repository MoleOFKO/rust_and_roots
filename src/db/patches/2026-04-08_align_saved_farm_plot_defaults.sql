ALTER TABLE `saved_farm_plot`
  MODIFY COLUMN `total_growth_seconds` double NOT NULL DEFAULT '3600',
  MODIFY COLUMN `water_cooldown_seconds` double NOT NULL DEFAULT '900',
  MODIFY COLUMN `water_reduction_seconds` double NOT NULL DEFAULT '300';
