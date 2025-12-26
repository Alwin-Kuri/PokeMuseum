/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Kuri
 */
import java.util.LinkedList;
public class User {
    private LinkedList<User> users;
    
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    
    public User() {
        users = new LinkedList<>();
        loadDefaultUsers(); // Add default admin + sample users
    }

    private void loadDefaultUsers() {
        users.add(new User("admin", "poke123", "admin")); //only admin for now (Milestone 1 CRUD)
    }

    /**
     * Validates login credentials
     * @param username
     * @param password
     * @return true if valid user found
     */
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) return false;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets user role (optional for future admin-only features)
     * @param username
     * @return role or null if not found
     */
    public String getUserRole(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.getRole();
            }
        }
        return null;
    }

    public LinkedList<User> getAllUsers() {
        return new LinkedList<>(users); // Return copy for safety
    }
}
