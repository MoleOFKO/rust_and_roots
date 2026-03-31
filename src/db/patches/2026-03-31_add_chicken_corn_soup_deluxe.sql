-- Adds missing output item used by CookController recipes:
-- chicken_thigh + corn + carrot -> chicken_corn_soup_deluxe
-- Uses existing image asset: chicken_corn_soup.png

START TRANSACTION;

SET @target_name := 'chicken_corn_soup_deluxe';
SET @existing_item_id := (
    SELECT item_id
    FROM items
    WHERE LOWER(REPLACE(item_name, '_', ' ')) = LOWER(REPLACE(@target_name, '_', ' '))
    LIMIT 1
);

INSERT INTO item_stats (str_stat, agi_stat, dex_stat, vit_stat, hp, stamina)
SELECT 0, 0, 0, 0, 0, 0
WHERE @existing_item_id IS NULL;

SET @new_item_stats_id := LAST_INSERT_ID();
SET @fallback_type_id := (SELECT COALESCE(MIN(item_type_id), 1) FROM item_types);

INSERT INTO items (item_name, price, item_stats_id, image, item_type_id)
SELECT @target_name, 120, @new_item_stats_id, 'chicken_corn_soup.png', @fallback_type_id
WHERE @existing_item_id IS NULL;

COMMIT;
