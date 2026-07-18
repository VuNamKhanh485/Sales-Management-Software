-- =============================================================
-- SAMPLE DATA FOR SMS_DB (all tables)
-- Database: sms_db
-- =============================================================
USE sms_db;

-- =============================================================
-- 1. BRANCH
-- =============================================================
INSERT IGNORE INTO `Branch` (`branch_code`, `name`, `phone`, `email`, `address`, `status`, `created_at`)
VALUES
('B001', 'Chi nhánh Trung Tâm', '02838234567', 'cn1@fpt.com', '123 Nguyễn Huệ, Quận 1, TP.HCM', 'ACTIVE', NOW()),
('B002', 'Chi nhánh Gò Vấp', '02838901234', 'cn2@fpt.com', '456 Lê Văn Thọ, Gò Vấp, TP.HCM', 'ACTIVE', NOW()),
('B003', 'Chi nhánh Thủ Đức', '02837281234', 'cn3@fpt.com', '789 Võ Văn Ngân, Thủ Đức, TP.HCM', 'ACTIVE', NOW());

-- =============================================================
-- 2. ROLE
-- =============================================================
INSERT IGNORE INTO `Role` (`code`, `name`, `description`)
VALUES
('OWNER', 'Chủ cửa hàng', 'Toàn quyền quản trị hệ thống'),
('BRANCH_MANAGER', 'Quản lý chi nhánh', 'Quản lý hoạt động của chi nhánh'),
('SALE_STAFF', 'Nhân viên bán hàng', 'Nhân viên bán hàng tại quầy'),
('WAREHOUSE_STAFF', 'Nhân viên kho', 'Quản lý nhập xuất tồn kho');

-- =============================================================
-- 3. CUSTOMER_RANK
-- =============================================================
INSERT IGNORE INTO `CustomerRank` (`name`, `discount_rate`, `condition_total_revenue`, `description`, `created_at`)
VALUES
('Đồng', 0.00, 0, 'Khách hàng mới, chưa có doanh thu', NOW()),
('Bạc', 0.03, 2000000, 'Khách hàng thân thiết - chiết khấu 3%', NOW()),
('Vàng', 0.05, 5000000, 'Khách hàng VIP - chiết khấu 5%', NOW()),
('Kim Cương', 0.10, 15000000, 'Khách hàng VIP cao cấp - chiết khấu 10%', NOW());

-- =============================================================
-- 4. EMPLOYEE
-- =============================================================
INSERT IGNORE INTO `Employee` (`employee_code`, `full_name`, `email`, `password_hash`, `phone`, `address`, `gender`, `dob`, `hired_date`, `base_salary`, `work_status`, `note`, `created_at`, `branch_id`, `role_id`)
VALUES
('NV001', 'Nguyễn Văn An', 'an.nguyen@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909123456', '123 Lý Tự Trọng, Quận 1, TP.HCM', 'MALE', '1990-05-15', '2024-01-01', 25000000.00, 'ACTIVE', 'Chủ cửa hàng', NOW(), 1, 1),
('NV002', 'Trần Thị Bình', 'binh.tran@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909234567', '456 Nguyễn Trãi, Quận 5, TP.HCM', 'FEMALE', '1995-08-20', '2024-01-15', 12000000.00, 'ACTIVE', 'Quản lý chi nhánh Trung Tâm', NOW(), 1, 2),
('NV003', 'Lê Văn Cường', 'cuong.le@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909345678', '789 Hoàng Diệu, Thủ Đức, TP.HCM', 'MALE', '1998-12-10', '2024-02-01', 8000000.00, 'ACTIVE', 'Nhân viên bán hàng', NOW(), 1, 3),
('NV004', 'Phạm Thị Dung', 'dung.pham@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909456789', '321 Phạm Văn Đồng, Gò Vấp', 'FEMALE', '2000-03-25', '2024-03-01', 7500000.00, 'ACTIVE', 'Nhân viên bán hàng', NOW(), 2, 3),
('NV005', 'Hoàng Minh Em', 'em.hoang@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909567890', '654 Xô Viết Nghệ Tĩnh, Bình Thạnh', 'MALE', '1997-07-18', '2024-03-15', 8500000.00, 'ACTIVE', 'Nhân viên kho', NOW(), 1, 4),
('NV006', 'Ngô Thị Phương', 'phuong.ngo@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909678901', '987 Lê Duẩn, Đà Nẵng', 'FEMALE', '1996-11-30', '2024-04-01', 12000000.00, 'ACTIVE', 'Quản lý chi nhánh Gò Vấp', NOW(), 2, 2),
('NV007', 'Vũ Đức Giang', 'giang.vu@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909789012', '147 Nguyễn Văn Linh, TP.HCM', 'MALE', '1993-09-05', '2024-04-15', 13000000.00, 'ACTIVE', 'Quản lý chi nhánh Thủ Đức', NOW(), 3, 2),
('NV008', 'Đặng Thị Hạnh', 'hanh.dang@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909890123', '258 Trần Hưng Đạo, Quận 1', 'FEMALE', '1999-04-12', '2024-05-01', 7500000.00, 'ACTIVE', 'Nhân viên bán hàng', NOW(), 3, 3),
('NV009', 'Bùi Quốc Huy', 'huy.bui@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909901234', '369 Lạc Long Quân, Tây Hồ, HN', 'MALE', '2001-01-20', '2024-06-01', 7000000.00, 'ACTIVE', 'Nhân viên bán hàng mới', NOW(), 1, 3),
('NV010', 'Lâm Thị Hương', 'huong.lam@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0909012345', '951 Nguyễn Trãi, Thanh Xuân, HN', 'FEMALE', '1994-06-08', '2024-07-01', 8000000.00, 'ACTIVE', 'Nhân viên bán hàng', NOW(), 2, 3);

