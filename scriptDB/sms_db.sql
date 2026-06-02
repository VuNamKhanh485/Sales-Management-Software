create database SMS_DB
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE SMS_DB;

CREATE TABLE Brand (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
    created_at datetime
);

CREATE TABLE Category (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
	description TEXT not null,
	status ENUM('ACTIVE','INACTIVE') not null,
	created_at datetime not null,
	updated_at datetime
);

CREATE TABLE ProductImage (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT not null,
    image_url varchar(1000) not null,
    is_thumbnail boolean,
    created_at datetime not null
);

CREATE TABLE Product (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT,
	brand_id BIGINT,
	name VARCHAR(255) not null,
	description TEXT not null,
    status ENUM('ACTIVE','INACTIVE') not null,
    note VARCHAR(255) not null,
	created_at datetime not null,
	updated_at datetime  
);

CREATE TABLE Unit (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
    created_at datetime
);

CREATE TABLE ProductUnit(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT, 
	unit_id BIGINT, 
    convention_value INT not null,
    price dec(10,2) not null,
    barcode_unit varchar(255) not null,
    is_base_unit boolean,
    sku varchar(255) not null
);

CREATE TABLE OrderItem (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
	product_unit_id BIGINT,
    supplier_id BIGINT,
	quantity Int not null,
    unit_price dec(10,2) not null,
    discount_amount dec(10,2),
    subtotal dec(10,2) not null
);

CREATE TABLE OrderTransaction(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    branch_id BIGINT,
	customer_id BIGINT,
	voucher_id	BIGINT,
    payment_method_id BIGINT,
	code VARCHAR(255) not null,
	total_amount dec(10,2) not null,
	discount_amount dec(10,2),
	final_amount dec(10,2) not null,
	status ENUM('DELIVERY','RECEIVED'),
	Transaction_type ENUM('EXPORT','SALE','IMPORT','OTHER'),
    From_branch_id BIGINT,
	To_branch_id BIGINT,
    create_by BIGINT,
	created_at datetime not null,
    updated_at datetime
);

CREATE TABLE Supplier (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
    phone VARCHAR(15),
    address VARCHAR(255) not null
);

CREATE TABLE PaymentMethod(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	code VARCHAR(255) not null,
	name VARCHAR(255) not null,
    status ENUM('ACTIVE','INACTIVE') not null,
    created_at datetime not null
);

CREATE TABLE Branch(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
	address VARCHAR(255) not null,
    status ENUM('ACTIVE','INACTIVE') not null,
    manager_id BIGINT,
    created_at datetime not null,
    update_at datetime
);

CREATE TABLE Employee(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    branch_id BIGINT,
    role_id BIGINT,
    manager_id BIGINT,
    created_by BIGINT,
	employee_code VARCHAR(255) not null,
	fullname VARCHAR(255) not null,
    address VARCHAR(255) not null,
    gender ENUM('MALE','FEMALE','OTHER') not null,
    dob date not null,
    hired_date date not null,
    base_salary dec(10,2) not null,
    work_status ENUM('ACTIVE','INACTIVE'),
    note TEXT,
    email varchar(255) not null,
    password varchar(255) not null,
    create_at datetime not null,
    updated_at datetime
);

CREATE TABLE LogHistory (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT,
    action ENUM('LOGIN','LOGOUT','LOGIN_FAIL','FORGET_PASSWORD') not null,
    time datetime
);

CREATE TABLE Role(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(255) not null,
	name VARCHAR(255) not null,
    description TEXT not null
);

CREATE TABLE Inventory (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    branch_id BIGINT,
    product_unit_id BIGINT,
    stock INT not null,
    min_stock int not null,
    max_stock int not null,
    position_in_shop VARCHAR(255)
);

CREATE TABLE Voucher(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	code VARCHAR(255) not null,
	name_voucher VARCHAR(255) not null,
	discount_type VARCHAR(255) not null,
	discount_value dec(10,2),
	min_order_value dec(10,2),
	max_discount_amount dec(10,2),
	usage_limit int,
	used_count int,
	start_date date not null,
	end_date date not null,
	status ENUM('ACTIVE','INACTIVE'),
	created_at datetime not null,
    created_by BIGINT
);

CREATE TABLE Customer(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_rank_id BIGINT,
	customer_code VARCHAR(255) not null,
	full_name VARCHAR(255) not null,
	phone VARCHAR(15) not null,
	email VARCHAR(255),
	total_point int,
	used_point int,
	total_revenue int not null,
	created_at datetime not null,
	updated_at datetime
);

