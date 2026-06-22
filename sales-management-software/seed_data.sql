-- ==========================================
-- FILE: seed_data.sql
-- MỤC ĐÍCH: Khởi tạo dữ liệu mẫu cho hệ thống Quản lý Bán hàng (SMS)
-- HƯỚNG DẪN: Hãy chạy script này trong cơ sở dữ liệu MySQL 'sms_db'.
-- ==========================================

-- 1. Dữ liệu bảng Role (Vai trò nhân viên)
INSERT IGNORE INTO Role (id, code, name, description) VALUES
(1, 'OWNER', 'Chủ cửa hàng', 'Toàn quyền quản trị hệ thống'),
(2, 'BRANCH_MANAGER', 'Quản lý chi nhánh', 'Quản lý hoạt động tại chi nhánh cụ thể'),
(3, 'SALE_STAFF', 'Nhân viên bán hàng', 'Lập hóa đơn và phục vụ khách hàng tại POS'),
(4, 'WAREHOUSE_STAFF', 'Nhân viên kho', 'Quản lý sản phẩm, đơn vị và tồn kho');

-- 2. Dữ liệu bảng Branch (Chi nhánh)
INSERT IGNORE INTO Branch (id, branch_code, name, phone, email, address, status, opened_at, created_at) VALUES
(1, 'BR-HN-01', 'Hà Nội - Trụ sở chính', '0241234567', 'hanoi@sms.com', '123 Đường Cầu Giấy, Quận Cầu Giấy, Hà Nội', 'ACTIVE', '2026-01-01', NOW()),
(2, 'BR-HCM-01', 'TP. Hồ Chí Minh - Chi nhánh 1', '0281234567', 'hcm@sms.com', '456 Đường Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', 'ACTIVE', '2026-02-01', NOW());

-- 3. Dữ liệu bảng Employee (Nhân viên)
-- Mật khẩu mặc định cho tất cả tài khoản là: 123456 (đã được băm bằng BCrypt)
-- Hash: $2a$10$ByIxzO7tV.Sg0UjK4jBqQu.ZgXg2.1y5F98iCg83u.w3i5585K.S2
INSERT IGNORE INTO Employee (id, branch_id, role_id, employee_code, full_name, email, password_hash, phone, address, gender, hired_date, base_salary, work_status, created_at) VALUES
-- Chủ cửa hàng (Owner)
(1, 1, 1, 'EMP-OWNER-01', 'Nguyễn Chủ Tiệm', 'owner@sms.com', '$2a$10$ByIxzO7tV.Sg0UjK4jBqQu.ZgXg2.1y5F98iCg83u.w3i5585K.S2', '0901111111', 'Hà Nội', 'MALE', '2026-01-01', 50000000.00, 'ACTIVE', NOW()),
-- Quản lý chi nhánh Hà Nội (Manager)
(2, 1, 2, 'EMP-MGR-HN', 'Trần Quản Lý HN', 'manager.hn@sms.com', '$2a$10$ByIxzO7tV.Sg0UjK4jBqQu.ZgXg2.1y5F98iCg83u.w3i5585K.S2', '0902222222', 'Hà Nội', 'FEMALE', '2026-01-05', 25000000.00, 'ACTIVE', NOW()),
-- Nhân viên bán hàng Hà Nội (Sale Staff)
(3, 1, 3, 'EMP-SALE-HN', 'Lê Bán Hàng HN', 'sale.hn@sms.com', '$2a$10$ByIxzO7tV.Sg0UjK4jBqQu.ZgXg2.1y5F98iCg83u.w3i5585K.S2', '0903333333', 'Hà Nội', 'MALE', '2026-01-10', 10000000.00, 'ACTIVE', NOW()),
-- Nhân viên kho Hà Nội (Warehouse Staff)
(4, 1, 4, 'EMP-WH-HN', 'Phạm Thủ Kho HN', 'warehouse.hn@sms.com', '$2a$10$ByIxzO7tV.Sg0UjK4jBqQu.ZgXg2.1y5F98iCg83u.w3i5585K.S2', '0904444444', 'Hà Nội', 'FEMALE', '2026-01-10', 12000000.00, 'ACTIVE', NOW());

-- 4. Dữ liệu bảng CustomerRank (Hạng thành viên)
INSERT IGNORE INTO CustomerRank (id, name, discount_rate, condition_total_revenue, description, created_at) VALUES
(1, 'Đồng', 0.00, 0.00, 'Hạng mới đăng ký', NOW()),
(2, 'Bạc', 2.00, 5000000.00, 'Tích lũy từ 5 triệu VND (Giảm 2% hóa đơn)', NOW()),
(3, 'Vàng', 5.00, 15000000.00, 'Tích lũy từ 15 triệu VND (Giảm 5% hóa đơn)', NOW()),
(4, 'Kim cương', 10.00, 50000000.00, 'Tích lũy từ 50 triệu VND (Giảm 10% hóa đơn)', NOW());

