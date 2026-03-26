package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

/**
 * Logs important actions to both the console and the audit_log table.
 * If the DB write fails for whatever reason, we still print to console
 * so nothing is silently swallowed.
 */
public class AuditLogger {

    public static void log(String action, String target, String details) {
        // always print to console
        System.out.printf("[AUDIT %s] %s | %s | %s%n",
                LocalDateTime.now().toString().substring(0, 19),
                action, target, details);

        // best-effort DB write
        String sql = "INSERT INTO audit_log (action, target, details) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, target);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AUDIT] Could not persist log: " + e.getMessage());
        }
    }
}
