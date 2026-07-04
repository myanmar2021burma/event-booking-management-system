-- Authentication Schema for Event Management System
-- This adds user registration and admin authentication

USE event_management;

-- Users table for customer accounts
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Admins table for admin accounts (pre-created by system)
CREATE TABLE IF NOT EXISTS admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username)
);

-- Update bookings table to link with users
ALTER TABLE bookings 
ADD COLUMN user_id INT,
ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Insert default admin accounts
-- Password: admin123 (you should change these!)
INSERT INTO admins (username, password, full_name, email) VALUES
('admin', 'admin123', 'System Administrator', 'admin@eventmanagement.com'),
('manager', 'manager123', 'Event Manager', 'manager@eventmanagement.com');

-- Create sample user accounts for testing
-- Password: user123
INSERT INTO users (username, email, password, full_name, phone) VALUES
('john_doe', 'john.doe@email.com', 'user123', 'John Doe', '555-0101'),
('jane_smith', 'jane.smith@email.com', 'user123', 'Jane Smith', '555-0102'),
('bob_johnson', 'bob.johnson@email.com', 'user123', 'Bob Johnson', '555-0103');

-- Update existing bookings to link with users
UPDATE bookings SET user_id = 1 WHERE customer_email = 'john.doe@email.com';
UPDATE bookings SET user_id = 2 WHERE customer_email = 'jane.smith@email.com';
UPDATE bookings SET user_id = 3 WHERE customer_email = 'bob.johnson@email.com';

-- Show tables
SHOW TABLES;

-- Display admin accounts
SELECT id, username, full_name, email FROM admins;

-- Display user accounts
SELECT id, username, full_name, email FROM users;
