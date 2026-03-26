import database.DBConnection;
import ui.AppTheme;
import ui.MainWindow;
 
import javax.swing.*;
public class Main {
 
    public static void main(String[] args) {
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n" +
                    "Make sure MySQL is running and you have run schema.sql.\n" +
                    "Then update the credentials in database/DBConnection.java.",
                    "Database Connection Failed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        AppTheme.applyGlobalDefaults();
 
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
 
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
 