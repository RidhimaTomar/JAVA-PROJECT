package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class AuditLogger {

    public static void log(String action, String target, String details) {
       
        System.out.printf("[AUDIT %s] %s | %s | %s%n",
                LocalDateTime.now().toString().substring(0, 19),
                action, target, details);

    
        String sql = "INSERT INTO audit_log (action, target, details) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql1)) {
            ps.setString(1, action);
            ps.setString(2, target);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AUDIT] Could not persist log: " + e.getMessage());
        }
    }
}
