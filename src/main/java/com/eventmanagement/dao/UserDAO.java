package com.eventmanagement.dao;

import com.eventmanagement.model.User;
import java.util.List;

public interface UserDAO {
    User getUserById(int id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    User authenticate(String username, String password);
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