-- =============================================================
-- 5. CUSTOMER
-- =============================================================
INSERT IGNORE INTO `Customer` (`customer_code`, `full_name`, `phone`, `email`, `gender`, `dob`, `address`, `total_point`, `used_point`, `total_revenue`, `status`, `note`, `created_at`, `customer_rank_id`, `created_by`, `updated_by`)
VALUES
('KH001', 'Trần Văn Minh', '0987654321', 'minh.tran@gmail.com', 'MALE', '1988-03-15', '123 Nguyễn Trãi, Quận 1, TP.HCM', 1500, 200, 8500000.00, 'ACTIVE', 'Khách VIP thường xuyên', NOW(), 3, 1, 1),
('KH002', 'Lê Thị Ngọc', '0978123456', 'ngoc.le@gmail.com', 'FEMALE', '1992-07-22', '456 Lê Văn Sỹ, Quận 3, TP.HCM', 800, 100, 3200000.00, 'ACTIVE', 'Khách quen mua online', NOW(), 2, 1, 1),
('KH003', 'Phạm Đức Anh', '0968234567', 'anh.pham@gmail.com', 'MALE', '1985-11-08', '789 Hoàng Văn Thụ, Tân Bình', 2500, 500, 18000000.00, 'ACTIVE', 'Khách hàng thân thiết lâu năm', NOW(), 4, 2, 1),
('KH004', 'Nguyễn Thị Hoa', '0958345678', 'hoa.nguyen@yahoo.com', 'FEMALE', '1990-05-30', '321 Lý Thường Kiệt, Quận 10', 300, 0, 1200000.00, 'ACTIVE', 'Khách mới', NOW(), 1, 2, 2),
('KH005', 'Văn Thị Bích', '0948456789', 'bich.van@gmail.com', 'FEMALE', '1998-09-12', '654 Bạch Đằng, Bình Thạnh', 200, 0, 800000.00, 'ACTIVE', 'Khách hàng tiềm năng', NOW(), 1, 2, 2),
('KH006', 'Đỗ Văn Phúc', '0938567890', 'phuc.do@outlook.com', 'MALE', '1982-02-28', '987 Hai Bà Trưng, Quận 1', 4500, 1000, 22000000.00, 'ACTIVE', 'Khách VIP Kim Cương', NOW(), 4, 1, 1),
('KH007', 'Trịnh Thị Mai', '0928678901', 'mai.trinh@gmail.com', 'FEMALE', '1996-12-05', '147 Cách Mạng Tháng 8, Quận 10', 0, 0, 0.00, 'ACTIVE', 'Khách mới chưa phát sinh GD', NOW(), 1, 2, 2),
('KH008', 'Lý Văn Hoàng', '0918789012', 'hoang.ly@gmail.com', 'MALE', '1993-04-18', '258 Trường Chinh, Tân Phú', 100, 50, 2400000.00, 'ACTIVE', 'Khách thường xuyên mua hàng', NOW(), 2, 1, 1),
('KH009', 'Dương Thị Lan', '0908890123', 'lan.duong@gmail.com', 'FEMALE', '1987-08-14', '369 Phan Đình Phùng, Quận 11', 600, 0, 2100000.00, 'ACTIVE', '', NOW(), 2, 3, 3),
('KH010', 'Tạ Quốc Bảo', '0998901234', 'bao.ta@yahoo.com', 'MALE', '1991-06-01', '159 Ngô Gia Tự, Quận 12', 1800, 300, 9500000.00, 'INACTIVE', 'Khách cũ, đã nghỉ', NOW(), 3, 3, 3);

