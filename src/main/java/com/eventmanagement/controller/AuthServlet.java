package com.eventmanagement.controller;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.dao.UserDAOImpl;
import com.eventmanagement.dao.AdminDAO;
import com.eventmanagement.dao.AdminDAOImpl;
import com.eventmanagement.model.User;
import com.eventmanagement.model.Admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();
     public boolean isGmail(String email) {
        return email != null && email.toLowerCase().endsWith("@gmail.com");
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            return;
        }
        
        switch (pathInfo) {
            case "/register":
                handleRegister(request, response);
                break;
            case "/login":
                handleLogin(request, response);
                break;
            case "/admin-login":
                handleAdminLogin(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/session".equals(pathInfo)) {
            handleGetSession(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = parseJsonRequest(request, User.class);
             String email = user.getEmail(); 
            
            // Validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Username is required");
                return;
            }
             if (!isGmail(email)) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Email must end with @gmail.com");
            return;
        }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email is required");
                return;
            }
            
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Password must be at least 6 characters");
                return;
            }
            
            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Full name is required");
                return;
            }
            
            // Check if username or email already exists
            if (userDAO.usernameExists(user.getUsername())) {
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Username already exists");
                return;
            }
            
            if (userDAO.emailExists(user.getEmail())) {
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Email already exists");
                return;
            }
            
            // Create user
            if (userDAO.createUser(user)) {
                // Don't send password back
                user.setPassword(null);
                
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userType", "user");
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("phone", user.getPhone());
                
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Registration successful");
                responseData.put("user", user);
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                sendJsonResponse(response, responseData);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Map<String, String> credentials = parseJsonRequest(request, Map.class);
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
                return;
            }
            
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                // Don't send password back
                user.setPassword(null);
                
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userType", "user");
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("email", user.getEmail());  
                session.setAttribute("phone", user.getPhone());
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Login successful");
                responseData.put("user", user);
                responseData.put("userType", "user");
                
                sendJsonResponse(response, responseData);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");
        }
    }
    
    private void handleAdminLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Map<String, String> credentials = parseJsonRequest(request, Map.class);
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
                return;
            }
            
            Admin admin = adminDAO.authenticate(username, password);
            
            if (admin != null) {
                // Don't send password back
                admin.setPassword(null);
                
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("username", admin.getUsername());
                session.setAttribute("userType", "admin");
                session.setAttribute("fullName", admin.getFullName());
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Admin login successful");
                responseData.put("admin", admin);
                responseData.put("userType", "admin");
                
                sendJsonResponse(response, responseData);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid admin credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Logged out successfully");
        
        sendJsonResponse(response, responseData);
    }
    
    private void handleGetSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null && session.getAttribute("userType") != null) {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("loggedIn", true);
            sessionData.put("userType", session.getAttribute("userType"));
            sessionData.put("username", session.getAttribute("username"));
            sessionData.put("fullName", session.getAttribute("fullName"));
            sessionData.put("email", session.getAttribute("email"));
            sessionData.put("phone", session.getAttribute("phone"));
            
            if ("user".equals(session.getAttribute("userType"))) {
                sessionData.put("userId", session.getAttribute("userId"));
            } else if ("admin".equals(session.getAttribute("userType"))) {
                sessionData.put("adminId", session.getAttribute("adminId"));
            }
            
            sendJsonResponse(response, sessionData);
        } else {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("loggedIn", false);
            sendJsonResponse(response, sessionData);
        }
    }
}
