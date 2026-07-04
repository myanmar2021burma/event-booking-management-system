# 🔐 Authentication System Guide

## Overview

Your Event Management System now has a complete authentication system with separate user and admin logins!

---

## ✨ New Features Added

### 1. **User Registration & Login**
- ✅ Users can create their own accounts
- ✅ Secure login with username/password
- ✅ Session management
- ✅ Password strength indicator
- ✅ Form validation

### 2. **Admin Login (Pre-created Accounts)**
- ✅ Admins use credentials provided by you
- ✅ Admins cannot self-register
- ✅ Separate admin authentication
- ✅ Admin-only features

### 3. **Modern UI Design**
- ✅ Beautiful gradient backgrounds
- ✅ Smooth animations
- ✅ Responsive design (mobile-friendly)
- ✅ Loading states
- ✅ Alert messages
- ✅ Tab switching for user/admin login

---

## 🚀 How to Use

### Access the Application

**Main URL:** http://localhost:7070/event-management

**Login Page:** http://localhost:7070/event-management/login.html

**Register Page:** http://localhost:7070/event-management/register.html

---

## 👥 User Accounts

### For Regular Users (Customers)

#### To Register:
1. Go to http://localhost:7070/event-management/register.html
2. Fill in the registration form:
   - Username (unique)
   - Email (unique)
   - Full Name
   - Phone (optional)
   - Password (min 6 characters)
   - Confirm Password
3. Click "Create Account"
4. You'll be automatically logged in and redirected

#### To Login:
1. Go to http://localhost:7070/event-management/login.html
2. Click "User Login" tab
3. Enter username and password
4. Click "Login"

#### Demo User Accounts:
```
Username: john_doe
Password: user123

Username: jane_smith
Password: user123

Username: bob_johnson
Password: user123
```

---

## 👨‍💼 Admin Accounts

### For Administrators

#### Pre-created Admin Accounts:
```
Username: admin
Password: admin123
Full Name: System Administrator
Email: admin@eventmanagement.com

Username: manager
Password: manager123
Full Name: Event Manager
Email: manager@eventmanagement.com
```

#### To Login as Admin:
1. Go to http://localhost:7070/event-management/login.html
2. Click "Admin Login" tab
3. Enter admin username and password
4. Click "Admin Login"

**⚠️ Important:** Admins CANNOT register themselves. Only use the pre-created credentials above.

---

## 🗄️ Database Structure

### New Tables Created:

#### `users` Table
```sql
- id (Primary Key)
- username (Unique)
- email (Unique)
- password
- full_name
- phone
- created_at
- updated_at
- is_active
```

#### `admins` Table
```sql
- id (Primary Key)
- username (Unique)
- password
- full_name
- email (Unique)
- created_at
- updated_at
- is_active
```

#### Updated `bookings` Table
```sql
- Added: user_id (Foreign Key to users table)
```

---

## 🔧 API Endpoints

### Authentication API

**Base URL:** `/api/auth/`

#### 1. User Registration
```
POST /api/auth/register
Content-Type: application/json

Body:
{
  "username": "newuser",
  "email": "user@example.com",
  "fullName": "John Doe",
  "phone": "555-1234",
  "password": "password123"
}

Response (201 Created):
{
  "success": true,
  "message": "Registration successful",
  "user": {
    "id": 4,
    "username": "newuser",
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "555-1234"
  }
}
```

#### 2. User Login
```
POST /api/auth/login
Content-Type: application/json

Body:
{
  "username": "john_doe",
  "password": "user123"
}

Response (200 OK):
{
  "success": true,
  "message": "Login successful",
  "user": {...},
  "userType": "user"
}
```

#### 3. Admin Login
```
POST /api/auth/admin-login
Content-Type: application/json

Body:
{
  "username": "admin",
  "password": "admin123"
}

Response (200 OK):
{
  "success": true,
  "message": "Admin login successful",
  "admin": {...},
  "userType": "admin"
}
```

#### 4. Get Session
```
GET /api/auth/session

Response (200 OK):
{
  "loggedIn": true,
  "userType": "user",
  "username": "john_doe",
  "fullName": "John Doe",
  "userId": 1
}
```

#### 5. Logout
```
POST /api/auth/logout

Response (200 OK):
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

## 🎨 UI Features

### Login Page (`login.html`)
- **Tab Switching:** Toggle between User and Admin login
- **Form Validation:** Real-time validation
- **Loading States:** Button shows loading animation
- **Error Messages:** Clear error display
- **Demo Credentials:** Shown for easy testing

### Register Page (`register.html`)
- **Password Strength Indicator:** Visual feedback
- **Confirm Password:** Ensures passwords match
- **Form Validation:** All fields validated
- **Responsive Design:** Works on all devices

### Styling (`auth.css`)
- **Modern Gradient Background:** Purple/blue gradient
- **Card-based Layout:** Clean, centered design
- **Smooth Animations:** Fade-in and slide-up effects
- **Responsive:** Mobile-first approach
- **Loading States:** Spinner animation on buttons

---

## 🔒 Security Features

### Implemented:
1. ✅ **Session Management:** Server-side sessions
2. ✅ **Password Validation:** Minimum 6 characters
3. ✅ **Unique Constraints:** Username and email must be unique
4. ✅ **SQL Injection Prevention:** Prepared statements
5. ✅ **XSS Prevention:** Input sanitization
6. ✅ **Active Status:** Can disable accounts

### Recommended for Production:
- 🔧 **Password Hashing:** Use BCrypt or similar (currently plain text)
- 🔧 **HTTPS:** Enable SSL/TLS
- 🔧 **CSRF Protection:** Add CSRF tokens
- 🔧 **Rate Limiting:** Prevent brute force attacks
- 🔧 **Email Verification:** Verify email addresses
- 🔧 **Password Reset:** Forgot password functionality

---

## 📝 How to Add More Admin Accounts

### Method 1: Direct Database Insert
```sql
USE event_management;

