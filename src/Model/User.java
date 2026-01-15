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
    private LinkedList<User> users = new LinkedList<>();
    
    private String username;
    private String password;
    private String role;
    private LinkedList<PokeCard> inventory = new LinkedList<>(); // Personal inventory

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {
        loadDefaultUsers(); // Add default admin + sample users
    }

    private void loadDefaultUsers() {
        users.add(new User("admin", "poke123", "admin")); 
        users.add(new User("user", "user123", "user")); 
        users.add(new User("alwin", "Kuri111", "user"));
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
    
    public LinkedList<PokeCard> getInventory() {
        return new LinkedList<>(inventory);
    }

    /**
     * Adds a card to personal inventory if not duplicate by ID.
     * @param card Card to add.
     * @return true if added, false if duplicate.
     */
    public boolean addToInventory(PokeCard card) {
        for (PokeCard c : inventory) {
            if (c.getId().equals(card.getId())) return false;
        }
        inventory.add(card);
        return true;
    }

    /**
     * Removes a card from inventory by ID.
     * @param id ID to remove.
     */
    public void removeFromInventory(String id) {
        inventory.removeIf(c -> c.getId().equals(id));
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
     * Gets user role 
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

    /**
     * Gets User by username.
     * @param username Username.
     * @return User or null.
     */
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a new user (role "user").
     * @param username Username.
     * @param password Password.
     * @return true if added, false if duplicate.
     */
    public boolean addUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Check for duplicate username
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return false; // duplicate
            }
        }

        // Add new user with role "user"
        users.add(new User(username, password, "user"));
        return true;
    }

    public LinkedList<User> getAllUsers() {
        return new LinkedList<>(users);
    }
}
