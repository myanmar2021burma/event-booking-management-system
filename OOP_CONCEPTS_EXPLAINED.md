# 🎓 OOP Concepts Explained - For Beginners

This document explains how Object-Oriented Programming (OOP) concepts are used in this Event Management System.

## 📚 What is OOP?

Object-Oriented Programming is a way of writing code that organizes data and functions into "objects" that represent real-world things. Think of it like building with LEGO blocks - each block (object) has its own properties and can do specific things.

## 🔑 The Four Pillars of OOP

### 1. 🔒 Encapsulation

**What is it?**
Encapsulation means "bundling data and methods together" and "hiding internal details."

**Real-world analogy:**
Think of a TV remote. You press buttons (public interface) without knowing the complex circuitry inside (private implementation).

**In our project:**

```java
// Event.java - Example of Encapsulation
public class Event {
    // Private fields - Hidden from outside
    private int id;
    private String name;
    private double ticketPrice;
    
    // Public methods - Controlled access
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getTicketPrice() {
        return ticketPrice;
    }
    
    public void setTicketPrice(double ticketPrice) {
        // We can add validation here
        if (ticketPrice >= 0) {
            this.ticketPrice = ticketPrice;
        }
    }
}
```

**Why is this good?**
- ✅ Data is protected from invalid changes
- ✅ We can add validation in setters
- ✅ Internal implementation can change without breaking other code
- ✅ Clear interface for using the class

**Where to find it in our project:**
- `Event.java` - All fields are private with public getters/setters
- `Booking.java` - Same pattern
- Both classes protect their data and provide controlled access

---

### 2. 🧬 Inheritance

**What is it?**
Inheritance allows a class to "inherit" properties and methods from another class, promoting code reuse.

**Real-world analogy:**
You inherit traits from your parents (eye color, height). Similarly, child classes inherit from parent classes.

**In our project:**

```java
// BaseServlet.java - Parent class
public abstract class BaseServlet extends HttpServlet {
    protected final Gson gson;
    
    // Common method all servlets can use
    protected void sendJsonResponse(HttpServletResponse response, Object data) {
        // Implementation
    }
    
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String message) {
        // Implementation
    }
}

// EventServlet.java - Child class
public class EventServlet extends BaseServlet {
    // Inherits gson, sendJsonResponse(), sendErrorResponse()
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Can use inherited methods
        sendJsonResponse(response, events);
    }
}

// BookingServlet.java - Another child class
public class BookingServlet extends BaseServlet {
    // Also inherits the same methods
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Can use inherited methods
        sendJsonResponse(response, booking);
    }
}
```

