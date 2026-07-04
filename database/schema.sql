-- Event Management System Database Schema

-- Create database
CREATE DATABASE IF NOT EXISTS event_management;
USE event_management;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS events;

-- Create events table
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    available_tickets INT NOT NULL DEFAULT 0,
    ticket_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_date (event_date),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create bookings table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    number_of_tickets INT NOT NULL,
    booking_date DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_customer_email (customer_email),
    INDEX idx_event_id (event_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample events
INSERT INTO events (name, description, event_date, location, available_tickets, ticket_price) VALUES
('Tech Conference 2025', 'Annual technology conference featuring industry leaders and innovators', '2025-12-15 09:00:00', 'Convention Center, New York', 500, 299.99),
('Music Festival Summer', 'Three-day outdoor music festival with top artists', '2025-07-20 14:00:00', 'Central Park, Los Angeles', 10000, 149.99),
('Food & Wine Expo', 'Culinary experience with renowned chefs and wine tasting', '2025-06-10 11:00:00', 'Grand Hotel, San Francisco', 300, 89.99),
('Startup Pitch Night', 'Watch innovative startups pitch their ideas to investors', '2025-05-25 18:00:00', 'Innovation Hub, Austin', 200, 49.99),
('Art Gallery Opening', 'Contemporary art exhibition opening night', '2025-08-05 19:00:00', 'Modern Art Museum, Chicago', 150, 75.00);

-- Insert sample bookings
INSERT INTO bookings (event_id, customer_name, customer_email, number_of_tickets, booking_date, total_amount, status) VALUES
(1, 'John Doe', 'john.doe@email.com', 2, NOW(), 599.98, 'CONFIRMED'),
(2, 'Jane Smith', 'jane.smith@email.com', 4, NOW(), 599.96, 'CONFIRMED'),
(3, 'Bob Johnson', 'bob.johnson@email.com', 1, NOW(), 89.99, 'CONFIRMED');

-- Update available tickets after sample bookings
UPDATE events SET available_tickets = available_tickets - 2 WHERE id = 1;
UPDATE events SET available_tickets = available_tickets - 4 WHERE id = 2;
UPDATE events SET available_tickets = available_tickets - 1 WHERE id = 3;

-- Display created tables
SHOW TABLES;

-- Display sample data
SELECT * FROM events;
SELECT * FROM bookings;
