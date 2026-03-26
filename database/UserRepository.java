package database;
 
import model.User;
 
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
public class UserRepository {
 
    public void save(User u) throws SQLException {
        
        if (u.getUserId() > 0) {
            update(u);
        } else {
            insert(u);
        }
    }
 
 
    private void insert(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, role, status, created_at, last_login) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole().name());
            ps.setString(5, u.getStatus().name());
            ps.setTimestamp(6, toTimestamp(u.getCreatedAt()));
            ps.setTimestamp(7, toTimestamp(u.getLastLogin()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) u.setUserId(keys.getInt(1));
        }
    }
 
    private void update(User u) throws SQLException {
        String sql = "UPDATE users SET username=?, password_hash=?, email=?, role=?, status=?, last_login=? "
                   + "WHERE user_id=?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole().name());
            ps.setString(5, u.getStatus().name());
            ps.setTimestamp(6, toTimestamp(u.getLastLogin()));
            ps.setInt(7, u.getUserId());
            ps.executeUpdate();
        }
    }
 
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }
 
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }
 
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        }
    }
 
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        }
    }
 
    public boolean existsById(int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeQuery().next();
        }
    }
 
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
 
    
 
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setEmail(rs.getString("email"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setStatus(User.Status.valueOf(rs.getString("status")));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) u.setCreatedAt(created.toLocalDateTime());
        Timestamp last = rs.getTimestamp("last_login");
        if (last != null) u.setLastLogin(last.toLocalDateTime());
        return u;
    }
 
    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }
}
 