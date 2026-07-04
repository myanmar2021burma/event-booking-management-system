package com.eventmanagement.dao;

import com.eventmanagement.model.Admin;
import java.util.List;

public interface AdminDAO {
    Admin getAdminById(int id);
    Admin getAdminByUsername(String username);
    List<Admin> getAllAdmins();
    Admin authenticate(String username, String password);
}
