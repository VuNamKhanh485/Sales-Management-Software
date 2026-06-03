CREATE DATABASE IF NOT EXISTS sale_management;
USE sale_management;

-- role
CREATE TABLE Role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL

    -- created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP -- không có create at
);

-- branch
CREATE TABLE Branch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    branch_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    phone VARCHAR(15) NULL,
    email VARCHAR(255) NULL,
    address VARCHAR(255) NOT NULL,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    opened_at DATE NULL,
    closed_at DATE NULL,

    note TEXT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT chk_branch_date
        CHECK (closed_at IS NULL OR opened_at IS NULL OR closed_at >= opened_at)
);

-- employee
CREATE TABLE Employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    branch_id BIGINT NULL,
    role_id BIGINT NOT NULL,
    manager_id BIGINT NULL,
    created_by BIGINT NULL,

    employee_code VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    phone VARCHAR(15) NULL,
    address VARCHAR(255) NULL,
    gender ENUM('MALE','FEMALE','OTHER') NULL,
    dob DATE NULL,

    hired_date DATE NOT NULL,
    base_salary DECIMAL(12,2) NULL,

    work_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    note TEXT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_employee_branch
        FOREIGN KEY (branch_id)
        REFERENCES Branch(id),

    CONSTRAINT fk_employee_role
        FOREIGN KEY (role_id)
        REFERENCES Role(id),

    CONSTRAINT fk_employee_manager
        FOREIGN KEY (manager_id)
        REFERENCES Employee(id),

    CONSTRAINT fk_employee_created_by
        FOREIGN KEY (created_by)
        REFERENCES Employee(id),

    CONSTRAINT chk_employee_salary
        CHECK (base_salary IS NULL OR base_salary >= 0)
);

-- customer_rank
CREATE TABLE CustomerRank (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL UNIQUE,

    discount_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    condition_total_revenue DECIMAL(15,2) NOT NULL DEFAULT 0,

    description VARCHAR(255) NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT chk_customer_rank_discount
        CHECK (discount_rate >= 0 AND discount_rate <= 100),

    CONSTRAINT chk_customer_rank_revenue
        CHECK (condition_total_revenue >= 0)
);

-- customer
CREATE TABLE Customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    customer_rank_id BIGINT NULL,

    created_by BIGINT NULL,
    updated_by BIGINT NULL,

    customer_code VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,

    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(255) NULL,

    gender ENUM('MALE','FEMALE','OTHER') NULL,
    dob DATE NULL,
    address VARCHAR(255) NULL,

    total_point INT NOT NULL DEFAULT 0,
    used_point INT NOT NULL DEFAULT 0,

    total_revenue DECIMAL(15,2) NOT NULL DEFAULT 0,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    note TEXT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_customer_rank
        FOREIGN KEY (customer_rank_id)
        REFERENCES CustomerRank(id),

    CONSTRAINT fk_customer_created_by
        FOREIGN KEY (created_by)
        REFERENCES Employee(id),

    CONSTRAINT fk_customer_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES Employee(id),

    CONSTRAINT chk_customer_point
        CHECK (
            total_point >= 0
            AND used_point >= 0
            AND total_point >= used_point
        ),

    CONSTRAINT chk_customer_revenue
        CHECK (total_revenue >= 0)
);

-- brand
CREATE TABLE Brand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL UNIQUE,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- category
CREATE TABLE Category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NULL,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- unit
CREATE TABLE Unit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL UNIQUE,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- supplier
CREATE TABLE Supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    supplier_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    phone VARCHAR(15) NULL,
    email VARCHAR(255) NULL,
    address VARCHAR(255) NULL,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    note TEXT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- payment_method
CREATE TABLE PaymentMethod (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL UNIQUE,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL
);

-- voucher
CREATE TABLE Voucher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    discount_type ENUM('PERCENT','AMOUNT') NOT NULL,
    discount_value DECIMAL(12,2) NOT NULL DEFAULT 0,

    min_order_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(12,2) NULL,

    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT chk_voucher_discount
        CHECK (
            discount_value >= 0
            AND (
                discount_type = 'AMOUNT'
                OR discount_value <= 100
            )
        ),

    CONSTRAINT chk_voucher_min_order
        CHECK (min_order_amount >= 0),

    CONSTRAINT chk_voucher_max_discount
        CHECK (max_discount_amount IS NULL OR max_discount_amount >= 0),

    CONSTRAINT chk_voucher_time
        CHECK (end_at >= start_at)
);

-- product
CREATE TABLE Product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    category_id BIGINT NOT NULL,
    brand_id BIGINT NULL,

    name VARCHAR(255) NOT NULL,

    image_url VARCHAR(1000) NULL,

    description TEXT NULL,
    note TEXT NULL,

    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES Category(id),

    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)
        REFERENCES Brand(id)
);

-- product_unit
CREATE TABLE ProductUnit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    product_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,

    sku VARCHAR(100) NOT NULL UNIQUE,
    barcode_unit VARCHAR(255) UNIQUE,

    conversion_value INT NOT NULL DEFAULT 1,

	price DECIMAL(12,2) NOT NULL DEFAULT 0,

    is_base_unit BOOLEAN NOT NULL DEFAULT FALSE,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_product_unit_product
        FOREIGN KEY (product_id)
        REFERENCES Product(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_product_unit_unit
        FOREIGN KEY (unit_id)
        REFERENCES Unit(id),

    CONSTRAINT uq_product_unit
        UNIQUE (product_id, unit_id),

    CONSTRAINT chk_product_unit_conversion
        CHECK (conversion_value > 0),

    CONSTRAINT chk_product_unit_price
        CHECK (
            sale_price >= 0
            AND import_price >= 0
        )
);

