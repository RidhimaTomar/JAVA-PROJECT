package usermanagement;

import database.AuditLogger;
import database.UserRepository;
import model.User;
import utils.PasswordUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public class UserService {

    private final UserRepository userRepo;
    private User currentUser; 

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    public AuthResult login(String username, String password) {
        try {
            Optional<User> opt = userRepo.findByUsername(username);
            if (opt.isEmpty()) {
                AuditLogger.log("LOGIN_FAILED", username, "User not found");
                return AuthResult.failure("Invalid username or password.");
            }

            User user = opt.get();

            if (user.getStatus() == User.Status.SUSPENDED) {
                AuditLogger.log("LOGIN_BLOCKED", username, "Account suspended");
                return AuthResult.failure("Your account has been suspended. Please contact an admin.");
            }
            if (user.getStatus() == User.Status.INACTIVE) {
                return AuthResult.failure("This account is inactive.");
            }

            if (!PasswordUtils.hash(password).equals(user.getPasswordHash())) {
                AuditLogger.log("LOGIN_FAILED", username, "Wrong password");
                return AuthResult.failure("Invalid username or password.");
            }

            user.setLastLogin(LocalDateTime.now());
            userRepo.save(user);
            currentUser = user;
            AuditLogger.log("LOGIN_SUCCESS", username, "Role=" + user.getRole());
            return AuthResult.success(user);

        } catch (SQLException e) {
            return AuthResult.failure("Database error during login: " + e.getMessage());
        }
    }

    public void logout() {
        if (currentUser != null) {
            AuditLogger.log("LOGOUT", currentUser.getUsername(), "");
            currentUser = null;
        }
    }

    public User getCurrentUser() { return currentUser; }

    public OperationResult registerUser(String username, String password,
                                        String email, User.Role role) {
        requireAdmin();
        try {
            if (username == null || username.isBlank())
                return OperationResult.failure("Username cannot be empty.");
            if (userRepo.existsByUsername(username))
                return OperationResult.failure("Username '" + username + "' is already taken.");
            if (userRepo.existsByEmail(email))
                return OperationResult.failure("Email '" + email + "' is already registered.");
            if (password.length() < 8)
                return OperationResult.failure("Password must be at least 8 characters long.");
            if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"))
                return OperationResult.failure("Email address format is invalid.");

            User user = new User(username, PasswordUtils.hash(password), email, role);
            userRepo.save(user);

            AuditLogger.log("USER_REGISTERED", username,
                    "By=" + currentUser.getUsername() + ", Role=" + role);
            return OperationResult.success("User '" + username + "' registered successfully.", user);

        } catch (SQLException e) {
            return OperationResult.failure("DB error: " + e.getMessage());
        }
    }

    public OperationResult updateRole(int userId, User.Role newRole) {
        requireAdmin();
        try {
            Optional<User> opt = userRepo.findById(userId);
            if (opt.isEmpty()) return OperationResult.failure("User not found.");
            User user    = opt.get();
            User.Role old = user.getRole();
            user.setRole(newRole);
            userRepo.save(user);
            AuditLogger.log("ROLE_CHANGED", user.getUsername(),
                    old + " → " + newRole + " By=" + currentUser.getUsername());
            return OperationResult.success("Role updated to " + newRole + ".", user);
        } catch (SQLException e) {
            return OperationResult.failure("DB error: " + e.getMessage());
        }
    }

    public OperationResult suspendUser(int userId) {
        requireAdmin();
        try {
            Optional<User> opt = userRepo.findById(userId);
            if (opt.isEmpty()) return OperationResult.failure("User not found.");
            User user = opt.get();
            if (user.getUserId() == currentUser.getUserId())
                return OperationResult.failure("You cannot suspend your own account.");
            user.setStatus(User.Status.SUSPENDED);
            userRepo.save(user);
            AuditLogger.log("USER_SUSPENDED", user.getUsername(), "By=" + currentUser.getUsername());
            return OperationResult.success("User '" + user.getUsername() + "' suspended.", user);
        } catch (SQLException e) {
            return OperationResult.failure("DB error: " + e.getMessage());
        }
    }

    public OperationResult activateUser(int userId) {
        requireAdmin();
        try {
            Optional<User> opt = userRepo.findById(userId);
            if (opt.isEmpty()) return OperationResult.failure("User not found.");
            User user = opt.get();
            user.setStatus(User.Status.ACTIVE);
            userRepo.save(user);
            AuditLogger.log("USER_ACTIVATED", user.getUsername(), "By=" + currentUser.getUsername());
            return OperationResult.success("User '" + user.getUsername() + "' activated.", user);
        } catch (SQLException e) {
            return OperationResult.failure("DB error: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        requireAdmin();
        try {
            return userRepo.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch users: " + e.getMessage(), e);
        }
    }
    
    private void requireAdmin() {
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            throw new SecurityException("This action requires ADMIN privileges.");
        }
    }
}