-- =============================================================
-- 6. UNIT
-- =============================================================
INSERT IGNORE INTO `Unit` (`name`, `created_at`)
VALUES
('Lon', NOW()),
('Thùng', NOW()),
('Chai', NOW()),
('Gói', NOW()),
('Hộp', NOW()),
('Ký', NOW()),
('Cái', NOW());

-- =============================================================
-- 7. CATEGORY
-- =============================================================
INSERT IGNORE INTO `Category` (`name`, `description`, `status`, `created_at`)
VALUES
('Nước giải khát', 'Các loại nước ngọt, nước lọc, trà, cà phê đóng chai/lon', 'ACTIVE', NOW()),
('Thực phẩm ăn liền', 'Mì gói, cháo gói, xúc xích, đồ hộp ăn liền', 'ACTIVE', NOW()),
('Bánh kẹo', 'Các loại bánh, kẹo, snack', 'ACTIVE', NOW()),
('Sữa & Sản phẩm từ sữa', 'Sữa tươi, sữa chua, phô mai', 'ACTIVE', NOW()),
('Đồ dùng gia đình', 'Nước rửa chén, nước lau sàn, khăn giấy', 'ACTIVE', NOW()),
('Mỹ phẩm & Chăm sóc cá nhân', 'Sữa tắm, dầu gội, kem đánh răng', 'ACTIVE', NOW());

-- =============================================================
-- 8. BRAND
-- =============================================================
INSERT IGNORE INTO `Brand` (`name`, `status`, `created_at`)
VALUES
('Coca-Cola', 'ACTIVE', NOW()),
('PepsiCo', 'ACTIVE', NOW()),
('Hảo Hảo', 'ACTIVE', NOW()),
('Oishi', 'ACTIVE', NOW()),
('Vinamilk', 'ACTIVE', NOW()),
('Dutch Lady', 'ACTIVE', NOW()),
('Sunlight', 'ACTIVE', NOW()),
('PS', 'ACTIVE', NOW()),
('P/S', 'ACTIVE', NOW()),
('Lay\'s', 'ACTIVE', NOW());

-- =============================================================
-- 9. SUPPLIER
-- =============================================================
INSERT IGNORE INTO `Supplier` (`supplier_code`, `name`, `phone`, `email`, `address`, `status`, `note`, `created_at`)
VALUES
('NCC001', 'Công ty TNHH TM & DV An Phát', '02838123456', 'info@anphat.com', '123 Bến Thành, Quận 1, TP.HCM', 'ACTIVE', 'Nhà cung cấp chính', NOW()),
('NCC002', 'Công ty CP Thực phẩm Sao Việt', '02838234567', 'contact@saoviet.vn', '456 Nguyễn Trãi, Quận 5, TP.HCM', 'ACTIVE', 'Cung cấp thực phẩm ăn liền', NOW()),
('NCC003', 'Công ty TNHH Sữa Quốc tế', '02838345678', 'info@quoctemilk.com', '789 Lê Văn Sỹ, Quận 3, TP.HCM', 'ACTIVE', 'Cung cấp sữa và sản phẩm từ sữa', NOW()),
('NCC004', 'Công ty CP Hóa mỹ phẩm Á Châu', '02838456789', 'info@asiamypham.vn', '321 Hoàng Văn Thụ, Tân Bình', 'ACTIVE', 'Cung cấp mỹ phẩm và đồ gia dụng', NOW()),
('NCC005', 'Công ty TNHH Nước giải khát Đông Nam', '02838567890', 'info@dnsoutheast.com', '654 Phạm Văn Đồng, Thủ Đức', 'ACTIVE', 'Cung cấp nước giải khát', NOW());

-- =============================================================
-- 10. PRODUCT & PRODUCT_UNIT
-- =============================================================

