package com.eventmanagement.controller;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.dao.UserDAOImpl;
import com.eventmanagement.model.User;

import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserServlet extends BaseServlet {
    
    private final UserDAO userDAO;
    
    public UserServlet() {
        this.userDAO = new UserDAOImpl();
    }
    
    public UserServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all users (only for admin)
                // Check if admin is logged in
                HttpSession session = request.getSession(false);
                if (session == null || !"admin".equals(session.getAttribute("userType"))) {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                    return;
                }
                
                List<User> users = userDAO.getAllUsers();
                // Remove passwords before sending
                users.forEach(user -> user.setPassword(null));
                sendJsonResponse(response, users);
            } else {
                // Get user by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int userId = Integer.parseInt(pathParts[1]);
                    User user = userDAO.getUserById(userId);
                    if (user != null) {
                        user.setPassword(null);
                        sendJsonResponse(response, user);
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Check if admin is logged in
            HttpSession session = request.getSession(false);
            if (session == null || !"admin".equals(session.getAttribute("userType"))) {
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return;
            }
            
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            
            int userId = Integer.parseInt(pathParts[1]);
            
            // Don't allow deleting yourself
            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId != null && adminId == userId) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Cannot delete your own account");
                return;
            }
            
            if (userDAO.deleteUser(userId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found or could not be deleted");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}