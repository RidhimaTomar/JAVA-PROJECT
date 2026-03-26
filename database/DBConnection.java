package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages a single JDBC connection to MySQL.
 * Just a straightforward singleton — nothing fancy.
 */
public class DBConnection {

    // ── Change these three lines to match your MySQL setup ──────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/docverify_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "mysql@123";
    // ────────────────────────────────────────────────────────────────────

    private static Connection connection;

    private DBConnection() {}   // nobody should instantiate this

    public static Connection get() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to docverify_db.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found on classpath: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }

    /** Quick sanity check – useful for startup. */
    public static boolean testConnection() {
        try {
            get();
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
