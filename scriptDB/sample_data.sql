USE SMS_DB;

-- =============================================
-- 1. ROLE
-- =============================================
INSERT INTO Role (code, name, description) VALUES
('ROLE_ADMIN', 'Quản trị viên', 'Toàn quyền hệ thống'),
('ROLE_MANAGER', 'Quản lý chi nhánh', 'Quản lý chi nhánh được phân công'),
('ROLE_STAFF', 'Nhân viên bán hàng', 'Thực hiện bán hàng và thu ngân'),
('ROLE_WAREHOUSE', 'Thủ kho', 'Quản lý kho và nhập hàng');

-- =============================================
-- 2. BRANCH (tạm thời chưa có manager_id)
-- =============================================
INSERT INTO Branch (name, address, status, manager_id, created_at) VALUES
('Chi nhánh Hà Nội - Hoàn Kiếm', '12 Lý Thái Tổ, Hoàn Kiếm, Hà Nội', 'ACTIVE', NULL, '2023-01-01 08:00:00'),
('Chi nhánh Hà Nội - Cầu Giấy', '45 Trần Thái Tông, Cầu Giấy, Hà Nội', 'ACTIVE', NULL, '2023-03-15 08:00:00'),
('Chi nhánh TP.HCM - Quận 1', '88 Nguyễn Huệ, Quận 1, TP.HCM', 'ACTIVE', NULL, '2023-06-01 08:00:00');

-- =============================================
-- 3. EMPLOYEE (Boss là người đầu tiên, id=1)
-- Password 123456 -> BCrypt hash
-- =============================================
INSERT INTO Employee (branch_id, role_id, manager_id, created_by, employee_code, fullname, address, gender, dob, hired_date, base_salary, work_status, note, email, password, create_at, updated_at) VALUES
-- Boss (admin, không có manager, tự tạo -> manager_id=NULL, created_by sẽ update sau)
(1, 1, NULL, NULL, 'EMP-001', 'Boss', '12 Lý Thái Tổ, Hoàn Kiếm, Hà Nội', 'MALE', '1985-05-15', '2023-01-01', 50000000.00, 'ACTIVE', 'Tài khoản quản trị tổng', 'khanhtit16@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-01-01 08:00:00', NULL),
-- Manager chi nhánh Hà Nội - Hoàn Kiếm
(1, 2, 1, 1, 'EMP-002', 'Nguyễn Văn Minh', '23 Hàng Bài, Hoàn Kiếm, Hà Nội', 'MALE', '1990-08-20', '2023-01-05', 20000000.00, 'ACTIVE', NULL, 'nguyenvanminh@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-01-05 08:00:00', NULL),
-- Manager chi nhánh Cầu Giấy
(2, 2, 1, 1, 'EMP-003', 'Trần Thị Hoa', '67 Xuân Thủy, Cầu Giấy, Hà Nội', 'FEMALE', '1992-03-12', '2023-03-15', 20000000.00, 'ACTIVE', NULL, 'tranthihoa@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-03-15 08:00:00', NULL),
-- Manager chi nhánh HCM
(3, 2, 1, 1, 'EMP-004', 'Lê Hoàng Nam', '99 Lê Lợi, Quận 1, TP.HCM', 'MALE', '1988-11-30', '2023-06-01', 22000000.00, 'ACTIVE', NULL, 'lehoangnam@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-06-01 08:00:00', NULL),
-- Nhân viên bán hàng CN1
(1, 3, 2, 1, 'EMP-005', 'Phạm Thị Lan', '5 Đinh Tiên Hoàng, Hoàn Kiếm, Hà Nội', 'FEMALE', '1998-07-22', '2023-02-01', 8000000.00, 'ACTIVE', NULL, 'phamthilan@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-02-01 08:00:00', NULL),
-- Nhân viên bán hàng CN1
(1, 3, 2, 1, 'EMP-006', 'Đỗ Văn Tú', '18 Tràng Thi, Hoàn Kiếm, Hà Nội', 'MALE', '1999-01-10', '2023-02-01', 8000000.00, 'ACTIVE', NULL, 'dovantu@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-02-01 08:00:00', NULL),
-- Thủ kho CN2
(2, 4, 3, 1, 'EMP-007', 'Vũ Thị Mai', '30 Duy Tân, Cầu Giấy, Hà Nội', 'FEMALE', '1995-09-05', '2023-04-01', 9000000.00, 'ACTIVE', NULL, 'vuthimai@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-04-01 08:00:00', NULL),
-- Nhân viên bán hàng CN3
(3, 3, 4, 1, 'EMP-008', 'Hoàng Văn Bình', '22 Pasteur, Quận 1, TP.HCM', 'MALE', '1997-04-18', '2023-07-01', 9000000.00, 'ACTIVE', NULL, 'hoangvanbinh@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lkii', '2023-07-01 08:00:00', NULL);