-- Helper: Insert product if not exists and get its ID via LAST_INSERT_ID
-- We use INSERT ... SELECT WHERE NOT EXISTS pattern

-- --- 10a. Coca-Cola 320ml ---
SET @cat_nuoc = (SELECT id FROM `Category` WHERE `name` = 'Nước giải khát' LIMIT 1);
SET @brand_coca = (SELECT id FROM `Brand` WHERE `name` = 'Coca-Cola' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Coca-Cola 320ml', @cat_nuoc, @brand_coca, 'Nước ngọt Coca-Cola vị nguyên bản 320ml', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Coca-Cola 320ml');

SET @p_coca = (SELECT id FROM `Product` WHERE `name` = 'Coca-Cola 320ml' LIMIT 1);
SET @u_lon = (SELECT id FROM `Unit` WHERE `name` = 'Lon' LIMIT 1);
SET @u_thung = (SELECT id FROM `Unit` WHERE `name` = 'Thùng' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_coca, @u_lon, 1, 10000.00, '8934822011003', 1, 'COCA-LON', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_coca AND `sku` = 'COCA-LON');

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_coca, @u_thung, 24, 220000.00, '8934822011003-T24', 0, 'COCA-THUNG', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_coca AND `sku` = 'COCA-THUNG');

-- --- 10b. Pepsi 320ml ---
SET @brand_pepsi = (SELECT id FROM `Brand` WHERE `name` = 'PepsiCo' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Pepsi 320ml', @cat_nuoc, @brand_pepsi, 'Nước ngọt Pepsi vị Cola 320ml', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Pepsi 320ml');

SET @p_pepsi = (SELECT id FROM `Product` WHERE `name` = 'Pepsi 320ml' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_pepsi, @u_lon, 1, 10000.00, '8934862012003', 1, 'PEPSI-LON', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_pepsi AND `sku` = 'PEPSI-LON');

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_pepsi, @u_thung, 24, 220000.00, '8934862012003-T24', 0, 'PEPSI-THUNG', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_pepsi AND `sku` = 'PEPSI-THUNG');

-- --- 10c. Mì Hảo Hảo Tôm Chua Cay ---
SET @cat_tp = (SELECT id FROM `Category` WHERE `name` = 'Thực phẩm ăn liền' LIMIT 1);
SET @brand_haohao = (SELECT id FROM `Brand` WHERE `name` = 'Hảo Hảo' LIMIT 1);
SET @u_goi = (SELECT id FROM `Unit` WHERE `name` = 'Gói' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Mì Hảo Hảo Tôm Chua Cay', @cat_tp, @brand_haohao, 'Mì gói ăn liền Hảo Hảo vị Tôm Chua Cay', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay');

SET @p_haohao = (SELECT id FROM `Product` WHERE `name` = 'Mì Hảo Hảo Tôm Chua Cay' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_haohao, @u_goi, 1, 4500.00, '8934567890123', 1, 'HAOHAO-GOI', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_haohao AND `sku` = 'HAOHAO-GOI');

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_haohao, @u_thung, 30, 130000.00, '8934567890123-T30', 0, 'HAOHAO-THUNG', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_haohao AND `sku` = 'HAOHAO-THUNG');

-- --- 10d. Oishi Snack ---
SET @cat_banhkeo = (SELECT id FROM `Category` WHERE `name` = 'Bánh kẹo' LIMIT 1);
SET @brand_oishi = (SELECT id FROM `Brand` WHERE `name` = 'Oishi' LIMIT 1);
SET @u_hop = (SELECT id FROM `Unit` WHERE `name` = 'Hộp' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Snack Oishi Vị Gà', @cat_banhkeo, @brand_oishi, 'Snack khoai tây Oishi vị gà chiên', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Snack Oishi Vị Gà');

SET @p_oishi = (SELECT id FROM `Product` WHERE `name` = 'Snack Oishi Vị Gà' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_oishi, @u_goi, 1, 8000.00, '8934876013123', 1, 'OISHI-GOI', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_oishi AND `sku` = 'OISHI-GOI');

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_oishi, @u_hop, 20, 150000.00, '8934876013123-H20', 0, 'OISHI-HOP', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_oishi AND `sku` = 'OISHI-HOP');

