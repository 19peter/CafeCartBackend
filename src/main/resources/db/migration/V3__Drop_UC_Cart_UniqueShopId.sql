-- Drop unique constraint if you know the exact name
ALTER TABLE cart DROP CONSTRAINT IF EXISTS cart_shop_id_key;