-- Cập nhật created_by cho boss (tự tạo)
UPDATE Employee SET created_by = 1 WHERE id = 1;

-- Cập nhật manager_id cho Branch
UPDATE Branch SET manager_id = 2 WHERE id = 1;
UPDATE Branch SET manager_id = 3 WHERE id = 2;
UPDATE Branch SET manager_id = 4 WHERE id = 3;

-- =============================================
-- 4. BRAND
-- =============================================
INSERT INTO Brand (name, created_at) VALUES
('Vinamilk', '2023-01-01 08:00:00'),
('TH True Milk', '2023-01-01 08:00:00'),
('Coca-Cola', '2023-01-01 08:00:00'),
('Pepsi', '2023-01-01 08:00:00'),
('Unilever', '2023-01-01 08:00:00'),
('P&G', '2023-01-01 08:00:00'),
('Masan', '2023-01-01 08:00:00'),
('Acecook', '2023-01-01 08:00:00');

-- =============================================
-- 5. CATEGORY
-- =============================================
INSERT INTO Category (name, description, status, created_at, updated_at) VALUES
('Sữa & Sản phẩm từ sữa', 'Sữa tươi, sữa hộp, sữa chua, phô mai...', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Đồ uống có ga', 'Nước ngọt, nước có ga các loại', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Mì & Bún ăn liền', 'Mì gói, bún, phở ăn liền', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Gia vị & Nước chấm', 'Nước mắm, tương ớt, dầu ăn, muối, đường', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Chăm sóc cá nhân', 'Dầu gội, sữa tắm, kem đánh răng', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Bánh kẹo & Snack', 'Bánh quy, kẹo, snack, chocolate', 'ACTIVE', '2023-01-01 08:00:00', NULL),
('Nước uống đóng chai', 'Nước khoáng, nước tinh khiết', 'ACTIVE', '2023-01-01 08:00:00', NULL);

-- =============================================
-- 6. PRODUCT
-- =============================================
INSERT INTO Product (category_id, brand_id, name, description, status, note, created_at, updated_at) VALUES
(1, 1, 'Sữa tươi tiệt trùng Vinamilk có đường', 'Sữa tươi tiệt trùng 100% từ bò tươi', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(1, 2, 'Sữa tươi TH True Milk không đường', 'Sữa tươi sạch organic từ trang trại TH', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(2, 3, 'Coca-Cola lon', 'Nước giải khát có ga Coca-Cola', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(2, 4, 'Pepsi lon', 'Nước giải khát có ga Pepsi', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(3, 8, 'Mì Hảo Hảo tôm chua cay', 'Mì ăn liền Hảo Hảo vị tôm chua cay', 'ACTIVE', 'Bestseller', '2023-01-10 08:00:00', NULL),
(3, 8, 'Mì Lẩu Thái Acecook', 'Mì ăn liền vị lẩu Thái', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(4, 7, 'Nước mắm Nam Ngư', 'Nước mắm truyền thống Nam Ngư', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(4, 7, 'Tương ớt Chinsu', 'Tương ớt Chinsu đỏ cay', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(5, 5, 'Dầu gội Dove dưỡng ẩm', 'Dầu gội Dove dưỡng ẩm tóc mềm mượt', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(5, 6, 'Sữa tắm Olay', 'Sữa tắm dưỡng da Olay', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(6, 7, 'Bánh gạo lứt Gạo Lức', 'Bánh ăn vặt từ gạo lứt tốt cho sức khỏe', 'ACTIVE', '', '2023-01-10 08:00:00', NULL),
(7, 2, 'Nước khoáng TH True Water', 'Nước khoáng thiên nhiên tinh khiết', 'ACTIVE', '', '2023-01-10 08:00:00', NULL);

-- =============================================
-- 7. PRODUCT IMAGE
-- =============================================
INSERT INTO ProductImage (product_id, image_url, is_thumbnail, created_at) VALUES
(1, 'https://cdn.sms.vn/products/vinamilk-co-duong-thumb.jpg', true, '2023-01-10 08:00:00'),
(1, 'https://cdn.sms.vn/products/vinamilk-co-duong-2.jpg', false, '2023-01-10 08:00:00'),
(2, 'https://cdn.sms.vn/products/th-truemilk-thumb.jpg', true, '2023-01-10 08:00:00'),
(3, 'https://cdn.sms.vn/products/coca-cola-thumb.jpg', true, '2023-01-10 08:00:00'),
(4, 'https://cdn.sms.vn/products/pepsi-thumb.jpg', true, '2023-01-10 08:00:00'),
(5, 'https://cdn.sms.vn/products/hao-hao-thumb.jpg', true, '2023-01-10 08:00:00'),
(5, 'https://cdn.sms.vn/products/hao-hao-2.jpg', false, '2023-01-10 08:00:00'),
(6, 'https://cdn.sms.vn/products/lau-thai-thumb.jpg', true, '2023-01-10 08:00:00'),
(7, 'https://cdn.sms.vn/products/nam-ngu-thumb.jpg', true, '2023-01-10 08:00:00'),
(8, 'https://cdn.sms.vn/products/chinsu-thumb.jpg', true, '2023-01-10 08:00:00'),
(9, 'https://cdn.sms.vn/products/dove-thumb.jpg', true, '2023-01-10 08:00:00'),
(10, 'https://cdn.sms.vn/products/olay-thumb.jpg', true, '2023-01-10 08:00:00'),
(11, 'https://cdn.sms.vn/products/gao-luc-thumb.jpg', true, '2023-01-10 08:00:00'),
(12, 'https://cdn.sms.vn/products/th-water-thumb.jpg', true, '2023-01-10 08:00:00');

-- =============================================
-- 8. UNIT
-- =============================================
INSERT INTO Unit (name, created_at) VALUES
('Cái', '2023-01-01 08:00:00'),
('Hộp', '2023-01-01 08:00:00'),
('Thùng', '2023-01-01 08:00:00'),
('Lốc', '2023-01-01 08:00:00'),
('Gói', '2023-01-01 08:00:00'),
('Chai', '2023-01-01 08:00:00'),
('Lon', '2023-01-01 08:00:00');

-- =============================================
-- 9. PRODUCT UNIT
-- =============================================
INSERT INTO ProductUnit (product_id, unit_id, convention_value, price, barcode_unit, is_base_unit, sku) VALUES
-- Sữa Vinamilk: đơn vị cơ sở Hộp 200ml, Lốc 4 hộp, Thùng 48 hộp
(1, 2, 1,    7500.00,  '8934673123001', true,  'VNM-SUATD-HOP'),
(1, 4, 4,   30000.00,  '8934673123002', false, 'VNM-SUATD-LOC'),
(1, 3, 48, 340000.00,  '8934673123003', false, 'VNM-SUATD-THUNG'),
-- Sữa TH: Hộp 500ml, Thùng 12 hộp
(2, 2, 1,   18000.00,  '8934673456001', true,  'TH-SUATD-HOP'),
(2, 3, 12, 210000.00,  '8934673456002', false, 'TH-SUATD-THUNG'),
-- Coca-Cola: Lon đơn, Lốc 6 lon, Thùng 24 lon
(3, 7, 1,    9000.00,  '5000112600003', true,  'CC-LON'),
(3, 4, 6,   52000.00,  '5000112600004', false, 'CC-LOC6'),
(3, 3, 24, 200000.00,  '5000112600005', false, 'CC-THUNG24'),
-- Pepsi: Lon đơn, Lốc 6 lon
(4, 7, 1,    8500.00,  '8934563700001', true,  'PEPSI-LON'),
(4, 4, 6,   50000.00,  '8934563700002', false, 'PEPSI-LOC6'),
-- Mì Hảo Hảo: Gói đơn, Thùng 30 gói
(5, 5, 1,    4500.00,  '8934563800001', true,  'HH-GOI'),
(5, 3, 30, 125000.00,  '8934563800002', false, 'HH-THUNG'),
-- Mì Lẩu Thái: Gói đơn, Thùng 30 gói
(6, 5, 1,    5000.00,  '8934563900001', true,  'LT-GOI'),
(6, 3, 30, 140000.00,  '8934563900002', false, 'LT-THUNG'),
-- Nước mắm Nam Ngư: Chai 500ml
(7, 6, 1,   28000.00,  '8934674100001', true,  'NN-500ML'),
-- Tương ớt Chinsu: Chai 250g
(8, 6, 1,   18000.00,  '8934674200001', true,  'CS-250G'),
-- Dầu gội Dove: Chai 180ml
(9, 6, 1,   55000.00,  '8711700640803', true,  'DOVE-180ML'),
-- Sữa tắm Olay: Chai 650ml
(10, 6, 1, 120000.00,  '8001841248219', true,  'OLAY-650ML'),
-- Bánh gạo lứt: Gói
(11, 5, 1,  25000.00,  '8934574300001', true,  'GRL-GOI'),
-- Nước khoáng TH: Chai 500ml, Thùng 24 chai
(12, 6, 1,   5000.00,  '8934673999001', true,  'THW-500ML'),
(12, 3, 24, 112000.00, '8934673999002', false, 'THW-THUNG24');

-- =============================================
-- 10. SUPPLIER
-- =============================================
INSERT INTO Supplier (name, phone, address) VALUES
('Công ty CP Sữa Việt Nam (Vinamilk)',    '02838299999', 'Lô B2, KCN Sóng Thần 1, Dĩ An, Bình Dương'),
('Công ty CP Thực phẩm Sữa TH',          '02438255668', 'KCN Nghĩa Đàn, Nghệ An'),
('Công ty TNHH Coca-Cola Việt Nam',       '02838940960', 'QL1A, KCN Tam Phước, Biên Hòa, Đồng Nai'),
('Công ty TNHH Pepsico Việt Nam',         '02838948686', 'KCN Sóng Thần 2, Dĩ An, Bình Dương'),
('Công ty CP Acecook Việt Nam',           '02838740026', 'Đường số 6, KCX Tân Thuận, Q.7, TP.HCM'),
('Công ty CP Hàng tiêu dùng Masan',       '02439742886', 'Tầng 12, Tòa nhà MPlaza Saigon, Q.1, TP.HCM'),
('Công ty TNHH Unilever Việt Nam',        '02838239560', '9 Đường D4, KCN Biên Hòa 2, Đồng Nai'),
('Công ty TNHH P&G Việt Nam',             '02838372472', 'Lô A2, KCN Mỹ Xuân A, Bà Rịa - Vũng Tàu');

-- =============================================
-- 11. PAYMENT METHOD
-- =============================================
INSERT INTO PaymentMethod (code, name, status, created_at) VALUES
('CASH',   'Tiền mặt',            'ACTIVE', '2023-01-01 08:00:00'),
('BANK',   'Chuyển khoản ngân hàng', 'ACTIVE', '2023-01-01 08:00:00'),
('MOMO',   'Ví MoMo',             'ACTIVE', '2023-01-01 08:00:00'),
('ZALOPAY','Ví ZaloPay',          'ACTIVE', '2023-01-01 08:00:00'),
('VNPAY',  'VNPay QR',            'ACTIVE', '2023-01-01 08:00:00');

-- =============================================
-- 12. CUSTOMER RANK
-- =============================================
INSERT INTO CustomerRank (name, discount_rate, condition_total_revenue, description) VALUES
('Đồng',   0.00,  0.00,       'Khách hàng mới, chưa có ưu đãi'),
('Bạc',    2.00,  2000000.00, 'Giảm 2% cho đơn hàng, tổng mua từ 2 triệu'),
('Vàng',   5.00,  10000000.00,'Giảm 5% cho đơn hàng, tổng mua từ 10 triệu'),
('Bạch Kim',8.00, 30000000.00,'Giảm 8%, tổng mua từ 30 triệu, ưu tiên cao'),
('Kim Cương',10.00,100000000.00,'Giảm 10%, khách VIP, dịch vụ đặc biệt');

-- =============================================
-- 13. CUSTOMER
-- =============================================
INSERT INTO Customer (customer_rank_id, customer_code, full_name, phone, email, total_point, used_point, total_revenue, created_at, updated_at) VALUES
(1, 'KH-001', 'Nguyễn Thị Bích', '0901234567', 'bich.nguyen@gmail.com',     150,  0,   1500000, '2023-02-10 09:00:00', NULL),
(2, 'KH-002', 'Trần Văn Dũng',   '0912345678', 'dung.tran@gmail.com',        580, 50,   5800000, '2023-03-05 10:00:00', NULL),
(3, 'KH-003', 'Lê Thị Hương',    '0923456789', 'huong.le@gmail.com',        1200, 200, 12000000, '2023-01-20 11:00:00', NULL),
(1, 'KH-004', 'Phạm Quốc Toản',  '0934567890', 'toan.pham@gmail.com',         80,   0,    800000, '2023-05-15 08:30:00', NULL),
(4, 'KH-005', 'Hoàng Minh Tuấn', '0945678901', 'tuan.hoang@gmail.com',      3500, 500, 35000000, '2023-01-10 09:00:00', NULL),
(1, 'KH-006', 'Đặng Thị Linh',   '0956789012', 'linh.dang@gmail.com',         30,   0,    300000, '2024-01-03 14:00:00', NULL),
(2, 'KH-007', 'Bùi Văn Khánh',   '0967890123', 'khanh.bui@gmail.com',        420,  100,  4200000, '2023-07-22 10:00:00', NULL),
(5, 'KH-008', 'Vũ Thị Thu',      '0978901234', 'thu.vu@gmail.com',          9800, 1000,120000000, '2023-01-05 08:00:00', NULL);

-- =============================================
-- 14. VOUCHER
-- =============================================
INSERT INTO Voucher (code, name_voucher, discount_type, discount_value, min_order_value, max_discount_amount, usage_limit, used_count, start_date, end_date, status, created_at, created_by) VALUES
('WELCOME10',  'Chào mừng khách mới',         'PERCENT', 10.00,  100000.00,  50000.00, 1000, 325, '2023-01-01', '2025-12-31', 'ACTIVE', '2023-01-01 08:00:00', 1),
('SALE50K',    'Giảm 50.000đ đơn từ 500k',    'FIXED',   50000.00,500000.00,  50000.00,  500, 180, '2023-06-01', '2023-12-31', 'INACTIVE','2023-06-01 08:00:00', 1),
('SUMMER20',   'Hè rực rỡ giảm 20%',          'PERCENT', 20.00,  200000.00, 100000.00,  300,  98, '2023-06-01', '2023-08-31', 'INACTIVE','2023-06-01 08:00:00', 2),
('VIP100K',    'Ưu đãi VIP giảm 100k',        'FIXED',  100000.00,1000000.00,100000.00,  200,  45, '2024-01-01', '2026-12-31', 'ACTIVE', '2024-01-01 08:00:00', 1),
('TETHOLIDAY', 'Khuyến mãi Tết Giáp Thìn',    'PERCENT', 15.00,  300000.00, 150000.00,  500, 210, '2024-02-01', '2024-02-15', 'INACTIVE','2024-01-25 08:00:00', 1),
('FREESHIP',   'Miễn phí vận chuyển',         'FIXED',   30000.00, 150000.00, 30000.00, 2000, 876, '2023-01-01', '2026-12-31', 'ACTIVE', '2023-01-01 08:00:00', 1);

-- =============================================
-- 15. ORDER TRANSACTION
-- =============================================
INSERT INTO OrderTransaction (branch_id, customer_id, voucher_id, payment_method_id, code, total_amount, discount_amount, final_amount, status, Transaction_type, From_branch_id, To_branch_id, create_by, created_at, updated_at) VALUES
-- Đơn bán hàng
(1, 3,    1, 1, 'HD-2024-0001', 450000.00,  45000.00, 405000.00, 'RECEIVED', 'SALE',   NULL, NULL, 5, '2024-01-10 09:15:00', '2024-01-10 09:20:00'),
(1, 2, NULL, 3, 'HD-2024-0002', 230000.00,      0.00, 230000.00, 'RECEIVED', 'SALE',   NULL, NULL, 5, '2024-01-12 14:30:00', '2024-01-12 14:35:00'),
(2, 5,    4, 2, 'HD-2024-0003',1500000.00, 100000.00,1400000.00, 'RECEIVED', 'SALE',   NULL, NULL, 7, '2024-01-15 10:00:00', '2024-01-15 10:10:00'),
(3, 8, NULL, 5, 'HD-2024-0004', 880000.00,     0.00,  880000.00, 'RECEIVED', 'SALE',   NULL, NULL, 8, '2024-01-18 11:45:00', '2024-01-18 11:50:00'),
(1, 1,    6, 1, 'HD-2024-0005', 175000.00,  30000.00, 145000.00, 'RECEIVED', 'SALE',   NULL, NULL, 6, '2024-02-01 08:30:00', '2024-02-01 08:35:00'),
-- Đơn nhập hàng
(1, NULL, NULL, 2, 'NK-2024-0001', 5400000.00, 0.00, 5400000.00, 'RECEIVED', 'IMPORT', NULL, NULL, 2, '2024-01-05 08:00:00', '2024-01-05 09:00:00'),
(2, NULL, NULL, 2, 'NK-2024-0002', 3200000.00, 0.00, 3200000.00, 'RECEIVED', 'IMPORT', NULL, NULL, 3, '2024-01-08 08:00:00', '2024-01-08 09:00:00'),
-- Đơn chuyển kho
(1, NULL, NULL, NULL,'CK-2024-0001', 500000.00, 0.00, 500000.00, 'RECEIVED', 'EXPORT', 1,    2,    2, '2024-01-20 07:00:00', '2024-01-20 07:30:00');

-- =============================================
-- 16. ORDER ITEM
-- =============================================
INSERT INTO OrderItem (order_id, product_unit_id, supplier_id, quantity, unit_price, discount_amount, subtotal) VALUES
-- HD-2024-0001 (order_id=1): Sữa Vinamilk Lốc + Mì Hảo Hảo Gói
(1, 2,  NULL, 3,  30000.00, 0.00,  90000.00),
(1, 11, NULL, 80,  4500.00, 0.00, 360000.00),
-- HD-2024-0002 (order_id=2): Coca-Cola lon + Tương ớt
(2, 6,  NULL, 12,  9000.00, 0.00, 108000.00),
(2, 16, NULL, 2,  28000.00, 0.00,  56000.00),
(2, 17, NULL, 3,  18000.00, 0.00,  54000.00),
-- HD-2024-0003 (order_id=3): Dầu gội Dove + Sữa tắm Olay + Nước mắm
(3, 18, NULL, 10, 55000.00, 0.00, 550000.00),
(3, 19, NULL, 5, 120000.00, 0.00, 600000.00),
(3, 16, NULL, 12, 28000.00, 0.00, 336000.00),
-- HD-2024-0004 (order_id=4): Sữa TH Hộp + Nước khoáng Thùng
(4, 4,  NULL, 20, 18000.00, 0.00, 360000.00),
(4, 22, NULL, 2, 112000.00,0.00, 224000.00),
(4, 21, NULL, 6,  18000.00, 0.00, 108000.00),  -- Mì lẩu thái
(4, 17, NULL, 1,  18000.00, 0.00,  18000.00),
-- HD-2024-0005 (order_id=5): Mì Hảo Hảo + Bánh gạo lứt
(5, 11, NULL, 20,  4500.00, 0.00,  90000.00),
(5, 20, NULL, 2,  25000.00, 0.00,  50000.00),
(5, 21, NULL, 5,  5000.00,  0.00,  25000.00),
-- NK-2024-0001 (order_id=6): Nhập Sữa Vinamilk Thùng
(6, 3,  1,   10, 300000.00, 0.00,3000000.00),
(6, 12, 3,   12, 200000.00, 0.00,2400000.00),
-- NK-2024-0002 (order_id=7): Nhập Mì Hảo Hảo Thùng
(7, 12, 5,   20, 110000.00, 0.00,2200000.00),
(7, 4,  2,    5, 200000.00, 0.00,1000000.00),
-- CK-2024-0001 (order_id=8): Chuyển kho
(8, 11, NULL, 100, 4500.00, 0.00, 450000.00),
(8, 21, NULL, 10,  5000.00, 0.00,  50000.00);

-- =============================================
-- 17. INVENTORY
-- =============================================
INSERT INTO Inventory (branch_id, product_unit_id, stock, min_stock, max_stock, position_in_shop) VALUES
-- Chi nhánh 1
(1, 1,  500, 100, 1000, 'Kệ A1-01'),
(1, 2,  120,  20,  300, 'Kệ A1-02'),
(1, 3,   15,   3,   30, 'Kệ A1-03'),
(1, 6,  300,  50,  600, 'Kệ B2-01'),
(1, 11, 400,  80,  800, 'Kệ C3-01'),
(1, 12,  10,   2,   20, 'Kệ C3-02'),
(1, 16, 100,  20,  200, 'Kệ D4-01'),
(1, 17,  80,  20,  150, 'Kệ D4-02'),
(1, 18,  60,  10,  120, 'Kệ E5-01'),
(1, 19,  40,  10,  100, 'Kệ E5-02'),
(1, 20,  90,  20,  180, 'Kệ F6-01'),
(1, 21, 600, 100, 1200, 'Kệ G7-01'),
-- Chi nhánh 2
(2, 1,  300,  50,  600, 'Kệ A1-01'),
(2, 4,  150,  30,  300, 'Kệ A2-01'),
(2, 6,  200,  40,  400, 'Kệ B1-01'),
(2, 11, 350,  50,  700, 'Kệ C1-01'),
(2, 13,  80,  20,  150, 'Kệ C2-01'),
(2, 17,  70,  15,  140, 'Kệ D1-01'),
-- Chi nhánh 3
(3, 1,  250,  50,  500, 'Kệ A1-01'),
(3, 4,  100,  20,  200, 'Kệ A2-01'),
(3, 6,  180,  30,  360, 'Kệ B1-01'),
(3, 9,  100,  20,  200, 'Kệ B2-01'),
(3, 10,  60,  10,  120, 'Kệ B3-01'),
(3, 18,  50,  10,  100, 'Kệ C1-01'),
(3, 21, 400,  60,  800, 'Kệ D1-01'),
(3, 22,  15,   3,   30, 'Kệ D2-01');

-- =============================================
-- 18. POINT HISTORY
-- =============================================
INSERT INTO PointHistory (customer_id, order_id, point_earned, created_at) VALUES
(3, 1, 40,  '2024-01-10 09:20:00'),
(2, 2, 23,  '2024-01-12 14:35:00'),
(5, 3, 140, '2024-01-15 10:10:00'),
(8, 4, 88,  '2024-01-18 11:50:00'),
(1, 5, 14,  '2024-02-01 08:35:00');

-- =============================================
-- 19. LOG HISTORY
-- =============================================
INSERT INTO LogHistory (employee_id, action, time) VALUES
(1, 'LOGIN',          '2024-01-01 08:00:00'),
(2, 'LOGIN',          '2024-01-05 07:55:00'),
(3, 'LOGIN',          '2024-01-08 08:02:00'),
(5, 'LOGIN',          '2024-01-10 08:00:00'),
(5, 'LOGOUT',         '2024-01-10 17:30:00'),
(6, 'LOGIN',          '2024-01-12 08:05:00'),
(7, 'LOGIN',          '2024-01-15 07:50:00'),
(7, 'LOGOUT',         '2024-01-15 17:00:00'),
(8, 'LOGIN',          '2024-01-18 08:10:00'),
(1, 'LOGIN_FAIL',     '2024-01-20 07:30:00'),
(1, 'LOGIN',          '2024-01-20 07:32:00'),
(4, 'FORGET_PASSWORD','2024-02-01 09:00:00'),
(4, 'LOGIN',          '2024-02-01 09:10:00'),
(1, 'LOGIN',          NOW()),
(2, 'LOGIN',          NOW());
