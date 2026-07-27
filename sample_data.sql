-- ============================================================
-- SMS_DB – DỮ LIỆU MẪU (Cửa hàng tạp hóa – Winmart style)
-- Mật khẩu nhân viên: Admin@123
-- Hash: $2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re
-- ============================================================
USE sms_db;

-- 1. ROLE
INSERT INTO Role (id, code, name, description) VALUES
(1,'OWNER','Chủ chuỗi','Toàn quyền quản lý toàn bộ hệ thống'),
(2,'BRANCH_MANAGER','Quản lý chi nhánh','Quản lý nhân sự, báo cáo và hoạt động chi nhánh'),
(3,'WAREHOUSE_STAFF','Nhân viên kho','Nhập hàng, xuất hàng, kiểm kê tồn kho'),
(4,'SALE_STAFF','Nhân viên bán hàng','Bán hàng, đổi trả, quản lý khách hàng');

-- 2. BRANCH
INSERT INTO Branch (id, branch_code, name, phone, email, address, status, opened_at, closed_at, note, created_at, updated_at) VALUES
(1,'CN001','Winmart+ Hà Nội - Hoàn Kiếm','02438001122','hanoi.hoankiem@winmart.vn','15 Hàng Đào, Hoàn Kiếm, Hà Nội','ACTIVE','2022-01-15',NULL,'Chi nhánh đầu tiên',NOW(),NULL),
(2,'CN002','Winmart+ TP.HCM - Quận 1','02838002233','hcm.q1@winmart.vn','25 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh','ACTIVE','2022-06-01',NULL,NULL,NOW(),NULL),
(3,'CN003','Winmart+ Đà Nẵng - Hải Châu','02363003344','danang.haichau@winmart.vn','10 Bạch Đằng, Hải Châu, Đà Nẵng','ACTIVE','2023-03-20',NULL,NULL,NOW(),NULL);

-- 3. EMPLOYEE – bước 1: OWNER
INSERT INTO Employee (id,branch_id,role_id,manager_id,created_by,employee_code,full_name,email,password_hash,phone,address,gender,dob,hired_date,base_salary,work_status,note,created_at,updated_at) VALUES
(1,NULL,1,NULL,NULL,'EMP001','Nguyễn Văn An','owner@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0901000001','1 Lê Duẩn, Đống Đa, Hà Nội','MALE','1985-05-10','2022-01-01',50000000.00,'ACTIVE','Tài khoản chủ chuỗi',NOW(),NULL);

-- bước 2: các nhân viên còn lại
INSERT INTO Employee (id,branch_id,role_id,manager_id,created_by,employee_code,full_name,email,password_hash,phone,address,gender,dob,hired_date,base_salary,work_status,note,created_at,updated_at) VALUES
(2,1,2,1,1,'EMP002','Trần Thị Bình','manager.hn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0902000002','15 Hàng Đào, Hoàn Kiếm, Hà Nội','FEMALE','1990-08-22','2022-01-15',20000000.00,'ACTIVE',NULL,NOW(),NULL),
(3,2,2,1,1,'EMP003','Lê Văn Cường','manager.hcm@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0903000003','25 Nguyễn Huệ, Quận 1, TP.HCM','MALE','1988-11-03','2022-06-01',20000000.00,'ACTIVE',NULL,NOW(),NULL),
(4,3,2,1,1,'EMP004','Phạm Thị Dung','manager.dn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0904000004','10 Bạch Đằng, Hải Châu, Đà Nẵng','FEMALE','1992-02-14','2023-03-20',20000000.00,'ACTIVE',NULL,NOW(),NULL),
(5,1,3,2,1,'EMP005','Hoàng Văn Em','warehouse1.hn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0905000005','20 Lý Thường Kiệt, Hà Nội','MALE','1995-06-30','2022-02-01',10000000.00,'ACTIVE',NULL,NOW(),NULL),
(6,2,3,3,1,'EMP006','Ngô Thị Phương','warehouse1.hcm@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0906000006','30 Đinh Tiên Hoàng, Quận 1, TP.HCM','FEMALE','1997-09-12','2022-07-01',10000000.00,'ACTIVE',NULL,NOW(),NULL),
(7,1,4,2,1,'EMP007','Đinh Văn Giang','sale1.hn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0907000007','5 Phan Chu Trinh, Hoàn Kiếm, Hà Nội','MALE','1999-03-17','2022-03-01',8000000.00,'ACTIVE',NULL,NOW(),NULL),
(8,2,4,3,1,'EMP008','Võ Thị Hoa','sale1.hcm@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0908000008','12 Pasteur, Quận 3, TP.HCM','FEMALE','2000-07-25','2022-08-15',8000000.00,'ACTIVE',NULL,NOW(),NULL),
(9,3,4,4,1,'EMP009','Bùi Văn Ích','sale1.dn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0909000009','8 Hùng Vương, Hải Châu, Đà Nẵng','MALE','1998-12-01','2023-04-01',8000000.00,'ACTIVE',NULL,NOW(),NULL),

(12,3,3,4,1,'EMP012','Đoàn Thị Mai','warehouse1.dn@winmart.vn','$2a$10$sX0QXo37M/khVqd85nG.IuWEPLGdcs735zCCMjfNufGpDmjL259Re','0912000012','22 Lê Duẩn, Hải Châu, Đà Nẵng','FEMALE','1996-01-15','2023-05-01',10000000.00,'INACTIVE','Đã nghỉ việc',NOW(),NOW());

-- 4. CUSTOMER RANK
INSERT INTO CustomerRank (id,name,discount_rate,condition_total_revenue,description,created_at,updated_at) VALUES
(1,'Thành viên', 0.00,       0.00,'Hạng mặc định khi đăng ký',NOW(),NULL),
(2,'Bạc',        3.00, 2000000.00,'Tổng mua từ 2 triệu',NOW(),NULL),
(3,'Vàng',       5.00, 5000000.00,'Tổng mua từ 5 triệu',NOW(),NULL),
(4,'Kim Cương', 10.00,15000000.00,'Tổng mua từ 15 triệu',NOW(),NULL);