-- --- 10e. Sữa Vinamilk 100% 1L ---
SET @cat_sua = (SELECT id FROM `Category` WHERE `name` = 'Sữa & Sản phẩm từ sữa' LIMIT 1);
SET @brand_vina = (SELECT id FROM `Brand` WHERE `name` = 'Vinamilk' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Sữa Vinamilk 100% 1L', @cat_sua, @brand_vina, 'Sữa tươi tiệt trùng Vinamilk 100% hộp 1 lít', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Sữa Vinamilk 100% 1L');

SET @p_vina = (SELECT id FROM `Product` WHERE `name` = 'Sữa Vinamilk 100% 1L' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_vina, @u_hop, 1, 32000.00, '8934563910123', 1, 'VINA-1L', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_vina AND `sku` = 'VINA-1L');

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_vina, @u_thung, 12, 360000.00, '8934563910123-T12', 0, 'VINA-THUNG', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_vina AND `sku` = 'VINA-THUNG');

-- --- 10f. Nước rửa chén Sunlight Chanh ---
SET @cat_dgd = (SELECT id FROM `Category` WHERE `name` = 'Đồ dùng gia đình' LIMIT 1);
SET @brand_sunlight = (SELECT id FROM `Brand` WHERE `name` = 'Sunlight' LIMIT 1);
SET @u_chai = (SELECT id FROM `Unit` WHERE `name` = 'Chai' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Nước rửa chén Sunlight Chanh', @cat_dgd, @brand_sunlight, 'Nước rửa chén Sunlight hương chanh 750ml', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Nước rửa chén Sunlight Chanh');

SET @p_sunlight = (SELECT id FROM `Product` WHERE `name` = 'Nước rửa chén Sunlight Chanh' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_sunlight, @u_chai, 1, 28000.00, '8934888888123', 1, 'SUNLIGHT-CHAI', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_sunlight AND `sku` = 'SUNLIGHT-CHAI');

-- --- 10g. Kem đánh răng P/S Trắng Sạch ---
SET @cat_mypham = (SELECT id FROM `Category` WHERE `name` = 'Mỹ phẩm & Chăm sóc cá nhân' LIMIT 1);
SET @brand_ps = (SELECT id FROM `Brand` WHERE `name` = 'P/S' LIMIT 1);
SET @u_cai = (SELECT id FROM `Unit` WHERE `name` = 'Cái' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Kem đánh răng P/S Trắng Sạch', @cat_mypham, @brand_ps, 'Kem đánh răng P/S Trắng Sạch 200g', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Kem đánh răng P/S Trắng Sạch');

SET @p_ps = (SELECT id FROM `Product` WHERE `name` = 'Kem đánh răng P/S Trắng Sạch' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_ps, @u_cai, 1, 25000.00, '8934888007123', 1, 'PS-VUA', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_ps AND `sku` = 'PS-VUA');

-- --- 10h. Snack Lay's Vị Tự Nhiên ---
SET @brand_lays = (SELECT id FROM `Brand` WHERE `name` = 'Lay\'s' LIMIT 1);

INSERT IGNORE INTO `Product` (`name`, `category_id`, `brand_id`, `description`, `status`, `created_at`)
SELECT 'Snack Lay\'s Vị Tự Nhiên', @cat_banhkeo, @brand_lays, 'Snack khoai tây Lay\'s vị muối tự nhiên', 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `Product` WHERE `name` = 'Snack Lay\'s Vị Tự Nhiên');

SET @p_lays = (SELECT id FROM `Product` WHERE `name` = 'Snack Lay\'s Vị Tự Nhiên' LIMIT 1);

INSERT IGNORE INTO `ProductUnit` (`product_id`, `unit_id`, `conversion_value`, `price`, `barcode_unit`, `is_base_unit`, `sku`, `created_at`)
SELECT @p_lays, @u_goi, 1, 12000.00, '8934888222123', 1, 'LAYS-GOI', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `ProductUnit` WHERE `product_id` = @p_lays AND `sku` = 'LAYS-GOI');

-- =============================================================
-- 11. INVENTORY (stock for each branch x product_unit)
-- =============================================================
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 100, 20, 500, 'Kệ A01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'COCA-LON';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 15, 3, 50, 'Kệ A02', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'COCA-THUNG';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 100, 20, 500, 'Kệ A01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'PEPSI-LON';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 200, 50, 1000, 'Kệ B01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 10, 2, 30, 'Kệ B02', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-THUNG';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 50, 10, 200, 'Kệ C01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'OISHI-GOI';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 30, 5, 100, 'Kệ D01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'VINA-1L';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 5, 1, 20, 'Kệ D02', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'VINA-THUNG';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 40, 10, 150, 'Kệ E01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'SUNLIGHT-CHAI';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 60, 10, 200, 'Kệ F01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'PS-VUA';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 1, pu.id, 80, 15, 300, 'Kệ C02', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'LAYS-GOI';