**Why is this good?**
- ✅ Avoid repeating code (DRY - Don't Repeat Yourself)
- ✅ Common functionality in one place
- ✅ Easy to maintain and update
- ✅ Clear hierarchy and relationships

**Where to find it in our project:**
- `BaseServlet.java` → Parent class
- `EventServlet.java` → Extends BaseServlet
- `BookingServlet.java` → Extends BaseServlet
- Both child servlets inherit JSON handling methods

---

### 3. 🎭 Polymorphism

**What is it?**
Polymorphism means "many forms." The same method name can behave differently in different classes.

**Real-world analogy:**
The word "draw" means different things: draw a picture, draw a gun, draw a conclusion. Same word, different actions.

**In our project:**

**Example 1: Method Overriding**
```java
// All servlets override these methods differently
public class EventServlet extends BaseServlet {
    @Override
    protected void doGet(...) {
        // Get events
    }
    
    @Override
    protected void doPost(...) {
        // Create event
    }
}

public class BookingServlet extends BaseServlet {
    @Override
    protected void doGet(...) {
        // Get bookings (different behavior!)
    }
    
    @Override
    protected void doPost(...) {
        // Create booking (different behavior!)
    }
}
```

**Example 2: Interface Implementation**
```java
// Interface defines the contract
public interface EventDAO {
    List<Event> getAllEvents();
    Event getEventById(int id);
}

// Implementation provides the actual behavior
public class EventDAOImpl implements EventDAO {
    @Override
    public List<Event> getAllEvents() {
        // MySQL implementation
        // Could be replaced with PostgreSQL, MongoDB, etc.
    }
    
    @Override
    public Event getEventById(int id) {
        // MySQL implementation
    }
}
```

**Why is this good?**
- ✅ Same interface, different implementations
- ✅ Easy to swap implementations (e.g., change database)
- ✅ Code is flexible and extensible
- ✅ Follows "program to an interface, not an implementation"

**Where to find it in our project:**
- `EventDAO` interface → `EventDAOImpl` class
- `BookingDAO` interface → `BookingDAOImpl` class
- Servlet method overriding (doGet, doPost, doPut, doDelete)

---

### 4. 🎨 Abstraction

**What is it?**
Abstraction means hiding complex implementation details and showing only essential features.

**Real-world analogy:**
When you drive a car, you use the steering wheel and pedals (simple interface) without knowing how the engine works (complex implementation).

**In our project:**

```java
// Interface - Abstract contract (what to do)
public interface EventDAO {
    List<Event> getAllEvents();
    Event getEventById(int id);
    boolean addEvent(Event event);
    // User doesn't need to know HOW these work
}

// Implementation - Concrete details (how to do it)
public class EventDAOImpl implements EventDAO {
    @Override
    public List<Event> getAllEvents() {
        // Complex SQL queries
        // Database connection handling
        // Result set processing
        // Error handling
        // All hidden from the user!
    }
}

// Usage - Simple and clean
public class EventServlet extends BaseServlet {
    private EventDAO eventDAO = new EventDAOImpl();
    
    protected void doGet(...) {
        // Just call the method - don't worry about SQL!
        List<Event> events = eventDAO.getAllEvents();
        sendJsonResponse(response, events);
    }
}
```

**Why is this good?**
- ✅ Simplifies complex operations
- ✅ Separates "what" from "how"
- ✅ Easy to change implementation without affecting users
- ✅ Makes code more maintainable

**Where to find it in our project:**
- DAO interfaces (`EventDAO`, `BookingDAO`) - Abstract contracts
- DAO implementations hide database complexity
- Servlets use DAOs without knowing database details

---

## 🏗️ Design Patterns Used

### 1. DAO (Data Access Object) Pattern

**Purpose:** Separate database operations from business logic.

**Structure:**
```
Interface (EventDAO) → Implementation (EventDAOImpl) → Database
```

**Benefits:**
- Database code in one place
- Easy to test (can mock the DAO)
- Can switch databases without changing business logic

**Files:**
- `dao/EventDAO.java`
- `dao/EventDAOImpl.java`
- `dao/BookingDAO.java`
- `dao/BookingDAOImpl.java`

### 2. MVC (Model-View-Controller) Pattern

**Purpose:** Separate concerns into three layers.

**Structure:**
- **Model** (`Event.java`, `Booking.java`) - Data structure
- **View** (`index.html`, CSS, JS) - User interface
- **Controller** (Servlets) - Handle requests, coordinate between model and view

**Benefits:**
- Clear separation of concerns
- Easy to modify one layer without affecting others
- Better organization

### 3. Singleton Pattern (Implicit)

**Where:** Database connections through `DatabaseUtil`

**Purpose:** Manage shared resources efficiently

---

## 📖 Code Examples Explained

### Example 1: Creating an Event (Full Flow)

```java
// 1. MODEL - Data structure (Encapsulation)
public class Event {
    private int id;
    private String name;
    private double ticketPrice;
    // Getters and setters
}

// 2. DAO INTERFACE - Abstract contract (Abstraction)
public interface EventDAO {
    boolean addEvent(Event event);
}

// 3. DAO IMPLEMENTATION - Concrete implementation (Polymorphism)
public class EventDAOImpl implements EventDAO {
    @Override
    public boolean addEvent(Event event) {
        // SQL logic here
    }
}

// 4. CONTROLLER - Request handler (Inheritance)
public class EventServlet extends BaseServlet {
    private EventDAO eventDAO = new EventDAOImpl();
    
    @Override // Polymorphism
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Event event = parseJsonRequest(request, Event.class);
        eventDAO.addEvent(event); // Uses abstraction
        sendJsonResponse(response, event); // Uses inheritance
    }
}
```

**All 4 OOP concepts in action!**

---

## 🎯 Learning Exercises

### Exercise 1: Add a New Field
Try adding a `category` field to the Event class:

1. Add private field to `Event.java`
2. Add getter and setter (Encapsulation)
3. Update database schema
4. Update `EventDAOImpl` to handle the new field

### Exercise 2: Create a New DAO
Create a `UserDAO` for managing users:

1. Create `User.java` model (Encapsulation)
2. Create `UserDAO` interface (Abstraction)
3. Create `UserDAOImpl` class (Polymorphism)
4. Create `UserServlet` extending `BaseServlet` (Inheritance)

### Exercise 3: Add Validation
Add validation to the `Event` class:

```java
public void setTicketPrice(double ticketPrice) {
    if (ticketPrice < 0) {
        throw new IllegalArgumentException("Price cannot be negative");
    }
    this.ticketPrice = ticketPrice;
}
```

This demonstrates encapsulation - controlling how data is modified.

---

## 🔍 Quick Reference

| Concept | Key Question | Example in Project |
|---------|-------------|-------------------|
| **Encapsulation** | How do we protect data? | Private fields with public getters/setters in `Event.java` |
| **Inheritance** | How do we reuse code? | `EventServlet` extends `BaseServlet` |
| **Polymorphism** | How do we have many forms? | `EventDAOImpl` implements `EventDAO` |
| **Abstraction** | How do we hide complexity? | DAO interfaces hide database details |

---

## 📚 Further Reading

### Beginner-Friendly Resources:
1. **Java OOP Basics**
   - [Oracle Java Tutorials - OOP Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
   - [W3Schools Java OOP](https://www.w3schools.com/java/java_oop.asp)

2. **Design Patterns**
   - [DAO Pattern Explained](https://www.baeldung.com/java-dao-pattern)
   - [MVC Pattern](https://www.geeksforgeeks.org/mvc-design-pattern/)

3. **Practice**
   - Try modifying this project
   - Add new features
   - Experiment with the code

---

## ✅ Checklist: Do You Understand?

- [ ] Can you explain encapsulation using the `Event` class?
- [ ] Can you identify where inheritance is used?
- [ ] Can you explain why we use DAO interfaces?
- [ ] Can you add a new field to a model class?
- [ ] Can you create a simple servlet?
- [ ] Do you understand the MVC pattern in this project?

---

**Remember:** The best way to learn OOP is by doing! Try modifying the code, break things, fix them, and experiment. That's how you truly understand these concepts.

Happy Learning! 🚀