-- 5. CUSTOMER (15 khách hàng)
INSERT INTO Customer (id,customer_rank_id,created_by,updated_by,customer_code,full_name,phone,email,gender,dob,address,total_point,used_point,total_revenue,status,note,created_at,updated_at) VALUES
(1, 1,7,NULL,'KH001','Nguyễn Thị Anh',   '0981001001','anh.nguyen@gmail.com',  'FEMALE','1995-03-12','4 Hàng Bài, Hoàn Kiếm, Hà Nội',      68,  0,   680000.00,'ACTIVE',NULL,NOW(),NULL),
(2, 2,7,NULL,'KH002','Trần Văn Bảo',     '0982001002','bao.tran@yahoo.com',    'MALE',  '1988-07-20','9 Đinh Lễ, Hoàn Kiếm, Hà Nội',        320, 50, 94185000.00,'ACTIVE',NULL,NOW(),NULL),
(3, 3,8,NULL,'KH003','Lê Thị Cẩm',       '0983001003','cam.le@outlook.com',    'FEMALE','1992-11-05','7 Đồng Khởi, Quận 1, TP.HCM',          750,100, 7500000.00,'ACTIVE',NULL,NOW(),NULL),
(4, 4,8,   8,'KH004','Phạm Văn Dũng',    '0984001004','dung.pham@gmail.com',   'MALE',  '1985-01-30','33 Pasteur, Quận 1, TP.HCM',           2200,300,310850000.00,'ACTIVE',NULL,NOW(),NOW()),
(5, 1,9,NULL,'KH005','Hoàng Thị Em',     '0985001005',NULL,                    'FEMALE','2000-06-18','15 Hùng Vương, Đà Nẵng',               65,  0,   650000.00,'ACTIVE',NULL,NOW(),NULL),
(6, 1,7,NULL,'KH006','Vũ Văn Phúc',      '0986001006','phuc.vu@gmail.com',     'MALE',  '1998-09-22','20 Bà Triệu, Hai Bà Trưng, Hà Nội',   180, 0,  1800000.00,'ACTIVE',NULL,NOW(),NULL),
(7, 1,8,NULL,'KH007','Đặng Thị Giang',   '0987001007',NULL,                    'FEMALE','2003-02-14','55 Đinh Tiên Hoàng, Quận 1, TP.HCM',  35,  0,   350000.00,'INACTIVE','Đã yêu cầu xóa tài khoản',NOW(),NOW()),
(8, 2,8,NULL,'KH008','Ngô Minh Hùng',    '0988001008','hung.ngo@gmail.com',    'MALE',  '1991-04-15','8 Nguyễn Bỉnh Khiêm, Quận 1, TP.HCM', 250, 0,  2500000.00,'ACTIVE',NULL,NOW(),NULL),
(9, 3,8,NULL,'KH009','Trịnh Thị Lan',    '0989001009','lan.trinh@yahoo.com',   'FEMALE','1987-07-08','3 Tôn Đức Thắng, Quận 1, TP.HCM',     610,100, 6100000.00,'ACTIVE',NULL,NOW(),NULL),
(10,1,9,NULL,'KH010','Đỗ Văn Mạnh',      '0990001010',NULL,                    'MALE',  '1993-12-25','22 Lê Lợi, Hải Châu, Đà Nẵng',        89,  0,   890000.00,'ACTIVE',NULL,NOW(),NULL),
(11,2,8,NULL,'KH011','Lưu Thị Ngọc',     '0991001011','ngoc.luu@gmail.com',    'FEMALE','1989-03-18','67 Pasteur, Quận 3, TP.HCM',           280, 30, 2800000.00,'ACTIVE',NULL,NOW(),NULL),
(12,4,8,   8,'KH012','Cao Văn Phong',     '0992001012','phong.cao@gmail.com',   'MALE',  '1982-08-10','120 Hai Bà Trưng, Quận 3, TP.HCM',   1850,200,18500000.00,'ACTIVE',NULL,NOW(),NOW()),
(13,3,9,NULL,'KH013','Bùi Thị Quỳnh',    '0993001013','quynh.bui@outlook.com', 'FEMALE','1994-05-22','5 Hùng Vương, Hải Châu, Đà Nẵng',     580, 80, 5800000.00,'ACTIVE',NULL,NOW(),NULL),
(14,1,7,NULL,'KH014','Hà Văn Sơn',       '0994001014',NULL,                    'MALE',  '2001-09-11','44 Lý Thường Kiệt, Hoàn Kiếm, Hà Nội',42,  0,   420000.00,'ACTIVE',NULL,NOW(),NULL),
(15,2,9,NULL,'KH015','Vũ Thị Tuyết',     '0995001015','tuyet.vu@gmail.com',    'FEMALE','1996-11-30','18 Trần Phú, Hải Châu, Đà Nẵng',      210, 20, 2100000.00,'ACTIVE',NULL,NOW(),NULL);

-- 6. BRAND (FMCG)
INSERT INTO Brand (id,name,status,created_at,updated_at) VALUES
(1,'Acecook',        'ACTIVE',NOW(),NULL),
(2,'Vinamilk',       'ACTIVE',NOW(),NULL),
(3,'Masan Consumer', 'ACTIVE',NOW(),NULL),
(4,'Unilever VN',    'ACTIVE',NOW(),NULL),
(5,'Coca-Cola VN',   'ACTIVE',NOW(),NULL),
(6,'P&G Vietnam',    'ACTIVE',NOW(),NULL),
(7,'Nestlé VN',      'ACTIVE',NOW(),NULL),
(8,'Liwayway (Oishi)','ACTIVE',NOW(),NULL),
(9,'Mondelez VN',    'ACTIVE',NOW(),NULL),
(10,'Generic',       'INACTIVE',NOW(),NOW());

-- 7. CATEGORY
INSERT INTO Category (id,name,description,status,created_at,updated_at) VALUES
(1,'Thực phẩm khô',      'Mì gói, gạo, bún khô, cháo ăn liền, ...','ACTIVE',NOW(),NULL),
(2,'Đồ uống',            'Nước ngọt, nước suối, cà phê, trà, sữa hộp','ACTIVE',NOW(),NULL),
(3,'Sữa & sản phẩm sữa','Sữa tươi, sữa đặc, sữa bột, phô mai, ...','ACTIVE',NOW(),NULL),
(4,'Bánh kẹo & snack',   'Bánh quy, kẹo, snack, bắp rang, ...','ACTIVE',NOW(),NULL),
(5,'Chăm sóc cá nhân',   'Dầu gội, sữa tắm, kem đánh răng, xà phòng','ACTIVE',NOW(),NULL),
(6,'Tẩy rửa & vệ sinh',  'Nước rửa chén, nước giặt, nước lau sàn, ...','ACTIVE',NOW(),NULL),
(7,'Gia vị & nước chấm', 'Nước mắm, tương ớt, nước tương, dầu ăn, ...','ACTIVE',NOW(),NULL);

-- 8. UNIT
INSERT INTO Unit (id,status,name,created_at,updated_at) VALUES
(1,'ACTIVE','Gói', NOW(),NULL),
(2,'ACTIVE','Thùng',NOW(),NULL),
(3,'ACTIVE','Chai', NOW(),NULL),
(4,'ACTIVE','Hộp', NOW(),NULL),
(5,'ACTIVE','Lốc', NOW(),NULL);

-- 9. SUPPLIER
INSERT INTO Supplier (id,supplier_code,name,phone,email,address,status,note,created_at,updated_at) VALUES
(1,'NCC001','Công ty TNHH Acecook Việt Nam',       '02838100001','acecook@supplier.vn',  '10 Tân Thới Hiệp, Quận 12, TP.HCM','ACTIVE',NULL,NOW(),NULL),
(2,'NCC002','Tổng Công ty CP Vinamilk',             '02838200002','vinamilk@supplier.vn', '184 Nguyễn Đình Chiểu, Quận 3, TP.HCM','ACTIVE','Nhà cung cấp sữa lớn nhất VN',NOW(),NULL),
(3,'NCC003','Công ty CP Masan Consumer',            '02438300003','masan@supplier.vn',    'Tầng 12 MPlaza, 39 Lê Duẩn, Hà Nội','ACTIVE',NULL,NOW(),NULL),
(4,'NCC004','Công ty TNHH Unilever Việt Nam',       '02838400004','unilever@supplier.vn', '156 Nguyễn Lương Bằng, Quận 7, TP.HCM','ACTIVE',NULL,NOW(),NULL),
(5,'NCC005','Công ty TNHH Coca-Cola Beverages VN',  '02838500005','cocacola@supplier.vn', 'KCN Long Bình, Biên Hòa, Đồng Nai','INACTIVE','Tạm ngừng hợp tác',NOW(),NOW());

-- 10. PAYMENT METHOD
INSERT INTO PaymentMethod (id,name,status,created_at,updated_at) VALUES
(1,'Tiền mặt',    'ACTIVE',NOW(),NULL),
(2,'Chuyển khoản','ACTIVE',NOW(),NULL),
(3,'Thẻ tín dụng','ACTIVE',NOW(),NULL),
(4,'Ví MoMo',     'ACTIVE',NOW(),NULL),
(5,'ZaloPay',     'ACTIVE',NOW(),NULL),
(6,'VNPay QR',    'ACTIVE',NOW(),NULL),
(7,'Thẻ ghi nợ',  'INACTIVE',NOW(),NOW());