-- 5. Dữ liệu bảng Customer (Khách hàng)
INSERT IGNORE INTO Customer (id, customer_rank_id, customer_code, full_name, phone, email, gender, dob, address, total_point, used_point, total_revenue, status, created_at) VALUES
(1, 1, 'CUS-0001', 'Nguyễn Văn Khách', '0988888888', 'khachvan@gmail.com', 'MALE', '1995-05-15', 'Cầu Giấy, Hà Nội', 10, 0, 1000000.00, 'ACTIVE', NOW()),
(2, 2, 'CUS-0002', 'Trần Thị Mua', '0977777777', 'muathi@gmail.com', 'FEMALE', '1992-10-20', 'Quận 1, TP. HCM', 150, 0, 6500000.00, 'ACTIVE', NOW()),
(3, 3, 'CUS-0003', 'Phạm Khách Vàng', '0966666666', 'khachvang@gmail.com', 'MALE', '1988-03-30', 'Hai Bà Trưng, Hà Nội', 400, 100, 18000000.00, 'ACTIVE', NOW());

-- 6. Dữ liệu bảng Category (Danh mục sản phẩm)
INSERT IGNORE INTO Category (id, name, description, status, created_at) VALUES
(1, 'Gia vị & Nước sốt', 'Nước mắm, nước tương, dầu ăn, hạt nêm, tương ớt', 'ACTIVE', NOW()),
(2, 'Mì, Cháo, Phở', 'Các loại mì gói, cháo ăn liền, phở khô', 'ACTIVE', NOW()),
(3, 'Sữa & Sản phẩm sữa', 'Sữa tươi, sữa chua, bơ, phô mai', 'ACTIVE', NOW());

-- 7. Dữ liệu bảng Brand (Thương hiệu)
INSERT IGNORE INTO Brand (id, name, status, created_at) VALUES
(1, 'Masan', 'ACTIVE', '2026-06-22 12:00:00'),
(2, 'Acecook', 'ACTIVE', '2026-06-22 12:00:00'),
(3, 'Vinamilk', 'ACTIVE', '2026-06-22 12:00:00');

-- 8. Dữ liệu bảng Unit (Đơn vị tính)
INSERT IGNORE INTO Unit (id, name, created_at) VALUES
(1, 'Chai', NOW()),
(2, 'Gói', NOW()),
(3, 'Hộp', NOW());

-- 9. Dữ liệu bảng Product (Sản phẩm)
INSERT IGNORE INTO Product (id, category_id, brand_id, name, image_url, description, status, created_at) VALUES
(1, 1, 1, 'Nước tương Chinsu tỏi ớt 250ml', 'https://images.unsplash.com/photo-1589135306090-e8508358b77c?w=300', 'Nước tương tỏi ớt đậm đà thơm ngon', 'ACTIVE', NOW()),
(2, 2, 2, 'Mì Hảo Hảo Tôm chua cay 75g', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=300', 'Mì ăn liền hương vị tôm chua cay truyền thống', 'ACTIVE', NOW()),
(3, 3, 3, 'Sữa tươi tiệt trùng Vinamilk ít đường 180ml', 'https://images.unsplash.com/photo-1550583724-b2692b85b150?w=300', 'Sữa tươi chất lượng cao cung cấp năng lượng', 'ACTIVE', NOW());

-- 10. Dữ liệu bảng ProductUnit (Đơn vị quy đổi & Giá bán của sản phẩm)
INSERT IGNORE INTO ProductUnit (id, product_id, unit_id, conversion_value, price, barcode_unit, is_base_unit, sku, created_at) VALUES
-- Nước tương Chinsu tỏi ớt 250ml
(1, 1, 1, 1, 16000.00, '8934588012112', 1, 'CS-TOIOT-250', NOW()),
-- Mì Hảo Hảo Tôm chua cay 75g
(2, 2, 2, 1, 4500.00, '8934563138072', 1, 'HH-TOMCHUACAY', NOW()),
-- Sữa tươi tiệt trùng Vinamilk ít đường 180ml
(3, 3, 3, 1, 7500.00, '8934673121094', 1, 'VNM-ITDUONG-180', NOW());

-- 11. Dữ liệu bảng Inventory (Tồn kho sản phẩm tại từng chi nhánh)
INSERT IGNORE INTO Inventory (id, branch_id, product_unit_id, stock, min_stock, max_stock, position_in_shop) VALUES
-- Chi nhánh Hà Nội
(1, 1, 1, 150, 10, 500, 'Kệ gia vị A1'),
(2, 1, 2, 500, 20, 1000, 'Kệ mì gói B2'),
(3, 1, 3, 200, 15, 800, 'Tủ mát sữa C1'),
-- Chi nhánh Hồ Chí Minh
(4, 2, 1, 120, 10, 500, 'Kệ gia vị 1'),
(5, 2, 2, 400, 20, 1000, 'Kệ mì gói 2'),
(6, 2, 3, 150, 15, 800, 'Tủ mát 1');

-- 12. Dữ liệu bảng Voucher (Mã giảm giá/Khuyến mãi)
INSERT IGNORE INTO Voucher (id, code, name, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, status, created_at) VALUES
('1', 'PERCENT10', 'Giảm 10% đơn hàng thực phẩm', 'PERCENT', 10.00, 50000.00, 30000.00, '2026-06-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', NOW()),
('2', 'FLAT15K', 'Giảm ngay 15k cho hóa đơn', 'AMOUNT', 15000.00, 100000.00, 150000.00, '2026-06-01 00:00:00', '2026-12-31 23:59:59', 'ACTIVE', NOW()),
('3', 'EXPIRED50', 'Mã giảm giá đã hết hạn', 'PERCENT', 50.00, 200000.00, 50000.00, '2026-01-01 00:00:00', '2026-06-01 00:00:00', 'ACTIVE', NOW());
