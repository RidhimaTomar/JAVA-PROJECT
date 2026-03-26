package ui.panels;
 
import ui.AppTheme;
import usermanagement.AuthResult;
import usermanagement.UserService;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel extends JPanel {
 
    private final UserService userService;
    private final Runnable    onSuccess;
 
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JButton        loginBtn;
 
    public LoginPanel(UserService userService, Runnable onSuccess) {
        this.userService = userService;
        this.onSuccess   = onSuccess;
        buildUI();
    }
 
    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(AppTheme.BG_DARK);
 
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(44, 52, 44, 52)));
        card.setPreferredSize(new Dimension(420, 500));
 
        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx   = 0;
 
        JLabel logo = new JLabel("DocVerify", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(AppTheme.ACCENT_INDIGO);
        c.gridy = 0; c.insets = new Insets(0, 0, 4, 0);
        card.add(logo, c);
 
        JLabel tagline = new JLabel("Document Verification System", SwingConstants.CENTER);
        tagline.setFont(AppTheme.FONT_SMALL);
        tagline.setForeground(AppTheme.TEXT_MUTED);
        c.gridy = 1; c.insets = new Insets(0, 0, 36, 0);
        card.add(tagline, c);

        c.insets = new Insets(0, 0, 4, 0);
        c.gridy  = 2;
        card.add(AppTheme.formLabel("Username"), c);
 
        usernameField = AppTheme.styledField(20);
        usernameField.setPreferredSize(new Dimension(320, 42));
        c.gridy = 3; c.insets = new Insets(0, 0, 16, 0);
        card.add(usernameField, c);
 
        c.gridy = 4; c.insets = new Insets(0, 0, 4, 0);
        card.add(AppTheme.formLabel("Password"), c);
 
        passwordField = AppTheme.styledPasswordField(20);
        passwordField.setPreferredSize(new Dimension(320, 42));
        c.gridy = 5; c.insets = new Insets(0, 0, 8, 0);
        card.add(passwordField, c);
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.ACCENT_RED);
        c.gridy = 6; c.insets = new Insets(0, 0, 12, 0);
        card.add(errorLabel, c);
 
        loginBtn = AppTheme.primaryButton("Sign In");
        loginBtn.setPreferredSize(new Dimension(320, 44));
        c.gridy = 7; c.insets = new Insets(0, 0, 24, 0);
        card.add(loginBtn, c);
 
        JLabel hint = new JLabel("Default credentials: admin / Admin@123", SwingConstants.CENTER);
        hint.setFont(AppTheme.FONT_SMALL);
        hint.setForeground(AppTheme.TEXT_MUTED);
        c.gridy = 8; c.insets = new Insets(0, 0, 0, 0);
        card.add(hint, c);
 
        add(card);
        loginBtn.addActionListener(e -> attemptLogin());
 
        KeyAdapter onEnter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        };
        usernameField.addKeyListener(onEnter);
        passwordField.addKeyListener(onEnter);
    }
 
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
 
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
 
        loginBtn.setEnabled(false);
        loginBtn.setText("Signing in…");
 
        new SwingWorker<AuthResult, Void>() {
            @Override protected AuthResult doInBackground() {
                return userService.login(username, password);
            }
            @Override protected void done() {
                try {
                    AuthResult r = get();
                    if (r.isSuccess()) {
                        errorLabel.setText(" ");
                        onSuccess.run();
                    } else {
                        showError(r.getMessage());
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    showError("Unexpected error: " + ex.getMessage());
                } finally {
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Sign In");
                }
            }
        }.execute();
    }
 
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setForeground(AppTheme.ACCENT_RED);
    }
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        usernameField.requestFocusInWindow();
    }
}
 