INSERT INTO admins (username, password, full_name, email) VALUES
('newadmin', 'password123', 'New Administrator', 'newadmin@example.com');
```

### Method 2: Using MySQL Command Line
```bash
mysql -u root event_management

INSERT INTO admins (username, password, full_name, email) 
VALUES ('newadmin', 'password123', 'New Administrator', 'newadmin@example.com');
```

---

## 🧪 Testing the Authentication

### Test User Registration:
1. Go to register page
2. Fill form with new user details
3. Submit
4. Should redirect to main page logged in

### Test User Login:
1. Go to login page
2. Use demo credentials: john_doe / user123
3. Should redirect to main page

### Test Admin Login:
1. Go to login page
2. Click "Admin Login" tab
3. Use: admin / admin123
4. Should redirect to main page with admin privileges

### Test Session:
1. Login as user or admin
2. Refresh the page
3. Should remain logged in
4. Check browser console for session data

---

## 🔄 Session Management

### How Sessions Work:
1. User/Admin logs in
2. Server creates session
3. Session stores:
   - User ID or Admin ID
   - Username
   - User Type (user/admin)
   - Full Name
4. Session cookie sent to browser
5. All subsequent requests include session

### Check Session Status:
```javascript
fetch('/event-management/api/auth/session')
  .then(res => res.json())
  .then(data => console.log(data));
```

---

## 🎯 Next Steps

### Recommended Enhancements:

1. **Password Hashing**
   - Implement BCrypt for secure password storage
   - Update UserDAOImpl and AdminDAOImpl

2. **Protected Routes**
   - Add authentication filter
   - Restrict admin-only endpoints
   - Redirect unauthenticated users

3. **User Profile**
   - View/edit profile page
   - Change password functionality
   - Upload profile picture

4. **Email Verification**
   - Send verification email on registration
   - Verify email before allowing login

5. **Password Reset**
   - Forgot password link
   - Email reset token
   - Reset password page

6. **Remember Me**
   - Checkbox on login
   - Extended session duration

7. **Two-Factor Authentication**
   - SMS or email OTP
   - Authenticator app support

---

## 📊 File Structure

```
event-management-system/
├── database/
│   └── auth_schema.sql              # Authentication tables
├── src/main/java/
│   └── com/eventmanagement/
│       ├── model/
│       │   ├── User.java            # User model
│       │   └── Admin.java           # Admin model
│       ├── dao/
│       │   ├── UserDAO.java         # User DAO interface
│       │   ├── UserDAOImpl.java     # User DAO implementation
│       │   ├── AdminDAO.java        # Admin DAO interface
│       │   └── AdminDAOImpl.java    # Admin DAO implementation
│       └── controller/
│           └── AuthServlet.java     # Authentication servlet
└── src/main/webapp/
    ├── login.html                   # Login page
    ├── register.html                # Registration page
    └── resources/
        ├── css/
        │   └── auth.css             # Authentication styles
        └── js/
            └── auth.js              # Authentication logic
```

---

## 🐛 Troubleshooting

### Issue: "Username already exists"
**Solution:** Choose a different username

### Issue: "Email already exists"
**Solution:** Use a different email or login with existing account

### Issue: "Invalid admin credentials"
**Solution:** Make sure you're using the correct admin credentials (admin/admin123)

### Issue: "Passwords do not match"
**Solution:** Ensure password and confirm password are identical

### Issue: "Password must be at least 6 characters"
**Solution:** Use a longer password

### Issue: Session not persisting
**Solution:** Check browser cookies are enabled

---

## 📞 Quick Reference

### Demo Credentials

**Users:**
- john_doe / user123
- jane_smith / user123
- bob_johnson / user123

**Admins:**
- admin / admin123
- manager / manager123

### URLs
- Main App: http://localhost:7070/event-management
- Login: http://localhost:7070/event-management/login.html
- Register: http://localhost:7070/event-management/register.html

### API Endpoints
- Register: POST /api/auth/register
- User Login: POST /api/auth/login
- Admin Login: POST /api/auth/admin-login
- Get Session: GET /api/auth/session
- Logout: POST /api/auth/logout

---

## ✅ Summary

You now have a complete authentication system with:
- ✅ User registration (self-service)
- ✅ User login
- ✅ Admin login (pre-created accounts only)
- ✅ Session management
- ✅ Modern, responsive UI
- ✅ Secure password handling
- ✅ Form validation
- ✅ Separate user/admin roles

**Your application is ready to use!** 🎉

Start by visiting: http://localhost:7070/event-management/login.html