-- 11. PRODUCT (15 sản phẩm)
INSERT INTO Product (id,category_id,brand_id,name,image_url,description,note,status,created_at,updated_at) VALUES
(1, 1,1,'Mì Hảo Hảo Tôm Chua Cay 75g',           NULL,'Mì ăn liền hương tôm chua cay, gói 75g',NULL,'ACTIVE',NOW(),NULL),
(2, 1,1,'Mì Phú Hương Hải Sản 65g',               NULL,'Mì ăn liền hương hải sản đặc biệt, gói 65g',NULL,'ACTIVE',NOW(),NULL),
(3, 1,3,'Gạo ST25 Sóc Trăng túi 5kg',             NULL,'Gạo đặc sản Sóc Trăng, dẻo thơm, túi 5kg',NULL,'ACTIVE',NOW(),NULL),
(4, 2,5,'Nước ngọt Coca-Cola lon 330ml',           NULL,'Nước ngọt có ga Coca-Cola, lon 330ml',NULL,'ACTIVE',NOW(),NULL),
(5, 2,7,'Nước suối Lavie chai 500ml',              NULL,'Nước khoáng thiên nhiên Lavie, chai 500ml',NULL,'ACTIVE',NOW(),NULL),
(6, 2,7,'Cà phê Nescafé 3in1 hộp 20 gói',         NULL,'Cà phê hòa tan Nescafé 3in1 Original, hộp 20 gói × 16g',NULL,'ACTIVE',NOW(),NULL),
(7, 3,2,'Sữa tươi Vinamilk không đường 1L',        NULL,'Sữa tươi tiệt trùng Vinamilk không đường, hộp 1 lít',NULL,'ACTIVE',NOW(),NULL),
(8, 3,2,'Sữa đặc Ông Thọ vàng 380g',              NULL,'Sữa đặc có đường Ông Thọ hộp thiếc 380g',NULL,'ACTIVE',NOW(),NULL),
(9, 4,9,'Bánh quy Oreo kem vani 137g',             NULL,'Bánh quy kẹp kem vani Oreo, gói 137g',NULL,'ACTIVE',NOW(),NULL),
(10,4,8,'Snack Oishi tôm chua cay 40g',            NULL,'Bánh snack tôm chua cay Oishi, gói 40g',NULL,'ACTIVE',NOW(),NULL),
(11,5,4,'Dầu gội Clear Men mát lạnh 650ml',        NULL,'Dầu gội đầu cho nam Clear Men mát lạnh, chai 650ml',NULL,'ACTIVE',NOW(),NULL),
(12,5,4,'Xà phòng Lifebuoy bảo vệ 10 khuẩn 90g',  NULL,'Xà phòng cục Lifebuoy diệt khuẩn, bánh 90g',NULL,'ACTIVE',NOW(),NULL),
(13,6,4,'Nước rửa chén Sunlight chanh 1kg',        NULL,'Nước rửa chén hương chanh Sunlight, chai 1kg',NULL,'ACTIVE',NOW(),NULL),
(14,6,6,'Nước giặt Tide trắng sạch 3kg',           NULL,'Nước giặt Tide trắng sáng bền màu, túi 3kg',NULL,'ACTIVE',NOW(),NULL),
(15,7,3,'Nước mắm Chin-su 40° chai 500ml',         NULL,'Nước mắm Chin-su độ đạm 40N, chai thủy tinh 500ml',NULL,'ACTIVE',NOW(),NULL);

