package ui.panels;
 
import model.User;
import ui.AppTheme;
import usermanagement.OperationResult;
import usermanagement.UserService;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
 
/**
 * Admin-only panel for managing system users.
 * Left: filterable user table + action buttons.
 * Right: register-new-user form.
 */
public class UserManagementPanel extends JPanel {
 
    private final UserService userService;
 
    private DefaultTableModel tableModel;
    private JTable            userTable;
    private JLabel            statusBar;
 
    private JTextField     newUsername;
    private JPasswordField newPassword;
    private JTextField     newEmail;
    private JComboBox<User.Role> roleCombo;
 
    public UserManagementPanel(UserService userService) {
        this.userService = userService;
        buildUI();
        refresh();
    }
 
    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_DARK);
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }
 
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 12, 28));
 
        JLabel title = new JLabel("User Management");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
 
        JLabel sub = new JLabel("Manage system accounts, roles and access");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
 
        JPanel block = new JPanel(new GridLayout(2, 1, 0, 3));
        block.setOpaque(false);
        block.add(title);
        block.add(sub);
 
        JButton refreshBtn = AppTheme.ghostButton("⟳  Refresh");
        refreshBtn.addActionListener(e -> refresh());
 
        p.add(block,      BorderLayout.WEST);
        p.add(refreshBtn, BorderLayout.EAST);
        return p;
    }
 
    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(620);
        split.setDividerSize(3);
        split.setBackground(AppTheme.BG_DARK);
        split.setBorder(null);
        return split;
    }
 
    // ── Left: user table ──────────────────────────────────────────────────
 
    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_PANEL);
        p.setBorder(new EmptyBorder(0, 28, 0, 8));
 
        String[] cols = {"ID", "Username", "Email", "Role", "Status", "Last Login"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        userTable = new JTable(tableModel);
        styleTable(userTable);
 
        // colour-code Status
        userTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                String s = val == null ? "" : val.toString();
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_TABLE_ODD : AppTheme.BG_TABLE_EVEN);
                setForeground(switch (s) {
                    case "ACTIVE"    -> AppTheme.ACCENT_GREEN;
                    case "SUSPENDED" -> AppTheme.ACCENT_RED;
                    default          -> AppTheme.ACCENT_AMBER;
                });
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        // colour-code Role
        userTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                String s = val == null ? "" : val.toString();
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_TABLE_ODD : AppTheme.BG_TABLE_EVEN);
                setForeground(switch (s) {
                    case "ADMIN"    -> AppTheme.ACCENT_INDIGO;
                    case "VERIFIER" -> AppTheme.ACCENT_BLUE;
                    default         -> AppTheme.TEXT_SECONDARY;
                });
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        JScrollPane scroll = new JScrollPane(userTable);
        scroll.getViewport().setBackground(AppTheme.BG_TABLE_ODD);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
 
        // action buttons row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        actions.setOpaque(false);
 
        JButton activateBtn = AppTheme.successButton("Activate");
        JButton suspendBtn  = AppTheme.dangerButton("Suspend");
        JButton chgRoleBtn  = AppTheme.ghostButton("Change Role");
 
        activateBtn.addActionListener(e -> activateSelected());
        suspendBtn.addActionListener(e  -> suspendSelected());
        chgRoleBtn.addActionListener(e  -> changeRoleSelected());
 
        actions.add(activateBtn);
        actions.add(suspendBtn);
        actions.add(chgRoleBtn);
 
        p.add(scroll,  BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }
 
    // ── Right: register form ──────────────────────────────────────────────
 
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppTheme.BG_DARK);
        outer.setBorder(new EmptyBorder(0, 8, 0, 28));
 
        JPanel card = AppTheme.cardPanel();
        card.setLayout(new GridBagLayout());
 
        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx   = 0;
 
        JLabel heading = new JLabel("Register New User");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.ACCENT_INDIGO);
        c.gridy = 0; c.insets = new Insets(0, 0, 20, 0);
        card.add(heading, c);
 
        c.insets = new Insets(0, 0, 4, 0);
 
        c.gridy = 1; card.add(AppTheme.formLabel("Username"), c);
        newUsername = AppTheme.styledField(16);
        c.gridy = 2; c.insets = new Insets(0, 0, 12, 0); card.add(newUsername, c);
 
        c.gridy = 3; c.insets = new Insets(0, 0, 4, 0);
        card.add(AppTheme.formLabel("Password (min 8 chars)"), c);
        newPassword = AppTheme.styledPasswordField(16);
        c.gridy = 4; c.insets = new Insets(0, 0, 12, 0); card.add(newPassword, c);
 
        c.gridy = 5; c.insets = new Insets(0, 0, 4, 0);
        card.add(AppTheme.formLabel("Email"), c);
        newEmail = AppTheme.styledField(16);
        c.gridy = 6; c.insets = new Insets(0, 0, 12, 0); card.add(newEmail, c);
 
        c.gridy = 7; c.insets = new Insets(0, 0, 4, 0);
        card.add(AppTheme.formLabel("Role"), c);
        roleCombo = AppTheme.styledCombo(User.Role.values());
        c.gridy = 8; c.insets = new Insets(0, 0, 20, 0); card.add(roleCombo, c);
 
        JButton registerBtn = AppTheme.primaryButton("Register User");
        registerBtn.setPreferredSize(new Dimension(220, 42));
        c.gridy = 9; c.insets = new Insets(0, 0, 0, 0);
        card.add(registerBtn, c);
 
        registerBtn.addActionListener(e -> registerUser());
 
        outer.add(card, BorderLayout.NORTH);
        return outer;
    }
 
    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(AppTheme.BG_SIDEBAR);
        p.setBorder(new EmptyBorder(4, 20, 4, 20));
        statusBar = new JLabel("Ready");
        statusBar.setFont(AppTheme.FONT_SMALL);
        statusBar.setForeground(AppTheme.TEXT_MUTED);
        p.add(statusBar);
        return p;
    }
 
    // ── Actions ───────────────────────────────────────────────────────────
 
    private void registerUser() {
        String username = newUsername.getText().trim();
        String password = new String(newPassword.getPassword());
        String email    = newEmail.getText().trim();
        User.Role role  = (User.Role) roleCombo.getSelectedItem();
 
        try {
            OperationResult r = userService.registerUser(username, password, email, role);
            if (r.isSuccess()) {
                setStatus("✔  " + r.getMessage(), AppTheme.ACCENT_GREEN);
                clearForm();
                refresh();
            } else {
                setStatus("✘  " + r.getMessage(), AppTheme.ACCENT_RED);
            }
        } catch (SecurityException ex) {
            setStatus("✘  Access denied — admin only.", AppTheme.ACCENT_RED);
        }
    }
 
    private void suspendSelected() {
        int row = userTable.getSelectedRow();
        if (row < 0) { setStatus("Please select a user first.", AppTheme.ACCENT_AMBER); return; }
        int userId = (int) tableModel.getValueAt(row, 0);
        try {
            OperationResult r = userService.suspendUser(userId);
            setStatus(r.isSuccess() ? "✔  " + r.getMessage() : "✘  " + r.getMessage(),
                      r.isSuccess() ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED);
            refresh();
        } catch (SecurityException ex) {
            setStatus("✘  Admin only.", AppTheme.ACCENT_RED);
        }
    }
 
    private void activateSelected() {
        int row = userTable.getSelectedRow();
        if (row < 0) { setStatus("Please select a user first.", AppTheme.ACCENT_AMBER); return; }
        int userId = (int) tableModel.getValueAt(row, 0);
        try {
            OperationResult r = userService.activateUser(userId);
            setStatus(r.isSuccess() ? "✔  " + r.getMessage() : "✘  " + r.getMessage(),
                      r.isSuccess() ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED);
            refresh();
        } catch (SecurityException ex) {
            setStatus("✘  Admin only.", AppTheme.ACCENT_RED);
        }
    }
 
    private void changeRoleSelected() {
        int row = userTable.getSelectedRow();
        if (row < 0) { setStatus("Please select a user first.", AppTheme.ACCENT_AMBER); return; }
        int    userId   = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
 
        User.Role chosen = (User.Role) JOptionPane.showInputDialog(
                this,
                "Select new role for \"" + username + "\":",
                "Change Role",
                JOptionPane.QUESTION_MESSAGE,
                null,
                User.Role.values(),
                User.Role.VERIFIER);
        if (chosen == null) return;
 
        try {
            OperationResult r = userService.updateRole(userId, chosen);
            setStatus(r.isSuccess() ? "✔  " + r.getMessage() : "✘  " + r.getMessage(),
                      r.isSuccess() ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED);
            refresh();
        } catch (SecurityException ex) {
            setStatus("✘  Admin only.", AppTheme.ACCENT_RED);
        }
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────
 
    public void refresh() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userService.getAllUsers();
            for (User u : users) {
                String last = u.getLastLogin() != null
                        ? u.getLastLogin().toString().replace("T", "  ").substring(0, 19)
                        : "Never";
                tableModel.addRow(new Object[]{
                        u.getUserId(), u.getUsername(), u.getEmail(),
                        u.getRole(), u.getStatus(), last
                });
            }
            setStatus("Loaded " + users.size() + " user(s).", AppTheme.TEXT_MUTED);
        } catch (SecurityException ex) {
            setStatus("Admin access required to view users.", AppTheme.ACCENT_AMBER);
        }
    }
 
    private void clearForm() {
        newUsername.setText("");
        newPassword.setText("");
        newEmail.setText("");
        roleCombo.setSelectedIndex(0);
    }
 
    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }
 
    private void styleTable(JTable t) {
        t.setBackground(AppTheme.BG_TABLE_ODD);
        t.setForeground(AppTheme.TEXT_PRIMARY);
        t.setFont(AppTheme.FONT_BODY);
        t.setRowHeight(34);
        t.setGridColor(AppTheme.BORDER_COLOR);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(AppTheme.ACCENT_INDIGO.darker());
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(AppTheme.BG_SIDEBAR);
        t.getTableHeader().setForeground(AppTheme.ACCENT_INDIGO);
        t.getTableHeader().setFont(AppTheme.FONT_LABEL);
        t.getTableHeader().setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_TABLE_ODD : AppTheme.BG_TABLE_EVEN);
                setForeground(AppTheme.TEXT_PRIMARY);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
}
 