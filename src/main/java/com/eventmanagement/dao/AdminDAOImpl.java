package com.eventmanagement.dao;

import com.eventmanagement.model.Admin;
import com.eventmanagement.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {
    
    @Override
    public Admin getAdminById(int id) {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'ADMIN'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND role = 'ADMIN'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'ADMIN' ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
    
    @Override
    public Admin authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'ADMIN'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        admin.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Check if is_active column exists, default to true if not
        try {
            admin.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            admin.setActive(true); // Default to active
        }
        
        return admin;
    }
}