-- inventory
CREATE TABLE Inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    branch_id BIGINT NOT NULL,
    product_unit_id BIGINT NOT NULL,

    stock INT NOT NULL DEFAULT 0,
    min_stock INT NOT NULL DEFAULT 0,
    max_stock INT NULL,

    position_in_shop VARCHAR(255) NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_inventory_branch
        FOREIGN KEY (branch_id)
        REFERENCES Branch(id),

    CONSTRAINT fk_inventory_product_unit
        FOREIGN KEY (product_unit_id)
        REFERENCES ProductUnit(id),

    CONSTRAINT uq_inventory_branch_product_unit
        UNIQUE (branch_id, product_unit_id),

    CONSTRAINT chk_inventory_stock
        CHECK (
            stock >= 0
            AND min_stock >= 0
        ),

    CONSTRAINT chk_inventory_max_stock
        CHECK (max_stock IS NULL OR max_stock >= min_stock)
);

-- order_transaction
CREATE TABLE OrderTransaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    branch_id BIGINT NOT NULL,
    customer_id BIGINT NULL,
    voucher_id BIGINT NULL,
    supplier_id BIGINT NULL,
    payment_method_id BIGINT NULL,
    created_by BIGINT NOT NULL,

    -- original_order_id BIGINT NULL, -- ? trường này chỉ cần khi transaction là bảng theo dõi bảng order còn mình gộp lại rồi nên không có

    code VARCHAR(100) NOT NULL UNIQUE,

    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(12,2) NOT NULL DEFAULT 0,

    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    change_amount DECIMAL(12,2) NOT NULL DEFAULT 0,

    status ENUM(
        'PENDING',
        'COMPLETED',
        'CANCELLED',
        'DELIVERY',
        'RECEIVED',
         'REFUNDED', -- trả hàng đối với customer bằng cách tạo giao dịch nhập hàng nếu để refunded ở đây thì chỉ dành cho lúc import từ nhà cung cấp 
         'PARTIALLY_REFUNDED' 
    ) NOT NULL DEFAULT 'PENDING',

    transaction_type ENUM(
        'SALE',
        'RETURN',
        'IMPORT',
        'EXPORT',
        -- 'TRANSFER', -- không cần transfer, khi Import và export  mà có form_branch và to_branch rồi thì là transfer luôn rồi
        'OTHER' -- thiếu other để phục vụ mua với mục đích khác
    ) NOT NULL DEFAULT 'SALE',

    from_branch_id BIGINT NULL,
    to_branch_id BIGINT NULL,

    note TEXT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,

    CONSTRAINT fk_order_transaction_branch
        FOREIGN KEY (branch_id)
        REFERENCES Branch(id),

    CONSTRAINT fk_order_transaction_customer
        FOREIGN KEY (customer_id)
        REFERENCES Customer(id),

    CONSTRAINT fk_order_transaction_voucher
        FOREIGN KEY (voucher_id)
        REFERENCES Voucher(id),

    CONSTRAINT fk_order_transaction_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES Supplier(id),

    CONSTRAINT fk_order_transaction_payment_method
        FOREIGN KEY (payment_method_id)
        REFERENCES PaymentMethod(id),

    CONSTRAINT fk_order_transaction_created_by
        FOREIGN KEY (created_by)
        REFERENCES Employee(id),

    CONSTRAINT fk_order_transaction_original_order
        FOREIGN KEY (original_order_id)
        REFERENCES OrderTransaction(id),

    CONSTRAINT fk_order_transaction_from_branch
        FOREIGN KEY (from_branch_id)
        REFERENCES Branch(id),

    CONSTRAINT fk_order_transaction_to_branch
        FOREIGN KEY (to_branch_id)
        REFERENCES Branch(id),

    CONSTRAINT chk_order_amount
        CHECK (
            total_amount >= 0
            AND discount_amount >= 0
            AND final_amount >= 0
            AND final_amount <= total_amount
        ),

    CONSTRAINT chk_order_paid
        CHECK (
            paid_amount >= 0
            AND change_amount >= 0
        )
);

-- order_transaction_detail
CREATE TABLE OrderTransactionDetail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    order_transaction_id BIGINT NOT NULL,
    product_unit_id BIGINT NOT NULL,

    quantity INT NOT NULL,

    sale_price DECIMAL(12,2) NULL DEFAULT 0, -- được null 1 trong 2 vì giao dịch nhập hàng thì không có sals và ngược lại
    import_price DECIMAL(12,2) NULL DEFAULT 0,

    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0, 
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_detail_order
        FOREIGN KEY (order_transaction_id)
        REFERENCES OrderTransaction(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_detail_product_unit
        FOREIGN KEY (product_unit_id)
        REFERENCES ProductUnit(id),

    CONSTRAINT chk_order_detail_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_order_detail_amount
        CHECK (
            sale_price >= 0
            AND import_price >= 0
            AND discount_amount >= 0
            AND total_amount >= 0
        )
);
