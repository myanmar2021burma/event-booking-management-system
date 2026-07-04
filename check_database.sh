#!/bin/bash

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}=== Database Connection & Data Check ===${NC}"
echo ""

# Get MySQL password
echo "Enter your MySQL root password:"
read -s MYSQL_PASSWORD
echo ""

# Test connection
echo -e "${YELLOW}1. Testing MySQL connection...${NC}"
if mysql -u root -p"$MYSQL_PASSWORD" -e "SELECT 1;" &> /dev/null; then
    echo -e "${GREEN}✓ MySQL connection successful${NC}"
else
    echo -e "${RED}✗ MySQL connection failed${NC}"
    exit 1
fi

# Check if database exists
echo ""
echo -e "${YELLOW}2. Checking if event_management database exists...${NC}"
DB_EXISTS=$(mysql -u root -p"$MYSQL_PASSWORD" -e "SHOW DATABASES LIKE 'event_management';" 2>/dev/null | grep -c "event_management")

if [ $DB_EXISTS -eq 0 ]; then
    echo -e "${RED}✗ Database 'event_management' does not exist${NC}"
    echo "Run: mysql -u root -p < database/schema.sql"
    exit 1
else
    echo -e "${GREEN}✓ Database 'event_management' exists${NC}"
fi

# Show current data
echo ""
echo -e "${BLUE}=== Current Database Data ===${NC}"
echo ""

echo -e "${YELLOW}Events in database:${NC}"
mysql -u root -p"$MYSQL_PASSWORD" -D event_management -e "SELECT id, name, venue, available_seats, price FROM events;"
echo ""

echo -e "${YELLOW}Bookings in database:${NC}"
mysql -u root -p"$MYSQL_PASSWORD" -D event_management -e "SELECT b.id, b.email, e.name as event_name, b.number_of_tickets, b.total_amount, b.status FROM bookings b JOIN events e ON b.event_id = e.id ORDER BY b.booking_date DESC LIMIT 10;"
echo ""

# Real-time monitoring
echo -e "${BLUE}=== Real-Time Monitoring ===${NC}"
echo "This will show new bookings as they happen (Press Ctrl+C to stop)"
echo ""

while true; do
    LATEST=$(mysql -u root -p"$MYSQL_PASSWORD" -D event_management -e "SELECT COUNT(*) FROM bookings;" 2>/dev/null | tail -1)
    echo -ne "\rTotal Bookings: $LATEST  (Last checked: $(date '+%H:%M:%S'))"
    sleep 2
done
