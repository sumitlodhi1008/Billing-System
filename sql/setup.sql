-- Shop Billing System Database Setup
-- Run this script in MySQL before launching the application

CREATE DATABASE IF NOT EXISTS shop_billing;
USE shop_billing;

-- Product table
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL
);

-- Bill table
CREATE TABLE IF NOT EXISTS bill (
    id INT PRIMARY KEY AUTO_INCREMENT,
     customer_name VARCHAR(255) NOT NULL DEFAULT 'Walk-in Customer',
    total_amount DOUBLE NOT NULL,
    gst_amount DOUBLE NOT NULL,
    grand_total DOUBLE NOT NULL,
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bill items table (stores item-wise details per bill)
CREATE TABLE IF NOT EXISTS bill_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    item_total DOUBLE NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bill(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Sample product data for testing
INSERT INTO product (name, price, quantity) VALUES
('Rice (1kg)', 55.00, 100),
('Wheat Flour (1kg)', 40.00, 80),
('Sugar (1kg)', 45.00, 60),
('Salt (1kg)', 20.00, 150),
('Cooking Oil (1L)', 130.00, 50),
('Tea Powder (250g)', 95.00, 40),
('Coffee Powder (200g)', 180.00, 30),
('Milk (1L)', 52.00, 200),
('Butter (500g)', 250.00, 25),
('Bread', 35.00, 50);
