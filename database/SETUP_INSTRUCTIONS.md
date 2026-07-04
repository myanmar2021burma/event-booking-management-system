# 🗄️ Database Setup Instructions

This guide will help you set up the MySQL database for the Event Management System.

## 📋 Prerequisites

- MySQL Server installed and running
- MySQL command-line client or MySQL Workbench

## 🚀 Quick Setup

### Method 1: Using MySQL Command Line (Recommended)

1. **Open Terminal/Command Prompt**

2. **Login to MySQL:**
   ```bash
   mysql -u root -p
   ```
   Enter your MySQL password when prompted.

3. **Run the schema file:**
   ```sql
   source /full/path/to/event-management-system/database/schema.sql;
   ```
   
   **Example:**
   ```sql
   source /Users/john/projects/event-management-system/database/schema.sql;
   ```

4. **Verify setup:**
   ```sql
   USE event_management;
   SHOW TABLES;
   SELECT COUNT(*) FROM events;
   ```
   
   You should see:
   - Tables: `events` and `bookings`
   - 5 sample events

### Method 2: Using MySQL Workbench

1. Open MySQL Workbench
2. Connect to your MySQL server
3. Click **File** → **Open SQL Script**
4. Select `database/schema.sql`
5. Click the **Execute** button (⚡ icon)
6. Refresh the schemas panel to see `event_management` database

### Method 3: Command Line (One-liner)

```bash
mysql -u root -p < /path/to/event-management-system/database/schema.sql
```

## 📊 Database Schema Overview

### Tables Created

#### 1. `events` Table
Stores information about events.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK, Auto) | Unique event identifier |
| name | VARCHAR(255) | Event name |
| description | TEXT | Event description |
| event_date | DATETIME | When the event occurs |
| location | VARCHAR(255) | Event venue |
| available_tickets | INT | Tickets still available |
| ticket_price | DECIMAL(10,2) | Price per ticket |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Last update time |

#### 2. `bookings` Table
Stores customer bookings.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK, Auto) | Unique booking identifier |
| event_id | INT (FK) | References events.id |
| customer_name | VARCHAR(255) | Customer's name |
| customer_email | VARCHAR(255) | Customer's email |
| number_of_tickets | INT | Tickets booked |
| booking_date | DATETIME | When booking was made |
| total_amount | DECIMAL(10,2) | Total cost |
| status | VARCHAR(50) | CONFIRMED or CANCELLED |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Last update time |

### Relationships

- `bookings.event_id` → `events.id` (Foreign Key)
- Cascade delete: Deleting an event deletes all its bookings

## 🎯 Sample Data

The schema includes 5 sample events:

1. **Tech Conference 2025** - $299.99
2. **Music Festival Summer** - $149.99
3. **Food & Wine Expo** - $89.99
4. **Startup Pitch Night** - $49.99
5. **Art Gallery Opening** - $75.00

And 3 sample bookings to demonstrate the system.

## 🔧 Customization

### Change Database Name

If you want to use a different database name:

1. Edit `schema.sql`:
   ```sql
   CREATE DATABASE IF NOT EXISTS your_database_name;
   USE your_database_name;
   ```

2. Update `DatabaseUtil.java`:
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
   ```

### Add More Sample Data

You can add more events by editing the INSERT statements in `schema.sql`:

```sql
INSERT INTO events (name, description, event_date, location, available_tickets, ticket_price) VALUES
('Your Event Name', 'Description here', '2025-12-31 20:00:00', 'Venue', 100, 50.00);
```

## 🔍 Verification Queries

After setup, run these queries to verify everything is working:

```sql
-- Check database exists
SHOW DATABASES LIKE 'event_management';

-- Use the database
USE event_management;

-- List all tables
SHOW TABLES;

-- Count events
SELECT COUNT(*) as total_events FROM events;

-- View all events
SELECT id, name, event_date, location, available_tickets, ticket_price 
FROM events 
ORDER BY event_date;

-- Count bookings
SELECT COUNT(*) as total_bookings FROM bookings;

-- View all bookings with event names
SELECT 
    b.id,
    e.name as event_name,
    b.customer_name,
    b.customer_email,
    b.number_of_tickets,
    b.total_amount,
    b.status
FROM bookings b
JOIN events e ON b.event_id = e.id;
```

## 🗑️ Reset Database

If you need to start fresh:

```sql
DROP DATABASE IF EXISTS event_management;
```

Then run the schema.sql again.

## 🔐 Security Notes

### For Production Use:

1. **Create a dedicated database user:**
   ```sql
   CREATE USER 'eventapp'@'localhost' IDENTIFIED BY 'strong_password_here';
   GRANT ALL PRIVILEGES ON event_management.* TO 'eventapp'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Update DatabaseUtil.java:**
   ```java
   private static final String DB_USER = "eventapp";
   private static final String DB_PASSWORD = "strong_password_here";
   ```

3. **Never use root user in production!**

### Password Security:

- Don't commit passwords to version control
- Use environment variables for sensitive data
- Consider using a properties file outside the WAR

## 🐛 Troubleshooting

### Error: "Access denied for user 'root'@'localhost'"

**Solution:**
```bash
# Reset MySQL root password
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

### Error: "Can't connect to MySQL server"

**Solution:**
```bash
# Check if MySQL is running
# macOS
brew services list

# Start MySQL
brew services start mysql

# Windows - Check Services panel
# Linux
sudo systemctl status mysql
sudo systemctl start mysql
```

### Error: "Table already exists"

**Solution:**
The schema uses `DROP TABLE IF EXISTS`, so this shouldn't happen.
If it does, manually drop tables:
```sql
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS events;
```

### Error: "Unknown database"

**Solution:**
Make sure you created the database:
```sql
CREATE DATABASE event_management;
USE event_management;
```

## 📝 Backup & Restore

### Create Backup

```bash
mysqldump -u root -p event_management > backup.sql
```

### Restore from Backup

```bash
mysql -u root -p event_management < backup.sql
```

## 🎓 Learning Resources

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [SQL Tutorial](https://www.w3schools.com/sql/)
- [Database Design Basics](https://www.lucidchart.com/pages/database-diagram/database-design)

---

**Need Help?** Check the main README.md troubleshooting section or the QUICKSTART.md guide.