-- Branch 2
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 2, pu.id, 80, 20, 500, 'Kệ A01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'COCA-LON';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 2, pu.id, 10, 2, 30, 'Kệ A02', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'COCA-THUNG';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 2, pu.id, 150, 50, 1000, 'Kệ B01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 2, pu.id, 30, 5, 100, 'Kệ D01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'VINA-1L';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 2, pu.id, 20, 5, 80, 'Kệ E01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'SUNLIGHT-CHAI';

-- Branch 3
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 3, pu.id, 60, 20, 400, 'Kệ A01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'COCA-LON';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 3, pu.id, 100, 30, 800, 'Kệ B01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';
INSERT IGNORE INTO `Inventory` (`branch_id`, `product_unit_id`, `stock`, `min_stock`, `max_stock`, `position_in_shop`, `created_at`)
SELECT 3, pu.id, 40, 10, 150, 'Kệ C01', NOW() FROM `ProductUnit` pu WHERE pu.sku = 'OISHI-GOI';

-- =============================================================
-- 12. VOUCHER
-- =============================================================
INSERT IGNORE INTO `Voucher` (`code`, `name`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `start_at`, `end_at`, `status`, `customer_rank_id`, `created_at`)
SELECT 'GIAM10K', 'Giảm 10.000đ cho đơn từ 100k', 'AMOUNT', 10000.00, 100000.00, 50000.00,
       '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', cr.id, NOW()
FROM `CustomerRank` cr WHERE cr.name = 'Đồng';

INSERT IGNORE INTO `Voucher` (`code`, `name`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `start_at`, `end_at`, `status`, `customer_rank_id`, `created_at`)
SELECT 'GIAM5PT', 'Giảm 5% cho đơn từ 200k', 'PERCENT', 5.00, 200000.00, 50000.00,
       '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', cr.id, NOW()
FROM `CustomerRank` cr WHERE cr.name = 'Bạc';

INSERT IGNORE INTO `Voucher` (`code`, `name`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `start_at`, `end_at`, `status`, `customer_rank_id`, `created_at`)
SELECT 'GIAM10PT', 'Giảm 10% cho đơn từ 500k', 'PERCENT', 10.00, 500000.00, 100000.00,
       '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', cr.id, NOW()
FROM `CustomerRank` cr WHERE cr.name = 'Vàng';

INSERT IGNORE INTO `Voucher` (`code`, `name`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `start_at`, `end_at`, `status`, `customer_rank_id`, `created_at`)
SELECT 'GIAM15PT', 'Giảm 15% cho đơn từ 1 triệu - Kim Cương', 'PERCENT', 15.00, 1000000.00, 200000.00,
       '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', cr.id, NOW()
FROM `CustomerRank` cr WHERE cr.name = 'Kim Cương';

INSERT IGNORE INTO `Voucher` (`code`, `name`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `start_at`, `end_at`, `status`, `customer_rank_id`, `created_at`)
SELECT 'SUMMER50K', 'Giảm 50.000đ mùa hè', 'AMOUNT', 50000.00, 300000.00, 50000.00,
       '2026-06-01 00:00:00', '2026-08-31 23:59:59', 'ACTIVE', cr.id, NOW()
FROM `CustomerRank` cr WHERE cr.name = 'Vàng';

-- =============================================================
-- 13. ORDERTRANSACTION & DETAILS
-- =============================================================

-- Order 1 - KH001, branch 1, NV003
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `customer_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `points_used`, `point_discount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 1001, 1, c.id, 3, 'HD-20260701-001', 50000.00, 5000.00, 0, 0.00, 45000.00, 50000.00, 5000.00, 'COMPLETED', 'SALE', 'Thanh toán tiền mặt', '2026-07-01 09:30:00'
FROM `Customer` c WHERE c.customer_code = 'KH001'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'HD-20260701-001');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1001, pu.id, 3, 10000.00, 0.00, 30000.00, '2026-07-01 09:30:00'
FROM `ProductUnit` pu WHERE pu.sku = 'COCA-LON';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1001, pu.id, 2, 10000.00, 0.00, 20000.00, '2026-07-01 09:30:00'
FROM `ProductUnit` pu WHERE pu.sku = 'PEPSI-LON';