CREATE TABLE CustomerRank(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) not null,
	discount_rate dec(10,2),
	condition_total_revenue dec(15,2),
	description VARCHAR(255) not null
);

CREATE TABLE PointHistory(
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
	order_id BIGINT,
	point_earned int not null,
	created_at datetime not null
);

ALTER TABLE ProductImage
ADD CONSTRAINT 	fk_product_productImage
	FOREIGN KEY (product_id)
REFERENCES Product(id)
ON DELETE CASCADE;

ALTER TABLE Product
ADD CONSTRAINT 	fk_brand_product
	FOREIGN KEY (brand_id)
REFERENCES Brand(id),

ADD CONSTRAINT 	fk_category_product
	FOREIGN KEY (category_id)
REFERENCES Category(id);

ALTER TABLE ProductUnit
ADD CONSTRAINT 	fk_product_product_unit
	FOREIGN KEY (product_id)
REFERENCES Product(id),

ADD CONSTRAINT 	fk_unit_product_unit
	FOREIGN KEY (unit_id)
REFERENCES Unit(id);

ALTER TABLE OrderItem
ADD CONSTRAINT 	fk_order_orderItem
	FOREIGN KEY (order_id)
REFERENCES OrderTransaction(id),

ADD CONSTRAINT 	fk_supplier_orderItem
	FOREIGN KEY (supplier_id)
REFERENCES Supplier(id),

ADD CONSTRAINT 	fk_product_unit_orderItem
	FOREIGN KEY (product_unit_id)
REFERENCES ProductUnit(id);

ALTER TABLE OrderTransaction
ADD CONSTRAINT 	fk_branch_order_transaction
	FOREIGN KEY (branch_id)
REFERENCES Branch(id),

ADD CONSTRAINT 	fk_customer_order_transaction
	FOREIGN KEY (customer_id)
REFERENCES Customer(id),

ADD CONSTRAINT 	fk_voucher_order_transaction
	FOREIGN KEY (voucher_id)
REFERENCES Voucher(id),

ADD CONSTRAINT 	fk_payment_method_order_transaction
	FOREIGN KEY (payment_method_id)
REFERENCES PaymentMethod(id),

ADD CONSTRAINT 	fk_From_branch
	FOREIGN KEY (From_branch_id)
REFERENCES Branch(id), 

ADD CONSTRAINT 	fk_To_branch
	FOREIGN KEY (To_branch_id)
REFERENCES Branch(id),

ADD CONSTRAINT 	fk_employee_order_transaction
	FOREIGN KEY (create_by)
REFERENCES Employee(id);

ALTER TABLE Branch
ADD CONSTRAINT 	fk_manager_branch
	FOREIGN KEY (manager_id)
REFERENCES Employee(id);

ALTER TABLE Employee
ADD CONSTRAINT 	fk_branch_employee
	FOREIGN KEY (branch_id)
REFERENCES Branch(id),

ADD CONSTRAINT 	fk_role_employee
	FOREIGN KEY (role_id)
REFERENCES Role(id),

ADD CONSTRAINT 	fk_manager_employee
	FOREIGN KEY (manager_id)
REFERENCES Employee(id),

ADD CONSTRAINT 	fk_created_by_employee
	FOREIGN KEY (created_by)
REFERENCES Employee(id);

ALTER TABLE LogHistory
ADD CONSTRAINT 	fk_employee_loghistory
	FOREIGN KEY (employee_id)
REFERENCES Employee(id);

ALTER TABLE Inventory
ADD CONSTRAINT 	fk_branch_inventory
	FOREIGN KEY (branch_id)
REFERENCES Branch(id),

ADD CONSTRAINT 	fk_product_unit_inventory
	FOREIGN KEY (product_unit_id)
REFERENCES ProductUnit(id);

ALTER TABLE Voucher
ADD CONSTRAINT 	fk_created_by_voucher
	FOREIGN KEY (created_by)
REFERENCES Employee(id)
ON DELETE SET NULL;

ALTER TABLE Customer
ADD CONSTRAINT 	fk_customer_rank_customer
	FOREIGN KEY (customer_rank_id)
REFERENCES CustomerRank(id);

ALTER TABLE PointHistory
ADD CONSTRAINT 	fk_customer_point_history
	FOREIGN KEY (customer_id)
REFERENCES Customer(id),

ADD CONSTRAINT 	fk_order_point_history
	FOREIGN KEY (order_id)
REFERENCES OrderTransaction(id);