-- 12. PRODUCT UNIT (22 đơn vị)
INSERT INTO ProductUnit (id,product_id,unit_id,sku,barcode_unit,conversion_value,price,is_base_unit,status,created_at,updated_at) VALUES
-- Mì Hảo Hảo 75g
(1, 1,1,'ACE-HH75-GOI',   '8934563140014',1,    4500.00,TRUE, 'ACTIVE',NOW(),NULL),
(2, 1,2,'ACE-HH75-THUNG', '8934563140021',30, 130000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Mì Phú Hương 65g
(3, 2,1,'ACE-PH65-GOI',   '8934563150013',1,    3500.00,TRUE, 'ACTIVE',NOW(),NULL),
(4, 2,2,'ACE-PH65-THUNG', '8934563150020',30, 100000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Gạo ST25 5kg
(5, 3,1,'MAS-GAO-ST25-5K','8936049700048',1,  125000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Coca-Cola lon 330ml
(6, 4,1,'CCA-LON-330-GOI','5449000000996',1,   10000.00,TRUE, 'ACTIVE',NOW(),NULL),
(7, 4,5,'CCA-LON-330-LOC','5449000001009',6,   58000.00,FALSE,'ACTIVE',NOW(),NULL),
(8, 4,2,'CCA-LON-THUNG',  '5449000001016',24, 228000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Lavie 500ml
(9, 5,3,'LAV-500-CHAI',   '8934588010016',1,    5000.00,TRUE, 'ACTIVE',NOW(),NULL),
(10,5,2,'LAV-500-THUNG',  '8934588010023',24, 114000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Nescafé 3in1 hộp
(11,6,4,'NES-3IN1-HOP',   '8934869109009',1,   55000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Sữa tươi Vinamilk 1L
(12,7,4,'VNM-TUOI-1L-HOP','8934868025025',1,   36000.00,TRUE, 'ACTIVE',NOW(),NULL),
(13,7,5,'VNM-TUOI-1L-LOC','8934868025032',4,  140000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Sữa đặc Ông Thọ 380g
(14,8,4,'VNM-OT-380-HOP', '8934868010144',1,   26000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Bánh quy Oreo 137g
(15,9,1,'MDZ-OREO-137-GOI','7622210951496',1,  25000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Snack Oishi 40g
(16,10,1,'LWY-OISHI-40-GOI','8935049000107',1,  8000.00,TRUE, 'ACTIVE',NOW(),NULL),
(17,10,2,'LWY-OISHI-THUNG','8935049000114',50,380000.00,FALSE,'ACTIVE',NOW(),NULL),
-- Dầu gội Clear Men 650ml
(18,11,3,'UNI-CLR-650-CHAI','8934804025045',1, 89000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Xà phòng Lifebuoy 90g
(19,12,1,'UNI-LBY-90-GOI', '8934804019006',1,  12000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Sunlight 1kg
(20,13,3,'UNI-SUN-1KG-CHAI','8934804030025',1, 45000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Tide 3kg
(21,14,1,'PG-TIDE-3KG-GOI','8006540891308',1, 155000.00,TRUE, 'ACTIVE',NOW(),NULL),
-- Nước mắm Chin-su 500ml
(22,15,3,'MAS-CHSU-500-CHAI','8936049710009',1,30000.00,TRUE, 'ACTIVE',NOW(),NULL);

-- 13. INVENTORY (42 bản ghi: 3 chi nhánh × 14 PU phổ biến)
INSERT INTO Inventory (id,branch_id,product_unit_id,stock,min_stock,max_stock,position_in_shop,created_at,updated_at) VALUES
-- CN001 (Hà Nội)
(1, 1,1, 500,100,2000,'Kệ A1-01',NOW(),NULL),(2, 1,2, 215,  5, 400,'Kho A1',   NOW(),NULL),
(3, 1,3, 600,150,2000,'Kệ A1-02',NOW(),NULL),(4, 1,4,  65, 10, 150,'Kho A1-2', NOW(),NULL),
(5, 1,5,  45, 10, 150,'Kệ A1-03',NOW(),NULL),(6, 1,6, 800, 200,3000,'Kệ B1-01',NOW(),NULL),
(7, 1,8,  25,  5, 100,'Kho B1',   NOW(),NULL),(8, 1,9, 400,100,1500,'Kệ B1-02',NOW(),NULL),
(9, 1,11,230, 50, 600,'Kệ C1-01',NOW(),NULL),(10,1,12,450,100,1500,'Kệ C1-02',NOW(),NULL),
(11,1,13, 80, 20, 300,'Kho C1',   NOW(),NULL),(12,1,14,200, 50, 600,'Kệ C1-03',NOW(),NULL),
(13,1,15,180, 40, 500,'Kệ D1-01',NOW(),NULL),(14,1,16,900,200,3000,'Kệ D1-02',NOW(),NULL),
(15,1,18, 80, 15, 200,'Kệ E1-01',NOW(),NULL),(16,1,19,480,100,1500,'Kệ E1-02',NOW(),NULL),
(17,1,20,180, 30, 500,'Kệ F1-01',NOW(),NULL),(18,1,21,140, 20, 300,'Kệ F1-02',NOW(),NULL),
(19,1,22,320, 80,1000,'Kệ G1-01',NOW(),NULL),
-- CN002 (TP.HCM)
(20,2,1, 800,200,3000,'Kệ A2-01',NOW(),NULL),(21,2,2,  30,  5, 100,'Kho A2',   NOW(),NULL),
(22,2,3, 700,200,2500,'Kệ A2-02',NOW(),NULL),(23,2,6, 500,150,2000,'Kệ B2-01',NOW(),NULL),
(24,2,8,  15,  3,  60,'Kho B2',   NOW(),NULL),(25,2,11,100, 30, 400,'Kệ C2-01',NOW(),NULL),
(26,2,12,450, 80,1500,'Kệ C2-02',NOW(),NULL),(27,2,13, 40, 10, 160,'Kho C2',   NOW(),NULL),
(28,2,14,250, 60, 800,'Kệ C2-03',NOW(),NULL),(29,2,18, 75, 15, 250,'Kệ D2-01',NOW(),NULL),
(30,2,19,300,100,1000,'Kệ D2-02',NOW(),NULL),(31,2,20,110, 25, 400,'Kệ F2-01',NOW(),NULL),
(32,2,21, 60, 15, 200,'Kệ F2-02',NOW(),NULL),(33,2,22,260, 60, 800,'Kệ G2-01',NOW(),NULL),
-- CN003 (Đà Nẵng)
(34,3,1, 300, 80,1200,'Kệ A3-01',NOW(),NULL),(35,3,2,  50, 10, 150,'Kho A3',   NOW(),NULL),
(36,3,3, 400,100,1500,'Kệ A3-02',NOW(),NULL),(37,3,6, 300,100,1200,'Kệ B3-01',NOW(),NULL),
(38,3,11, 60, 15, 200,'Kệ C3-01',NOW(),NULL),(39,3,16,500,150,2000,'Kệ D3-01',NOW(),NULL),
(40,3,20, 50, 10, 200,'Kệ F3-01',NOW(),NULL),(41,3,21, 30,  5, 100,'Kệ F3-02',NOW(),NULL),
(42,3,22,160, 40, 500,'Kệ G3-01',NOW(),NULL);

-- 14. VOUCHER
INSERT INTO Voucher (id,code,name,discount_type,discount_value,min_order_amount,max_discount_amount,start_at,end_at,customer_rank_id,status,created_at,updated_at) VALUES
(1,'WELCOME10','Giảm 10% cho khách mới',           'PERCENT',10.00,      0.00, 20000.00,'2025-01-01 00:00:00','2026-12-31 23:59:59',NULL,'ACTIVE',NOW(),NULL),
(2,'SALE10K',  'Giảm 10.000đ đơn từ 100k',         'AMOUNT', 10000.00,100000.00,    NULL,'2026-07-01 00:00:00','2026-07-31 23:59:59',NULL,'ACTIVE',NOW(),NULL),
(3,'GOLD15',   'Giảm 15% dành riêng Thành viên Vàng','PERCENT',15.00,200000.00,50000.00,'2025-01-01 00:00:00','2026-12-31 23:59:59',3,'ACTIVE',NOW(),NULL),
(4,'DIAMOND20','Giảm 20% dành riêng Kim Cương',    'PERCENT',20.00,500000.00,150000.00,'2025-01-01 00:00:00','2026-12-31 23:59:59',4,'ACTIVE',NOW(),NULL),
(5,'TETHOLIDAY','Voucher Tết 2025 (hết hạn)',       'PERCENT',20.00,100000.00,50000.00,'2025-01-01 00:00:00','2025-02-28 23:59:59',NULL,'INACTIVE',NOW(),NOW()),
(6,'SILVER5',  'Giảm 5% dành riêng Thành viên Bạc','PERCENT', 5.00,100000.00,15000.00,'2025-01-01 00:00:00','2026-12-31 23:59:59',2,'ACTIVE',NOW(),NULL);

-- 15. ORDER TRANSACTION (35 đơn hàng: nhập/bán/chuyển/trả – 2025 & 2026)
INSERT INTO OrderTransaction (id,branch_id,customer_id,voucher_id,supplier_id,payment_method_id,created_by,original_order_id,code,total_amount,discount_amount,points_used,point_discount,final_amount,paid_amount,change_amount,status,transaction_type,from_branch_id,to_branch_id,note,created_at,updated_at) VALUES
-- ==== 2025 Q1 ====
(1, 1,NULL,NULL,1,2,5,NULL,'IMP-20250301-001',31000000.00,0.00,NULL,NULL,31000000.00,31000000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập mì ăn liền Acecook tháng 3/2025','2025-03-01 08:00:00','2025-03-01 08:30:00'),
(2, 1,1,   NULL,NULL,1,7,NULL,'ORD-20250315-001',170000.00,0.00,0,0.00,170000.00,200000.00,30000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-03-15 09:00:00','2025-03-15 09:05:00'),
(3, 1,2,   6,   NULL,1,7,NULL,'ORD-20250320-001',500000.00,15000.00,0,0.00,485000.00,500000.00,15000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-03-20 10:30:00','2025-03-20 10:35:00'),
-- ==== 2025 Q2 ====
(4, 2,NULL,NULL,2,2,6,NULL,'IMP-20250401-001',56000000.00,0.00,NULL,NULL,56000000.00,56000000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập sữa Vinamilk & Coca-Cola tháng 4/2025','2025-04-01 08:00:00','2025-04-01 08:30:00'),
(5, 2,4,   4,   NULL,3,8,NULL,'ORD-20250415-001',1709000.00,150000.00,0,0.00,1559000.00,1559000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-04-15 15:00:00','2025-04-15 15:05:00'),
(6, 3,5,   NULL,NULL,4,9,NULL,'ORD-20250510-001',160000.00,0.00,0,0.00,160000.00,160000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-05-10 11:00:00','2025-05-10 11:05:00'),
(7, 1,NULL,NULL,4,2,5,NULL,'IMP-20250601-001',32600000.00,0.00,NULL,NULL,32600000.00,32600000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập hàng Unilever tháng 6/2025','2025-06-01 08:00:00','2025-06-01 08:30:00'),
(8, 1,6,   NULL,NULL,1,7,NULL,'ORD-20250615-001',313000.00,0.00,0,0.00,313000.00,320000.00,7000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-06-15 11:30:00','2025-06-15 11:35:00'),
(9, 2,8,   NULL,NULL,5,8,NULL,'ORD-20250620-001',876000.00,0.00,0,0.00,876000.00,876000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-06-20 14:00:00','2025-06-20 14:05:00'),
-- ==== 2025 Q3 ====
(10,3,NULL,NULL,1,2,4,NULL,'IMP-20250701-001',15500000.00,0.00,NULL,NULL,15500000.00,15500000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập mì Acecook cho CN003 tháng 7/2025','2025-07-01 08:00:00','2025-07-01 08:30:00'),
(11,2,9,   3,   NULL,2,8,NULL,'ORD-20250715-001',595000.00,50000.00,0,0.00,545000.00,545000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-07-15 16:00:00','2025-07-15 16:05:00'),
(12,3,10,  NULL,NULL,1,9,NULL,'ORD-20250720-001',110000.00,0.00,0,0.00,110000.00,120000.00,10000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-07-20 09:00:00','2025-07-20 09:05:00'),
(13,1,NULL,NULL,NULL,NULL,2,NULL,'TRF-20250801-001',0.00,0.00,NULL,NULL,0.00,0.00,0.00,'COMPLETED','TRANSFER',1,3,'Chuyển hàng mì & snack sang CN003 Đà Nẵng','2025-08-01 07:00:00','2025-08-01 17:00:00'),
(14,2,12,  4,   NULL,6,8,NULL,'ORD-20250815-001',1912000.00,150000.00,0,0.00,1762000.00,1762000.00,0.00,'REFUNDED','SALE',NULL,NULL,NULL,'2025-08-15 11:00:00','2025-08-15 11:05:00'),
(15,2,12,  NULL,NULL,2,8,14, 'RET-20250816-001',140000.00,0.00,NULL,NULL,140000.00,0.00,0.00,'COMPLETED','RETURN',NULL,NULL,'Hoàn tiền lốc sữa Vinamilk bị phình nắp','2025-08-16 09:00:00','2025-08-16 09:30:00'),
(16,1,2,   6,   NULL,1,7,NULL,'ORD-20250901-001',91000000.00,15000.00,0,0.00,90985000.00,90985000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-09-01 10:00:00','2025-09-01 10:05:00'),
(17,3,13,  3,   NULL,4,9,NULL,'ORD-20250915-001',395000.00,50000.00,0,0.00,345000.00,345000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-09-15 14:00:00','2025-09-15 14:05:00'),
-- ==== 2025 Q4 ====
(18,2,NULL,NULL,3,2,6,NULL,'IMP-20251001-001',22600000.00,0.00,NULL,NULL,22600000.00,22600000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập hàng Masan Consumer tháng 10/2025','2025-10-01 08:00:00','2025-10-01 08:30:00'),
(19,2,3,   3,   NULL,2,8,NULL,'ORD-20251015-001',446000.00,50000.00,0,0.00,396000.00,396000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-10-15 14:00:00','2025-10-15 14:05:00'),
(20,1,NULL,NULL,NULL,1,7,NULL,'ORD-20251101-001',76000.00,0.00,NULL,NULL,76000.00,80000.00,4000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-11-01 09:00:00','2025-11-01 09:05:00'),
(21,3,15,  6,   NULL,5,9,NULL,'ORD-20251115-001',355000.00,15000.00,0,0.00,340000.00,340000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-11-15 15:00:00','2025-11-15 15:05:00'),
(22,1,4,   4,   NULL,3,7,NULL,'ORD-20251201-001',289000000.00,150000.00,0,0.00,288850000.00,288850000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-12-01 16:00:00','2025-12-01 16:05:00'),
(23,2,11,  6,   NULL,6,8,NULL,'ORD-20251215-001',581000.00,15000.00,0,0.00,566000.00,566000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2025-12-15 15:00:00','2025-12-15 15:05:00'),
-- ==== 2026 Q1 ====
(24,2,NULL,NULL,4,2,6,NULL,'IMP-20260101-001',19500000.00,0.00,NULL,NULL,19500000.00,19500000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập hàng Unilever đầu năm 2026','2026-01-01 08:00:00','2026-01-01 08:30:00'),
(25,1,2,   2,   NULL,1,7,NULL,'ORD-20260115-001',650000.00,10000.00,0,0.00,640000.00,650000.00,10000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2026-01-15 09:30:00','2026-01-15 09:35:00'),
(26,2,3,   3,   NULL,2,8,NULL,'ORD-20260201-001',876000.00,50000.00,100,20000.00,806000.00,806000.00,0.00,'REFUNDED','SALE',NULL,NULL,NULL,'2026-02-01 10:00:00','2026-02-01 10:10:00'),
(27,1,NULL,NULL,NULL,1,7,NULL,'ORD-20260310-001',90000.00,0.00,NULL,NULL,90000.00,100000.00,10000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2026-03-10 14:00:00','2026-03-10 14:05:00'),
-- ==== 2026 Q2 ====
(28,1,NULL,NULL,1,2,5,NULL,'IMP-20260401-001',31000000.00,0.00,NULL,NULL,31000000.00,31000000.00,0.00,'COMPLETED','IMPORT',NULL,NULL,'Nhập mì ăn liền Acecook tháng 4/2026','2026-04-01 08:00:00','2026-04-01 08:30:00'),
(29,2,NULL,NULL,2,2,6,NULL,'IMP-20260501-001',100000000.00,0.00,NULL,NULL,100000000.00,0.00,0.00,'PENDING','IMPORT',NULL,NULL,'Nhập sữa Vinamilk & Coca-Cola tháng 5/2026, chờ xác nhận','2026-05-10 09:00:00',NULL),
(30,1,NULL,NULL,NULL,NULL,2,NULL,'TRF-20260601-001',0.00,0.00,NULL,NULL,0.00,0.00,0.00,'COMPLETED','TRANSFER',1,3,'Chuyển mì & snack sang CN003','2026-06-01 07:00:00','2026-06-01 17:00:00'),
(31,2,4,   4,   NULL,3,8,NULL,'ORD-20260615-001',202000.00,40400.00,0,0.00,161600.00,0.00,0.00,'CANCELLED','SALE',NULL,NULL,'Khách hủy đơn trước khi thanh toán','2026-06-15 11:00:00','2026-06-15 11:30:00'),
(32,2,3,   NULL,NULL,2,8,26, 'RET-20260210-001',140000.00,0.00,NULL,NULL,140000.00,0.00,0.00,'COMPLETED','RETURN',NULL,NULL,'Hoàn tiền lốc sữa bị phình nắp','2026-02-10 09:00:00','2026-02-10 09:30:00'),
-- ==== 2026 Q3 (tháng 7) ====
(33,1,14,  NULL,NULL,1,7,NULL,'ORD-20260705-001',155000.00,0.00,0,0.00,155000.00,160000.00,5000.00,'COMPLETED','SALE',NULL,NULL,NULL,'2026-07-05 09:30:00','2026-07-05 09:35:00'),
(34,2,9,   3,   NULL,2,8,NULL,'ORD-20260710-001',508000.00,50000.00,0,0.00,458000.00,458000.00,0.00,'COMPLETED','SALE',NULL,NULL,NULL,'2026-07-10 14:00:00','2026-07-10 14:10:00'),
(35,3,5,   NULL,NULL,4,9,NULL,'ORD-20260720-001',110000.00,0.00,NULL,NULL,110000.00,0.00,0.00,'PENDING','SALE',NULL,NULL,NULL,'2026-07-20 15:00:00',NULL);

-- 16. ORDER TRANSACTION DETAIL (77 dòng)
INSERT INTO OrderTransactionDetail (id,order_transaction_id,product_unit_id,quantity,sale_price,import_price,discount_amount,total_amount,created_at) VALUES
-- Order 1 IMP-20250301 (nhập mì)
(1, 1,2,200,NULL,110000.00,0.00,22000000.00,'2025-03-01 08:00:00'),
(2, 1,4,100,NULL, 90000.00,0.00, 9000000.00,'2025-03-01 08:00:00'),
-- Order 2 ORD-20250315 KH001 (bán lẻ)
(3, 2,15,3,25000.00,NULL,0.00,75000.00,'2025-03-15 09:00:00'),
(4, 2,16,5, 8000.00,NULL,0.00,40000.00,'2025-03-15 09:00:00'),
(5, 2,11,1,55000.00,NULL,0.00,55000.00,'2025-03-15 09:00:00'),
-- Order 3 ORD-20250320 KH002 (Silver5)
(6, 3,2, 3,130000.00,NULL,15000.00,375000.00,'2025-03-20 10:30:00'),
(7, 3,11,2, 55000.00,NULL,0.00,   110000.00,'2025-03-20 10:30:00'),
-- Order 4 IMP-20250401 CN002 (nhập sữa+coca)
(8, 4,13,300,NULL,120000.00,0.00,36000000.00,'2025-04-01 08:00:00'),
(9, 4,8, 100,NULL,200000.00,0.00,20000000.00,'2025-04-01 08:00:00'),
-- Order 5 ORD-20250415 KH004 Diamond
(10,5,8, 3,228000.00,NULL,50000.00,  634000.00,'2025-04-15 15:00:00'),
(11,5,13,4,140000.00,NULL,50000.00,  510000.00,'2025-04-15 15:00:00'),
(12,5,21,3,155000.00,NULL,50000.00,  415000.00,'2025-04-15 15:00:00'),
-- Order 6 ORD-20250510 KH005
(13,6,11,2,55000.00,NULL,0.00,110000.00,'2025-05-10 11:00:00'),
(14,6,9, 10,5000.00,NULL,0.00, 50000.00,'2025-05-10 11:00:00'),
-- Order 7 IMP-20250601 CN001 Unilever
(15,7,18,100,NULL,75000.00,0.00, 7500000.00,'2025-06-01 08:00:00'),
(16,7,19,500,NULL, 9000.00,0.00, 4500000.00,'2025-06-01 08:00:00'),
(17,7,20,200,NULL,38000.00,0.00, 7600000.00,'2025-06-01 08:00:00'),
(18,7,21,100,NULL,130000.00,0.00,13000000.00,'2025-06-01 08:00:00'),
-- Order 8 ORD-20250615 KH006
(19,8,18,2,89000.00,NULL,0.00,178000.00,'2025-06-15 11:30:00'),
(20,8,20,3,45000.00,NULL,0.00,135000.00,'2025-06-15 11:30:00'),
-- Order 9 ORD-20250620 KH008
(21,9,13,3,140000.00,NULL,0.00,420000.00,'2025-06-20 14:00:00'),
(22,9,8, 2,228000.00,NULL,0.00,456000.00,'2025-06-20 14:00:00'),
-- Order 10 IMP-20250701 CN003
(23,10,2, 100,NULL,110000.00,0.00,11000000.00,'2025-07-01 08:00:00'),
(24,10,4,  50,NULL, 90000.00,0.00, 4500000.00,'2025-07-01 08:00:00'),
-- Order 11 ORD-20250715 KH009 Gold15
(25,11,13,2,140000.00,NULL,25000.00,255000.00,'2025-07-15 16:00:00'),
(26,11,11,3, 55000.00,NULL,15000.00,150000.00,'2025-07-15 16:00:00'),
(27,11,22,5, 30000.00,NULL,10000.00,140000.00,'2025-07-15 16:00:00'),
-- Order 12 ORD-20250720 KH010
(28,12,16,10,8000.00,NULL,0.00,80000.00,'2025-07-20 09:00:00'),
(29,12,9,  6,5000.00,NULL,0.00,30000.00,'2025-07-20 09:00:00'),
-- Order 13 TRF-20250801 CN001→CN003
(30,13,2, 30,NULL,NULL,0.00,0.00,'2025-08-01 07:00:00'),
(31,13,17,20,NULL,NULL,0.00,0.00,'2025-08-01 07:00:00'),
-- Order 14 ORD-20250815 KH012 Diamond
(32,14,13,5,140000.00,NULL,50000.00,650000.00,'2025-08-15 11:00:00'),
(33,14,8, 4,228000.00,NULL,50000.00,862000.00,'2025-08-15 11:00:00'),
(34,14,22,10,30000.00,NULL,50000.00,250000.00,'2025-08-15 11:00:00'),
-- Order 15 RET-20250816 KH012 (trả lốc sữa)
(35,15,13,1,140000.00,NULL,0.00,140000.00,'2025-08-16 09:00:00'),
-- Order 16 ORD-20250901 KH002 Silver5
(36,16,2, 500,130000.00,NULL,10000.00,65000000.00,'2025-09-01 10:00:00'),
(37,16,14,1000, 26000.00,NULL, 5000.00,26000000.00,'2025-09-01 10:00:00'),
-- Order 17 ORD-20250915 KH013 Gold15
(38,17,2, 2,130000.00,NULL,25000.00,235000.00,'2025-09-15 14:00:00'),
(39,17,20,3, 45000.00,NULL,25000.00,110000.00,'2025-09-15 14:00:00'),
-- Order 18 IMP-20251001 CN002 Masan
(40,18,22,500,NULL,24000.00,0.00,12000000.00,'2025-10-01 08:00:00'),
(41,18,14,300,NULL,22000.00,0.00, 6600000.00,'2025-10-01 08:00:00'),
(42,18,15,200,NULL,20000.00,0.00, 4000000.00,'2025-10-01 08:00:00'),
-- Order 19 ORD-20251015 KH003 Gold15
(43,19,22,5,30000.00,NULL,20000.00,130000.00,'2025-10-15 14:00:00'),
(44,19,14,6,26000.00,NULL,15000.00,141000.00,'2025-10-15 14:00:00'),
(45,19,13,1,140000.00,NULL,15000.00,125000.00,'2025-10-15 14:00:00'),
-- Order 20 ORD-20251101 vãng lai
(46,20,16,5,8000.00,NULL,0.00,40000.00,'2025-11-01 09:00:00'),
(47,20,19,3,12000.00,NULL,0.00,36000.00,'2025-11-01 09:00:00'),
-- Order 21 ORD-20251115 KH015 Silver5
(48,21,9, 20, 5000.00,NULL,5000.00, 95000.00,'2025-11-15 15:00:00'),
(49,21,11, 3,55000.00,NULL,5000.00,160000.00,'2025-11-15 15:00:00'),
(50,21,22, 3,30000.00,NULL,5000.00, 85000.00,'2025-11-15 15:00:00'),
-- Order 22 ORD-20251201 KH004 Diamond
(51,22,21,1000,155000.00,NULL,50000.00,155000000.00,'2025-12-01 16:00:00'),
(52,22,18,1000, 89000.00,NULL,50000.00,89000000.00,'2025-12-01 16:00:00'),
(53,22,20,1000, 45000.00,NULL,50000.00,45000000.00,'2025-12-01 16:00:00'),
-- Order 23 ORD-20251215 KH011 Silver5
(54,23,8, 2,228000.00,NULL,10000.00,446000.00,'2025-12-15 15:00:00'),
(55,23,15,5, 25000.00,NULL, 5000.00,120000.00,'2025-12-15 15:00:00'),
-- Order 24 IMP-20260101 CN002 Unilever
(56,24,18, 80,NULL,75000.00,0.00, 6000000.00,'2026-01-01 08:00:00'),
(57,24,20,150,NULL,38000.00,0.00, 5700000.00,'2026-01-01 08:00:00'),
(58,24,21, 60,NULL,130000.00,0.00,7800000.00,'2026-01-01 08:00:00'),
-- Order 25 ORD-20260115 KH002 Sale10K
(59,25,2,5,130000.00,NULL,10000.00,640000.00,'2026-01-15 09:30:00'),
-- Order 26 ORD-20260201 KH003 Gold15 (dùng 100 điểm)
(60,26,8, 2,228000.00,NULL,25000.00,431000.00,'2026-02-01 10:00:00'),
(61,26,13,3,140000.00,NULL,25000.00,395000.00,'2026-02-01 10:00:00'),
-- Order 27 ORD-20260310 vãng lai
(62,27,16,5,8000.00,NULL,0.00,40000.00,'2026-03-10 14:00:00'),
(63,27,15,2,25000.00,NULL,0.00,50000.00,'2026-03-10 14:00:00'),
-- Order 28 IMP-20260401 CN001 Acecook
(64,28,2, 200,NULL,110000.00,0.00,22000000.00,'2026-04-01 08:00:00'),
(65,28,4, 100,NULL, 90000.00,0.00, 9000000.00,'2026-04-01 08:00:00'),
-- Order 29 IMP-20260501 CN002 Vinamilk (PENDING)
(66,29,13,500,NULL,120000.00,0.00,60000000.00,'2026-05-10 09:00:00'),
(67,29,8, 200,NULL,200000.00,0.00,40000000.00,'2026-05-10 09:00:00'),
-- Order 30 TRF-20260601 CN001→CN003
(68,30,2, 50,NULL,NULL,0.00,0.00,'2026-06-01 07:00:00'),
(69,30,4, 20,NULL,NULL,0.00,0.00,'2026-06-01 07:00:00'),
-- Order 31 ORD-20260615 KH004 Diamond (CANCELLED)
(70,31,18,2,89000.00,NULL,20200.00,157800.00,'2026-06-15 11:00:00'),
(71,31,19,2,12000.00,NULL,20200.00,  3800.00,'2026-06-15 11:00:00'),
-- Order 32 RET-20260210 KH003 (trả lốc sữa từ order 26)
(72,32,13,1,140000.00,NULL,0.00,140000.00,'2026-02-10 09:00:00'),
-- Order 33 ORD-20260705 KH014
(73,33,21,1,155000.00,NULL,0.00,155000.00,'2026-07-05 09:30:00'),
-- Order 34 ORD-20260710 KH009 Gold15
(74,34,13,2,140000.00,NULL,25000.00,255000.00,'2026-07-10 14:00:00'),
(75,34,22,5, 30000.00,NULL,10000.00,140000.00,'2026-07-10 14:00:00'),
(76,34,14,3, 26000.00,NULL,15000.00, 63000.00,'2026-07-10 14:00:00'),
-- Order 35 ORD-20260720 KH005 (PENDING)
(77,35,11,2,55000.00,NULL,0.00,110000.00,'2026-07-20 15:00:00');

-- 17. RETURN REQUEST (4 yêu cầu)
INSERT INTO ReturnRequest (id,order_id,branch_id,requested_by,reason,status,reviewed_by,reviewed_at,reject_reason,created_at,updated_at) VALUES
(1,14,2,8,'Lốc sữa Vinamilk bị phình nắp, có mùi lạ','APPROVED',3,'2025-08-16 10:00:00',NULL,'2025-08-16 09:00:00','2025-08-16 10:00:00'),
(2,26,2,8,'Lốc sữa Vinamilk bị hỏng nắp khi nhận hàng','APPROVED',3,'2026-02-09 11:00:00',NULL,'2026-02-09 10:00:00','2026-02-09 11:00:00'),
(3,25,1,7,'Thùng mì bị ướt, bao bì bên trong hỏng','PENDING',NULL,NULL,NULL,'2026-07-25 09:00:00',NULL),
(4,27,1,7,'Bánh Oreo bị mốc trước hạn sử dụng','REJECTED',2,'2026-03-20 14:00:00','Đã quá 7 ngày kể từ khi mua','2026-03-20 13:00:00','2026-03-20 14:00:00');

-- 18. RETURN REQUEST ITEM
INSERT INTO ReturnRequestItem (id,return_request_id,order_detail_id,product_unit_id,quantity,sale_price) VALUES
(1,1,32,13,1,140000.00),
(2,2,61,13,1,140000.00),
(3,3,59, 2,1,130000.00),
(4,4,63,15,1, 25000.00);

-- 19. RETURN REQUEST IMAGE
INSERT INTO ReturnRequestImage (id,return_request_id,image_url) VALUES
(1,1,'/uploads/returns/rr1_sua_phong_nap.jpg'),
(2,1,'/uploads/returns/rr1_sua_hoa_don.jpg'),
(3,2,'/uploads/returns/rr2_sua_vnm_hong.jpg'),
(4,3,'/uploads/returns/rr3_mi_uot_thung.jpg'),
(5,4,'/uploads/returns/rr4_oreo_moc.jpg');

-- 20. CASHBOOK TRANSACTION (28 bản ghi)
INSERT INTO CashbookTransaction (id,branch_id,transaction_type,payment_method,amount,reference_code,description,created_by,created_at,status) VALUES
(1, 1,'IN', 'CASH',   170000.00,'ORD-20250315-001','Thu tiền bán lẻ - KH001',               7,'2025-03-15 09:05:00','COMPLETED'),
(2, 1,'IN', 'CASH',   485000.00,'ORD-20250320-001','Thu tiền bán mì & cà phê - KH002',       7,'2025-03-20 10:35:00','COMPLETED'),
(3, 1,'OUT','BANK', 31000000.00,'IMP-20250301-001','Thanh toán nhập mì Acecook tháng 3/2025', 5,'2025-03-01 08:30:00','COMPLETED'),
(4, 2,'OUT','BANK', 56000000.00,'IMP-20250401-001','Thanh toán nhập sữa VNM & Coca tháng 4', 6,'2025-04-01 08:30:00','COMPLETED'),
(5, 2,'IN', 'BANK',  1559000.00,'ORD-20250415-001','Thu tiền bán bulk - KH004 (Kim Cương)',   8,'2025-04-15 15:05:00','COMPLETED'),
(6, 3,'IN', 'MOMO',   160000.00,'ORD-20250510-001','Thu tiền bán cà phê & nước - KH005',      9,'2025-05-10 11:05:00','COMPLETED'),
(7, 1,'OUT','BANK', 32600000.00,'IMP-20250601-001','Thanh toán nhập Unilever tháng 6/2025',   5,'2025-06-01 08:30:00','COMPLETED'),
(8, 1,'IN', 'CASH',   313000.00,'ORD-20250615-001','Thu tiền bán FMCG - KH006',               7,'2025-06-15 11:35:00','COMPLETED'),
(9, 2,'IN', 'MOMO',   876000.00,'ORD-20250620-001','Thu tiền bán sữa + Coca - KH008',         8,'2025-06-20 14:05:00','COMPLETED'),
(10,1,'OUT','BANK', 55000000.00,'luongnvthang5',    'Thanh toán lương CN001 tháng 5/2025',    2,'2025-06-30 17:00:00','COMPLETED'),
(11,2,'OUT','BANK', 60000000.00,'luongnvthang6',    'Thanh toán lương CN002 tháng 6/2025',    3,'2025-06-30 17:00:00','COMPLETED'),
(12,3,'OUT','BANK', 15500000.00,'IMP-20250701-001','Thanh toán nhập mì Acecook CN003',        4,'2025-07-01 08:30:00','COMPLETED'),
(13,2,'IN', 'BANK',   545000.00,'ORD-20250715-001','Thu tiền bán sữa, cà phê - KH009',        8,'2025-07-15 16:05:00','COMPLETED'),
(14,2,'IN', 'BANK',  1762000.00,'ORD-20250815-001','Thu tiền bán bulk - KH012 (Kim Cương)',   8,'2025-08-15 11:05:00','COMPLETED'),
(15,1,'OUT','BANK',  3000000.00,'tiendienthang8',   'Thanh toán tiền điện CN001 tháng 8/2025',2,'2025-08-05 09:00:00','COMPLETED'),
(16,1,'IN', 'CASH',   90985000.00,'ORD-20250901-001','Thu tiền bán mì & sữa đặc - KH002',       7,'2025-09-01 10:05:00','COMPLETED'),
(17,3,'IN', 'MOMO',   345000.00,'ORD-20250915-001','Thu tiền bán mì & Sunlight - KH013',      9,'2025-09-15 14:05:00','COMPLETED'),
(18,2,'OUT','BANK', 22600000.00,'IMP-20251001-001','Thanh toán nhập Masan tháng 10/2025',     6,'2025-10-01 08:30:00','COMPLETED'),
(19,2,'IN', 'BANK',   396000.00,'ORD-20251015-001','Thu tiền bán gia vị & sữa - KH003',       8,'2025-10-15 14:05:00','COMPLETED'),
(20,1,'IN', 'CASH',    76000.00,'ORD-20251101-001','Thu tiền bán snack & xà phòng',            7,'2025-11-01 09:05:00','COMPLETED'),
(21,1,'IN', 'BANK',  288850000.00,'ORD-20251201-001','Thu tiền bán bulk - KH004 (Kim Cương)',   7,'2025-12-01 16:05:00','COMPLETED'),
(22,1,'OUT','BANK', 55000000.00,'luongnvthang12',   'Thanh toán lương CN001 tháng 12/2025',   2,'2025-12-31 17:00:00','COMPLETED'),
(23,2,'IN', 'BANK',   566000.00,'ORD-20251215-001','Thu tiền bán Coca & bánh - KH011',        8,'2025-12-15 15:05:00','COMPLETED'),
(24,2,'OUT','BANK', 19500000.00,'IMP-20260101-001','Thanh toán nhập Unilever đầu 2026',        6,'2026-01-01 08:30:00','COMPLETED'),
(25,1,'IN', 'CASH',   640000.00,'ORD-20260115-001','Thu tiền bán mì thùng - KH002',            7,'2026-01-15 09:35:00','COMPLETED'),
(26,2,'IN', 'BANK',   806000.00,'ORD-20260201-001','Thu tiền bán Coca & sữa - KH003',          8,'2026-02-01 10:10:00','COMPLETED'),
(27,1,'OUT','BANK', 31000000.00,'IMP-20260401-001','Thanh toán nhập mì Acecook tháng 4/2026',  5,'2026-04-01 08:30:00','COMPLETED'),
(28,2,'OUT','BANK', 35000000.00,'thuematbangcn2t5', 'Thuê mặt bằng CN002 tháng 5/2026',        3,'2026-05-05 09:00:00','COMPLETED');

-- 21. PRODUCT SUPPLIER
INSERT INTO productSupplier (product_id,supplier_id) VALUES
(1,1),(2,1),(3,3),(4,3),(5,3),(6,3),
(7,2),(8,2),(9,3),(10,3),
(11,4),(12,4),(13,4),(14,4),(15,3);

-- 22. INVENTORY SNAPSHOT (30 bản ghi – 2025 Q1→Q4 & 2026 T1→T7)
-- Các bản ghi này đảm bảo báo cáo Nhập/Xuất tồn kho hiển thị đúng số liệu
INSERT INTO InventorySnapshot (id,snapshot_type,snapshot_date,branch_id,product_unit_id,opening_stock,opening_value,stock_in,stock_in_value,stock_out,stock_out_value,closing_stock,closing_value,created_at) VALUES
-- ==== 2025 Q1 (31/3) ====
(1, 'MONTHLY','2025-03-31',1, 2,   0,           0.00, 200, 22000000.00,  25,  2750000.00, 175, 19250000.00,NOW()),
(2, 'MONTHLY','2025-03-31',1, 1,   0,           0.00,6000, 27000000.00,5500, 24750000.00, 500,  2250000.00,NOW()),
(3, 'MONTHLY','2025-03-31',1, 4,   0,           0.00, 100,  9000000.00,  10,   900000.00,  90,  8100000.00,NOW()),
-- ==== 2025 Q2 (30/6) ====
(4, 'MONTHLY','2025-06-30',1, 2, 175, 19250000.00,   0,         0.00,  30,  3300000.00, 145, 15950000.00,NOW()),
(5, 'MONTHLY','2025-06-30',1,18,   0,           0.00, 100,  7500000.00,  15,  1125000.00,  85,  6375000.00,NOW()),
(6, 'MONTHLY','2025-06-30',1,20,   0,           0.00, 200,  7600000.00,  20,   760000.00, 180,  6840000.00,NOW()),
(7, 'MONTHLY','2025-06-30',1,21,   0,           0.00, 100, 13000000.00,   5,   650000.00,  95, 12350000.00,NOW()),
(8, 'MONTHLY','2025-06-30',2,13,   0,           0.00, 300, 36000000.00, 120, 14400000.00, 180, 21600000.00,NOW()),
(9, 'MONTHLY','2025-06-30',2, 8,   0,           0.00, 100, 20000000.00,  50,  9000000.00,  50, 11000000.00,NOW()),
-- ==== 2025 Q3 (30/9) ====
(10,'MONTHLY','2025-09-30',1, 2, 145, 15950000.00,   0,         0.00,  42,  4620000.00, 103, 11330000.00,NOW()),
(11,'MONTHLY','2025-09-30',1,18,  85,  6375000.00,   0,         0.00,  20,  1500000.00,  65,  4875000.00,NOW()),
(12,'MONTHLY','2025-09-30',2,13, 180, 21600000.00,   0,         0.00,  85, 10200000.00,  95, 11400000.00,NOW()),
(13,'MONTHLY','2025-09-30',2, 8,  50, 11000000.00,   0,         0.00,  28,  6300000.00,  22,  4840000.00,NOW()),
(14,'MONTHLY','2025-09-30',3, 2,   0,           0.00, 100, 11000000.00,  30,  3300000.00,  70,  7700000.00,NOW()),
(15,'MONTHLY','2025-09-30',3, 4,   0,           0.00,  50,  4500000.00,  10,   900000.00,  40,  3600000.00,NOW()),
-- ==== 2025 Q4 (31/12) ====
(16,'MONTHLY','2025-12-31',1, 2, 103, 11330000.00,   0,         0.00,  23,  2530000.00,  80,  8800000.00,NOW()),
(17,'MONTHLY','2025-12-31',1,18,  65,  4875000.00,   0,         0.00,  15,  1125000.00,  50,  3750000.00,NOW()),
(18,'MONTHLY','2025-12-31',1,21,  95, 12350000.00,   0,         0.00,  10,  1300000.00,  85, 11050000.00,NOW()),
(19,'MONTHLY','2025-12-31',2,13,  95, 11400000.00,   0,         0.00,  55,  6600000.00,  40,  4800000.00,NOW()),
(20,'MONTHLY','2025-12-31',2, 8,  22,  4840000.00,   0,         0.00,   7,  1575000.00,  15,  3300000.00,NOW()),
(21,'MONTHLY','2025-12-31',3, 2,  70,  7700000.00,   0,         0.00,  40,  4400000.00,  30,  3300000.00,NOW()),
-- ==== 2026 T1 (31/1) ====
(22,'MONTHLY','2026-01-31',1, 2,  80,  8800000.00,   0,         0.00,   5,   550000.00,  75,  8250000.00,NOW()),
(23,'MONTHLY','2026-01-31',2,18,   0,           0.00,  80,  6000000.00,   5,   375000.00,  75,  5625000.00,NOW()),
(24,'MONTHLY','2026-01-31',2,20,   0,           0.00, 150,  5700000.00,   8,   304000.00, 142,  5396000.00,NOW()),
-- ==== 2026 T2 (28/2) ====
(25,'MONTHLY','2026-02-28',1, 2,  75,  8250000.00,   0,         0.00,   5,   550000.00,  70,  7700000.00,NOW()),
(26,'MONTHLY','2026-02-28',2,13,  40,  4800000.00,   0,         0.00,   3,   360000.00,  37,  4440000.00,NOW()),
-- ==== 2026 T4 (30/4) – sau khi nhập ====
(27,'MONTHLY','2026-04-30',1, 2,  70,  7700000.00, 200, 22000000.00,  10,  1100000.00, 260, 28600000.00,NOW()),
(28,'MONTHLY','2026-04-30',1, 4,  45,  4050000.00, 100,  9000000.00,   5,   450000.00, 140, 12600000.00,NOW()),
-- ==== 2026 T6 (30/6) ====
(29,'MONTHLY','2026-06-30',1, 2, 260, 28600000.00,   0,         0.00,  50,  5500000.00, 210, 23100000.00,NOW()),
(30,'MONTHLY','2026-06-30',2,13,  37,  4440000.00,   0,         0.00,   3,   360000.00,  34,  4080000.00,NOW());