-- Order 2 - KH003 (VIP), branch 1, NV003
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `customer_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `points_used`, `point_discount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 1002, 1, c.id, 3, 'HD-20260701-002', 375000.00, 37500.00, 100, 20000.00, 317500.00, 317500.00, 0.00, 'COMPLETED', 'SALE', 'Chuyển khoản', '2026-07-01 14:15:00'
FROM `Customer` c WHERE c.customer_code = 'KH003'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'HD-20260701-002');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1002, pu.id, 1, 220000.00, 22000.00, 198000.00, '2026-07-01 14:15:00'
FROM `ProductUnit` pu WHERE pu.sku = 'COCA-THUNG';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1002, pu.id, 30, 4500.00, 13500.00, 121500.00, '2026-07-01 14:15:00'
FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1002, pu.id, 2, 28000.00, 2000.00, 54000.00, '2026-07-01 14:15:00'
FROM `ProductUnit` pu WHERE pu.sku = 'SUNLIGHT-CHAI';

-- Order 3 - KH006 (VIP), branch 2, NV004
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `customer_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `points_used`, `point_discount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 1003, 2, c.id, 4, 'HD-20260702-001', 1020000.00, 153000.00, 500, 100000.00, 767000.00, 770000.00, 3000.00, 'COMPLETED', 'SALE', 'Tiền mặt', '2026-07-02 10:00:00'
FROM `Customer` c WHERE c.customer_code = 'KH006'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'HD-20260702-001');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1003, pu.id, 10, 32000.00, 32000.00, 288000.00, '2026-07-02 10:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'VINA-1L';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1003, pu.id, 3, 220000.00, 66000.00, 594000.00, '2026-07-02 10:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'COCA-THUNG';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1003, pu.id, 2, 8000.00, 0.00, 16000.00, '2026-07-02 10:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'OISHI-GOI';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1003, pu.id, 5, 25000.00, 5000.00, 120000.00, '2026-07-02 10:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'PS-VUA';

-- Order 4 - no customer, branch 1, NV003
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
VALUES
(1004, 1, 3, 'HD-20260702-002', 45000.00, 0.00, 45000.00, 50000.00, 5000.00, 'COMPLETED', 'SALE', 'Tiền mặt', '2026-07-02 15:30:00');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1004, pu.id, 10, 4500.00, 0.00, 45000.00, '2026-07-02 15:30:00'
FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';

-- Order 5 - KH002, branch 1, NV009, with voucher
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `customer_id`, `voucher_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 1005, 1, c.id, v.id, 9, 'HD-20260703-001', 120000.00, 6000.00, 114000.00, 114000.00, 0.00, 'COMPLETED', 'SALE', 'Chuyển khoản', '2026-07-03 11:00:00'
FROM `Customer` c, `Voucher` v
WHERE c.customer_code = 'KH002' AND v.code = 'GIAM5PT'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'HD-20260703-001');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1005, pu.id, 5, 10000.00, 0.00, 50000.00, '2026-07-03 11:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'COCA-LON';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1005, pu.id, 10, 4500.00, 0.00, 45000.00, '2026-07-03 11:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-GOI';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 1005, pu.id, 2, 12000.00, 0.00, 24000.00, '2026-07-03 11:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'LAYS-GOI';

-- =============================================================
-- 14. IMPORT transactions (purchase orders)
-- =============================================================
INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `supplier_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 2001, 1, s.id, 5, 'NK-20260701-001', 4400000.00, 0.00, 4400000.00, 4400000.00, 0.00, 'COMPLETED', 'IMPORT', 'Nhập hàng Coca-Cola', '2026-07-01 08:00:00'
FROM `Supplier` s WHERE s.supplier_code = 'NCC001'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'NK-20260701-001');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `import_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 2001, pu.id, 20, 220000.00, 180000.00, 0.00, 3600000.00, '2026-07-01 08:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'COCA-THUNG';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `import_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 2001, pu.id, 20, 10000.00, 8000.00, 0.00, 160000.00, '2026-07-01 08:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'PEPSI-LON';

