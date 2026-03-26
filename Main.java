import database.DBConnection;
import ui.AppTheme;
import ui.MainWindow;
 
import javax.swing.*;
 
/**
 * Application entry point.
 *
 * Before running:
 *   1. Create the MySQL database by running schema.sql
 *   2. Update DB_URL / USER / PASSWORD in database/DBConnection.java
 *   3. Add mysql-connector-j-*.jar to your classpath
 *
 * Then compile everything under src/ and run this class.
 */
public class Main {
 
    public static void main(String[] args) {
 
        // Check DB before opening the window — gives a clear error early
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n" +
                    "Make sure MySQL is running and you have run schema.sql.\n" +
                    "Then update the credentials in database/DBConnection.java.",
                    "Database Connection Failed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
 
        // Apply theme defaults and launch on the Event Dispatch Thread
        AppTheme.applyGlobalDefaults();
 
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to use the system look-and-feel as a base,
                // then we override everything with our dark theme.
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // not a big deal if this fails
            }
 
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
 