-- SQL script to insert sample data with multiple product units
-- Database: sms_db

USE sms_db;

-- 1. Insert Brands (if not exist)
INSERT INTO `Brand` (`name`, `status`, `created_at`) 
VALUES 
('Coca-Cola', 'ACTIVE', NOW()),
('Hảo Hảo', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE `status` = 'ACTIVE';

-- 2. Insert Categories
INSERT INTO `Category` (`name`, `description`, `status`, `created_at`) 
VALUES 
('Nước giải khát', 'Các loại nước ngọt, nước lọc, trà đóng chai', 'ACTIVE', NOW()),
('Thực phẩm ăn liền', 'Mì gói, cháo gói, xúc xích ăn liền', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE `status` = 'ACTIVE';

-- 3. Insert Units
INSERT INTO `Unit` (`name`, `created_at`)
VALUES 
('Lon', NOW()),
('Thùng', NOW()),
('Gói', NOW()),
('Hộp', NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 4. Insert Products and Product Units
-- Product: Coca-Cola 320ml
INSERT INTO `Product` (`name`, `description`, `status`, `created_at`, `category_id`, `brand_id`)
SELECT 'Coca-Cola 320ml', 'Nước ngọt Coca-Cola vị nguyên bản', 'ACTIVE', NOW(), 
       (SELECT `id` FROM `Category` WHERE `name` = 'Nước giải khát' LIMIT 1), 
       (SELECT `id` FROM `Brand` WHERE `name` = 'Coca-Cola' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Coca-Cola 320ml');

-- Lon (Base unit) for Coca-Cola 320ml
INSERT INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT 
    (SELECT `id` FROM `Product` WHERE `name` = 'Coca-Cola 320ml' LIMIT 1),
    (SELECT `id` FROM `Unit` WHERE `name` = 'Lon' LIMIT 1),
    1,
    10000.00,
    '8934822011003',
    1,
    'COCA-LON',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `ProductUnit` 
    WHERE `product_id` = (SELECT `id` FROM `Product` WHERE `name` = 'Coca-Cola 320ml' LIMIT 1)
      AND `unit_id` = (SELECT `id` FROM `Unit` WHERE `name` = 'Lon' LIMIT 1)
);

-- Thùng (24 lon) for Coca-Cola 320ml
INSERT INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT 
    (SELECT `id` FROM `Product` WHERE `name` = 'Coca-Cola 320ml' LIMIT 1),
    (SELECT `id` FROM `Unit` WHERE `name` = 'Thùng' LIMIT 1),
    24,
    220000.00,
    '8934822011003-T24',
    0,
    'COCA-THUNG',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `ProductUnit` 
    WHERE `product_id` = (SELECT `id` FROM `Product` WHERE `name` = 'Coca-Cola 320ml' LIMIT 1)
      AND `unit_id` = (SELECT `id` FROM `Unit` WHERE `name` = 'Thùng' LIMIT 1)
);


-- Product: Mì Hảo Hảo Tôm Chua Cay
INSERT INTO `Product` (`name`, `description`, `status`, `created_at`, `category_id`, `brand_id`)
SELECT 'Mì Hảo Hảo Tôm Chua Cay', 'Mì gói ăn liền Hảo Hảo vị Tôm Chua Cay thơm ngon', 'ACTIVE', NOW(), 
       (SELECT `id` FROM `Category` WHERE `name` = 'Thực phẩm ăn liền' LIMIT 1), 
       (SELECT `id` FROM `Brand` WHERE `name` = 'Hảo Hảo' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay');

-- Gói (Base unit) for Mì Hảo Hảo
INSERT INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT 
    (SELECT `id` FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay' LIMIT 1),
    (SELECT `id` FROM `Unit` WHERE `name` = 'Gói' LIMIT 1),
    1,
    4500.00,
    '8934567890123',
    1,
    'HAOHAO-GOI',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `ProductUnit` 
    WHERE `product_id` = (SELECT `id` FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay' LIMIT 1)
      AND `unit_id` = (SELECT `id` FROM `Unit` WHERE `name` = 'Gói' LIMIT 1)
);

-- Thùng (30 gói) for Mì Hảo Hảo
INSERT INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT 
    (SELECT `id` FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay' LIMIT 1),
    (SELECT `id` FROM `Unit` WHERE `name` = 'Thùng' LIMIT 1),
    30,
    130000.00,
    '8934567890123-T30',
    0,
    'HAOHAO-THUNG',
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `ProductUnit` 
    WHERE `product_id` = (SELECT `id` FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay' LIMIT 1)
      AND `unit_id` = (SELECT `id` FROM `Unit` WHERE `name` = 'Thùng' LIMIT 1)
);