INSERT IGNORE INTO `ordertransaction` (`id`, `branch_id`, `supplier_id`, `created_by`, `code`, `total_amount`, `discount_amount`, `final_amount`, `paid_amount`, `change_amount`, `status`, `transaction_type`, `note`, `created_at`)
SELECT 2002, 1, s.id, 5, 'NK-20260702-001', 5200000.00, 100000.00, 5100000.00, 5100000.00, 0.00, 'COMPLETED', 'IMPORT', 'Nhập hàng Hảo Hảo', '2026-07-02 08:00:00'
FROM `Supplier` s WHERE s.supplier_code = 'NCC002'
AND NOT EXISTS (SELECT 1 FROM `ordertransaction` WHERE `code` = 'NK-20260702-001');

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `import_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 2002, pu.id, 40, 130000.00, 110000.00, 0.00, 4400000.00, '2026-07-02 08:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'HAOHAO-THUNG';

INSERT IGNORE INTO `ordertransactiondetail` (`order_transaction_id`, `product_unit_id`, `quantity`, `sale_price`, `import_price`, `discount_amount`, `total_amount`, `created_at`)
SELECT 2002, pu.id, 100, 8000.00, 6500.00, 0.00, 650000.00, '2026-07-02 08:00:00'
FROM `ProductUnit` pu WHERE pu.sku = 'OISHI-GOI';

-- =============================================================
-- 15. RETURN_REQUEST (1 sample return with items)
-- =============================================================
INSERT IGNORE INTO `return_request` (`id`, `order_id`, `branch_id`, `requested_by`, `reason`, `status`, `reviewed_by`, `reviewed_at`, `created_at`, `updated_at`)
SELECT 1, ot.id, 1, 3, 'Sản phẩm bị lỗi nắp lon', 'APPROVED', 1, '2026-07-03 16:00:00', '2026-07-03 15:30:00', '2026-07-03 16:00:00'
FROM `ordertransaction` ot WHERE ot.code = 'HD-20260701-001'
AND NOT EXISTS (SELECT 1 FROM `return_request` WHERE `id` = 1);

-- =============================================================
-- 16. CASHBOOK_TRANSACTION (sample entries)
-- =============================================================
INSERT IGNORE INTO `cashbook_transaction` (`branch_id`, `transaction_type`, `payment_method`, `amount`, `reference_code`, `description`, `created_by`, `created_at`)
SELECT 1, 'IN', 'CASH', 50000.00, 'HD-20260701-001', 'Thu tiền bán hàng HD-20260701-001', e.id, '2026-07-01 09:30:00'
FROM `Employee` e WHERE e.employee_code = 'NV003';

INSERT IGNORE INTO `cashbook_transaction` (`branch_id`, `transaction_type`, `payment_method`, `amount`, `reference_code`, `description`, `created_by`, `created_at`)
SELECT 1, 'IN', 'BANK', 317500.00, 'HD-20260701-002', 'Thu chuyển khoản HD-20260701-002', e.id, '2026-07-01 14:15:00'
FROM `Employee` e WHERE e.employee_code = 'NV003';

INSERT IGNORE INTO `cashbook_transaction` (`branch_id`, `transaction_type`, `payment_method`, `amount`, `reference_code`, `description`, `created_by`, `created_at`)
SELECT 2, 'IN', 'CASH', 770000.00, 'HD-20260702-001', 'Thu tiền mặt HD-20260702-001', e.id, '2026-07-02 10:00:00'
FROM `Employee` e WHERE e.employee_code = 'NV004';

INSERT IGNORE INTO `cashbook_transaction` (`branch_id`, `transaction_type`, `payment_method`, `amount`, `reference_code`, `description`, `created_by`, `created_at`)
SELECT 1, 'OUT', 'BANK', 4400000.00, 'NK-20260701-001', 'Chi nhập hàng NCC An Phát', e.id, '2026-07-01 08:00:00'
FROM `Employee` e WHERE e.employee_code = 'NV005';

INSERT IGNORE INTO `cashbook_transaction` (`branch_id`, `transaction_type`, `payment_method`, `amount`, `reference_code`, `description`, `created_by`, `created_at`)
SELECT 1, 'OUT', 'BANK', 5100000.00, 'NK-20260702-001', 'Chi nhập hàng NCC Sao Việt', e.id, '2026-07-02 08:00:00'
FROM `Employee` e WHERE e.employee_code = 'NV005';

-- =============================================================
-- DONE
-- =============================================================
