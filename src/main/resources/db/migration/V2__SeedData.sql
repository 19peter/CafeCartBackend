-- Seed data for Vendors, Vendor Shops, Categories, Products, and Inventory
-- This migration temporarily relaxes the circular FK between vendors and vendor_access_accounts
-- to allow inserting consistent seed data, then restores the constraints.

-- Wrap in a transaction so either all seeds apply or none
BEGIN;

-- 1) Temporarily drop FK from vendors.vendor_access_account_id and allow NULLs
ALTER TABLE vendors DROP CONSTRAINT IF EXISTS FKmvrnkjpgn0jcn1xs8g4cucjv3;
ALTER TABLE vendors ALTER COLUMN vendor_access_account_id DROP NOT NULL;

-- 2) Seed categories (required by products)
INSERT INTO categories (is_active, created_at, updated_at, description, image_url, name)
VALUES
  (true, NOW(), NOW(), 'Freshly roasted coffee beans and brewed coffee beverages', NULL, 'Coffee'),
  (true, NOW(), NOW(), 'Assorted teas and herbal infusions', NULL, 'Tea'),
  (true, NOW(), NOW(), 'Baked goods including pastries and bread', NULL, 'Bakery')
ON CONFLICT (name) DO NOTHING;

-- 3) Seed vendors (without access accounts for now)
INSERT INTO vendors (is_active, created_at, updated_at, email, image_url, name, phone_number, vendor_access_account_id)
VALUES
  (true, NOW(), NOW(), 'contact@beanbarn.com', NULL, 'Bean Barn', '+15550000001', NULL),
  (true, NOW(), NOW(), 'hello@steepsips.com', NULL, 'Steep Sips', '+15550000002', NULL),
  (true, NOW(), NOW(), 'info@butterbake.com', NULL, 'Butter & Bake', '+15550000003', NULL)
ON CONFLICT (email) DO NOTHING;

-- 4) Seed vendor access accounts (requires vendor_id)
-- Use distinct account emails for uniqueness
INSERT INTO vendor_access_accounts (
  failed_login_attempts, is_active, account_locked_until, created_at, last_login, updated_at,
  vendor_id, email, password
)
SELECT 0, true, NULL, NOW(), NULL, NOW(), v.id, acc_email, acc_password
FROM (
  VALUES
    ('contact@beanbarn.com', 'owner@beanbarn.com', '{noop}password1'),
    ('hello@steepsips.com', 'owner@steepsips.com', '{noop}password2'),
    ('info@butterbake.com', 'owner@butterbake.com', '{noop}password3')
) AS s(vendor_email, acc_email, acc_password)
JOIN vendors v ON v.email = s.vendor_email
ON CONFLICT (email) DO NOTHING;

-- 5) Link vendors to their access accounts and then restore NOT NULL and FK
UPDATE vendors v
SET vendor_access_account_id = vaa.id
FROM vendor_access_accounts vaa
WHERE vaa.vendor_id = v.id AND v.vendor_access_account_id IS NULL;

-- Restore NOT NULL and FK constraint
ALTER TABLE vendors ALTER COLUMN vendor_access_account_id SET NOT NULL;
ALTER TABLE vendors
  ADD CONSTRAINT FKmvrnkjpgn0jcn1xs8g4cucjv3
  FOREIGN KEY (vendor_access_account_id) REFERENCES vendor_access_accounts(id);

-- 6) Seed vendor shops
INSERT INTO vendor_shops (
  closing_time, is_active, is_delivery_available, last_order_time, opening_time,
  created_at, updated_at, vendor_id, address, email, logo_url, name, phone_number
)
SELECT TIME '20:00', true, true, TIME '19:30', TIME '08:00', NOW(), NOW(), v.id,
       addr, shop_email, NULL, shop_name, shop_phone
FROM (
  VALUES
    ('contact@beanbarn.com', 'Bean Barn - Downtown', '101 Main St, Cityville', 'downtown@beanbarn.com', '+15551110001'),
    ('contact@beanbarn.com', 'Bean Barn - Riverside', '22 River Rd, Cityville', 'riverside@beanbarn.com', '+15551110002'),
    ('hello@steepsips.com', 'Steep Sips - Central', '500 Market Ave, Townsburg', 'central@steepsips.com', '+15552220001'),
    ('info@butterbake.com', 'Butter & Bake - West', '77 Baker Ln, Villageton', 'west@butterbake.com', '+15553330001')
) AS s(vendor_email, shop_name, addr, shop_email, shop_phone)
JOIN vendors v ON v.email = s.vendor_email
ON CONFLICT DO NOTHING;

-- 7) Seed products for vendors
INSERT INTO products (
  is_available, is_deleted, price, category_id, created_at, updated_at, vendor_id,
  description, image_url, name
)
SELECT true, false, price, c.id, NOW(), NOW(), v.id, desc_txt, NULL, prod_name
FROM (
  VALUES
    ('contact@beanbarn.com', 'Coffee', 'Espresso',        2.50, 'Strong and rich espresso shot'),
    ('contact@beanbarn.com', 'Coffee', 'Cappuccino',      3.50, 'Espresso with steamed milk and foam'),
    ('hello@steepsips.com',  'Tea',    'Matcha Latte',    4.00, 'Ceremonial matcha with milk'),
    ('hello@steepsips.com',  'Tea',    'Chai Latte',      3.75, 'Spiced black tea with milk'),
    ('info@butterbake.com',  'Bakery', 'Butter Croissant',2.25, 'Flaky croissant with butter'),
    ('info@butterbake.com',  'Bakery', 'Chocolate Muffin',2.75, 'Rich chocolate muffin')
) AS s(vendor_email, cat_name, prod_name, price, desc_txt)
JOIN vendors v ON v.email = s.vendor_email
JOIN categories c ON c.name = s.cat_name
ON CONFLICT DO NOTHING;

-- 8) Seed inventory per shop for products of corresponding vendor
-- Distribute some initial quantities
INSERT INTO inventory (
  quantity, created_at, product_id, updated_at, vendor_shop_id
)
SELECT qty, NOW(), p.id, NOW(), vs.id
FROM (
  VALUES
    ('Bean Barn - Downtown', 'Espresso', 50),
    ('Bean Barn - Downtown', 'Cappuccino', 40),
    ('Bean Barn - Riverside', 'Espresso', 30),
    ('Bean Barn - Riverside', 'Cappuccino', 25),
    ('Steep Sips - Central',  'Matcha Latte', 35),
    ('Steep Sips - Central',  'Chai Latte',   45),
    ('Butter & Bake - West',  'Butter Croissant', 60),
    ('Butter & Bake - West',  'Chocolate Muffin', 55)
) AS s(shop_name, prod_name, qty)
JOIN vendor_shops vs ON vs.name = s.shop_name
JOIN products p ON p.name = s.prod_name AND p.vendor_id = vs.vendor_id
ON CONFLICT DO NOTHING;

COMMIT;
