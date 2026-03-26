package model;

import java.time.LocalDateTime;

public class User {

    public enum Role   { ADMIN, VERIFIER, VIEWER }
    public enum Status { ACTIVE, INACTIVE, SUSPENDED }

    private int           userId;
    private String        username;
    private String        passwordHash;
    private String        email;
    private Role          role;
    private Status        status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User() {
        this.status    = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String passwordHash, String email, Role role) {
        this();
        this.username     = username;
        this.passwordHash = passwordHash;
        this.email        = email;
        this.role         = role;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int           getUserId()                       { return userId; }
    public void          setUserId(int id)                 { this.userId = id; }
    public String        getUsername()                     { return username; }
    public void          setUsername(String u)             { this.username = u; }
    public String        getPasswordHash()                 { return passwordHash; }
    public void          setPasswordHash(String h)         { this.passwordHash = h; }
    public String        getEmail()                        { return email; }
    public void          setEmail(String e)                { this.email = e; }
    public Role          getRole()                         { return role; }
    public void          setRole(Role r)                   { this.role = r; }
    public Status        getStatus()                       { return status; }
    public void          setStatus(Status s)               { this.status = s; }
    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)     { this.createdAt = t; }
    public LocalDateTime getLastLogin()                    { return lastLogin; }
    public void          setLastLogin(LocalDateTime t)     { this.lastLogin = t; }

   
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', role=" + role + ", status=" + status + "}";
    }
}
