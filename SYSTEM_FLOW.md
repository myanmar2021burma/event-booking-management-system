# 🔄 System Flow Diagrams

This document shows how data flows through the Event Management System with visual diagrams.

---

## 📊 Complete System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         WEB BROWSER                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  HTML (Structure) + CSS (Style) + JavaScript (Behavior)   │  │
│  │  - index.html                                             │  │
│  │  - style.css                                              │  │
│  │  - app.js                                                 │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           │ HTTP Requests (JSON)
                           │ GET, POST, PUT, DELETE
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                    TOMCAT WEB SERVER                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    CORS Filter                            │  │
│  │  (Allows cross-origin requests)                           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           ↓                                      │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              SERVLET LAYER (Controllers)                  │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │           BaseServlet (Parent Class)                │  │  │
│  │  │  - sendJsonResponse()                               │  │  │
│  │  │  - parseJsonRequest()                               │  │  │
│  │  │  - sendErrorResponse()                              │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  │           ↓                              ↓                   │  │
│  │  ┌──────────────────┐         ┌──────────────────┐          │  │
│  │  │  EventServlet    │         │ BookingServlet   │          │  │
│  │  │  /api/events/*   │         │ /api/bookings/*  │          │  │
│  │  │  - doGet()       │         │ - doGet()        │          │  │
│  │  │  - doPost()      │         │ - doPost()       │          │  │
│  │  │  - doPut()       │         │ - doPut()        │          │  │
│  │  │  - doDelete()    │         │ - doDelete()     │          │  │
│  │  └──────────────────┘         └──────────────────┘          │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           │ Method Calls
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                      DAO LAYER (Data Access)                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  EventDAO (Interface)         BookingDAO (Interface)      │  │
│  │  - getAllEvents()             - getAllBookings()          │  │
│  │  - getEventById()             - getBookingById()          │  │
│  │  - addEvent()                 - createBooking()           │  │
│  │  - updateEvent()              - updateBooking()           │  │
│  │  - deleteEvent()              - cancelBooking()           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           ↓                                      │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  EventDAOImpl                 BookingDAOImpl              │  │
│  │  (Concrete Implementation)    (Concrete Implementation)   │  │
│  │  - SQL Queries                - SQL Queries               │  │
│  │  - ResultSet Processing       - ResultSet Processing      │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           │ JDBC Connection
                           │ SQL Queries
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                    DATABASE UTILITY                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  DatabaseUtil                                             │  │
│  │  - getConnection()                                        │  │
│  │  - closeConnection()                                      │  │
│  │  - MySQL JDBC Driver                                      │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           │ TCP/IP Connection
                           │ Port 3306
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                       MYSQL DATABASE                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Database: event_management                               │  │
│  │  ┌─────────────────────┐    ┌─────────────────────┐      │  │
│  │  │   events table      │    │  bookings table     │      │  │
│  │  │  - id (PK)          │    │  - id (PK)          │      │  │
│  │  │  - name             │    │  - event_id (FK)    │      │  │
│  │  │  - description      │    │  - customer_name    │      │  │
│  │  │  - event_date       │    │  - customer_email   │      │  │
│  │  │  - location         │    │  - num_tickets      │      │  │
│  │  │  - avail_tickets    │    │  - total_amount     │      │  │
│  │  │  - ticket_price     │    │  - status           │      │  │
│  │  └─────────────────────┘    └─────────────────────┘      │  │
│  │              │                         │                   │  │
│  │              └─────────────────────────┘                   │  │
│  │                   Foreign Key Relationship                 │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎫 Flow 1: Booking a Ticket

### User Journey
```
User Action → Frontend → Backend → Database → Response
```

### Detailed Flow

```
┌──────────────────────────────────────────────────────────────┐
│ STEP 1: User Clicks "Book Now"                               │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 2: JavaScript Opens Modal                               │
│ File: app.js → openBookingModal(eventId)                     │
│ - Finds event in allEvents array                             │
│ - Displays event details                                     │
│ - Shows booking form                                         │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 3: User Fills Form and Submits                          │
│ - Customer name                                              │
│ - Customer email                                             │
│ - Number of tickets                                          │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 4: JavaScript Sends POST Request                        │
│ File: app.js → booking form submit handler                   │
│                                                              │
│ POST /api/bookings/                                          │
│ Content-Type: application/json                               │
│ Body: {                                                      │
│   eventId: 1,                                               │
│   customerName: "John Doe",                                 │
│   customerEmail: "john@example.com",                        │
│   numberOfTickets: 2                                        │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 5: Request Hits BookingServlet                          │
│ File: BookingServlet.java → doPost()                         │
│                                                              │
│ 1. Parse JSON request → Booking object                      │
│ 2. Validate booking data                                    │
│ 3. Check if event exists (EventDAO.getEventById)            │
│ 4. Check ticket availability                                │
│ 5. Calculate total amount                                   │
│ 6. Set booking date and status                              │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 6: Create Booking in Database                           │
│ File: BookingDAOImpl.java → createBooking()                  │
│                                                              │
│ SQL: INSERT INTO bookings                                    │
│      (event_id, customer_name, customer_email,              │
│       number_of_tickets, booking_date,                      │
│       total_amount, status)                                 │
│      VALUES (?, ?, ?, ?, ?, ?, ?)                           │
│                                                              │
│ Returns: Generated booking ID                                │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 7: Update Available Tickets                             │
│ File: EventDAOImpl.java → updateAvailableTickets()           │
│                                                              │
│ SQL: UPDATE events                                           │
│      SET available_tickets = available_tickets - ?          │
│      WHERE id = ? AND available_tickets >= ?                │
│                                                              │
│ This ensures we don't oversell tickets                      │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 8: Send Success Response                                │
│ File: BaseServlet.java → sendJsonResponse()                  │
│                                                              │
│ HTTP 201 Created                                             │
│ Body: {                                                      │
│   id: 123,                                                  │
│   eventId: 1,                                               │
│   customerName: "John Doe",                                 │
│   customerEmail: "john@example.com",                        │
│   numberOfTickets: 2,                                       │
│   totalAmount: 599.98,                                      │
│   status: "CONFIRMED"                                       │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 9: JavaScript Handles Response                          │
│ File: app.js → booking form submit handler                   │
│                                                              │
│ - Show success alert with booking ID                        │
│ - Close modal                                               │
│ - Reload events to show updated ticket count               │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 10: User Sees Confirmation                              │
│ "Booking successful! Your booking ID is: 123"                │
└──────────────────────────────────────────────────────────────┘
```

---

## 📅 Flow 2: Loading Events

```
┌──────────────────────────────────────────────────────────────┐
│ STEP 1: Page Loads                                           │
│ File: index.html                                             │
│ - DOM Content Loaded event fires                            │
│ - JavaScript initializes                                    │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 2: Call loadEvents()                                    │
│ File: app.js → loadEvents()                                  │
│                                                              │
│ GET /api/events/                                             │
│ Accept: application/json                                     │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 3: EventServlet Handles Request                         │
│ File: EventServlet.java → doGet()                            │
│                                                              │
│ - Check path (no ID = get all events)                       │
│ - Call EventDAO.getAllEvents()                              │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 4: Query Database                                       │
│ File: EventDAOImpl.java → getAllEvents()                     │
│                                                              │
│ SQL: SELECT * FROM events                                    │
│      WHERE event_date >= CURRENT_DATE                       │
│      ORDER BY event_date ASC                                │
│                                                              │
│ - Execute query                                             │
│ - Process ResultSet                                         │
│ - Create Event objects                                      │
│ - Add to List<Event>                                        │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 5: Convert to JSON and Send                             │
│ File: BaseServlet.java → sendJsonResponse()                  │
│                                                              │
│ - Use Gson to convert List<Event> to JSON                   │
│ - Set Content-Type: application/json                        │
│ - Send response                                             │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 6: JavaScript Receives Data                             │
│ File: app.js → loadEvents()                                  │
│                                                              │
│ - Parse JSON response                                       │
│ - Store in allEvents array                                  │
│ - Call displayEvents(events)                                │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 7: Render Events on Page                                │
│ File: app.js → displayEvents()                               │
│                                                              │
│ - Loop through events                                       │
│ - Create HTML for each event card                           │
│ - Insert into DOM (events-grid)                             │
│ - Attach event listeners                                    │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ STEP 8: User Sees Events                                     │
│ - Event cards displayed in grid                             │
│ - Can click "Book Now" buttons                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔍 Flow 3: Searching Bookings

```
User enters email → Search → Query database → Display results
```

### Detailed Steps

```
1. User enters email in search box
   ↓
2. Clicks "Search" button
   ↓
3. JavaScript: searchBookings() function
   ↓
4. GET /api/bookings/?email=john@example.com
   ↓
5. BookingServlet.doGet() receives request
   ↓
6. Extracts email parameter
   ↓
7. Calls BookingDAO.getBookingsByCustomerEmail(email)
   ↓
8. SQL: SELECT * FROM bookings WHERE customer_email = ?
   ↓
9. Returns List<Booking>
   ↓
10. Convert to JSON and send response
    ↓
11. JavaScript receives bookings
    ↓
12. Display bookings in list
    ↓
13. User sees their bookings
```

---

## ➕ Flow 4: Creating an Event (Admin)

```
Admin fills form → Submit → Validate → Insert → Confirm
```

### Detailed Steps

```
1. Admin goes to Admin panel
   ↓
2. Fills event creation form
   - Name, description, date, location, tickets, price
   ↓
3. Submits form
   ↓
4. JavaScript: event form submit handler
   ↓
5. POST /api/events/ with event data
   ↓
6. EventServlet.doPost() receives request
   ↓
7. Parse JSON to Event object
   ↓
8. Validate event data
   ↓
9. Call EventDAO.addEvent(event)
   ↓
10. SQL: INSERT INTO events (name, description, ...)
          VALUES (?, ?, ...)
    ↓
11. Get generated ID
    ↓
12. Return success with event data
    ↓
13. JavaScript shows success message
    ↓
14. Reload events list
    ↓
15. Admin sees new event
```

---

## ❌ Flow 5: Cancelling a Booking

```
Find booking → Click cancel → Confirm → Update status → Restore tickets
```

### Detailed Steps

```
1. User searches for bookings by email
   ↓
2. Finds booking to cancel
   ↓
3. Clicks "Cancel Booking" button
   ↓
4. Confirms cancellation in dialog
   ↓
5. JavaScript: cancelBooking(bookingId)
   ↓
6. DELETE /api/bookings/{id}
   ↓
7. BookingServlet.doDelete() receives request
   ↓
8. Get booking by ID
   ↓
9. Check if already cancelled
   ↓
10. Update booking status to CANCELLED
    ↓
11. SQL: UPDATE bookings SET status = 'CANCELLED'
          WHERE id = ?
    ↓
12. Restore tickets to event
    ↓
13. SQL: UPDATE events
          SET available_tickets = available_tickets + ?
          WHERE id = ?
    ↓
14. Send success response (204 No Content)
    ↓
15. JavaScript shows success message
    ↓
16. Reload bookings and events
    ↓
17. User sees updated status
```

---

## 🔄 Data Flow Summary

### Request Flow
```
Browser → Servlet → DAO → Database
```

### Response Flow
```
Database → DAO → Servlet → Browser
```

### Complete Round Trip
```
User Action
    ↓
JavaScript (app.js)
    ↓
HTTP Request (JSON)
    ↓
Servlet (EventServlet/BookingServlet)
    ↓
DAO Interface (EventDAO/BookingDAO)
    ↓
DAO Implementation (EventDAOImpl/BookingDAOImpl)
    ↓
Database Utility (DatabaseUtil)
    ↓
MySQL Database
    ↓
ResultSet
    ↓
Model Objects (Event/Booking)
    ↓
JSON Response
    ↓
JavaScript Processing
    ↓
DOM Update
    ↓
User Sees Result
```

---

## 🎯 Key Concepts in Action

### Encapsulation
```
Event object has private fields
    ↓
Only accessible through getters/setters
    ↓
Data is protected and validated
```

### Inheritance
```
BaseServlet (parent)
    ↓
EventServlet extends BaseServlet
    ↓
BookingServlet extends BaseServlet
    ↓
Both inherit common methods
```

### Polymorphism
```
EventDAO interface
    ↓
EventDAOImpl implements EventDAO
    ↓
Can swap implementations without changing code
```

### Abstraction
```
Servlet calls dao.getAllEvents()
    ↓
Doesn't know about SQL
    ↓
DAO handles all database complexity
```

---

## 📊 Performance Flow

### Optimizations in Place

1. **Database Connection Pooling**
   - Reuse connections instead of creating new ones

2. **Prepared Statements**
   - Prevent SQL injection
   - Better performance

3. **Indexes on Database**
   - Fast lookups by ID
   - Fast searches by email
   - Fast filtering by date

4. **JSON Caching**
   - Gson instance reused
   - Faster serialization

---

## 🔐 Security Flow

### Input Validation
```
User Input
    ↓
JavaScript Validation (client-side)
    ↓
Servlet Validation (server-side)
    ↓
Database Constraints
```

### SQL Injection Prevention
```
User Input
    ↓
PreparedStatement with ? placeholders
    ↓
Parameters bound safely
    ↓
SQL injection prevented
```

### XSS Prevention
```
User Input
    ↓
Stored in database
    ↓
Retrieved as data
    ↓
Escaped before display (escapeHtml function)
    ↓
XSS attacks prevented
```

---

This flow documentation helps you understand how every piece works together!
