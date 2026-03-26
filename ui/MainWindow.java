package ui;
 
import database.DocumentRepository;
import database.UserRepository;
import ui.panels.*;
import usermanagement.UserService;
import usermanagement.VerificationService;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
 
/**
 * The main application window.
 *
 * Layout:
 *   ┌──────────────────────────────────────────────────────────────┐
 *   │  Sidebar  │              Main content area                   │
 *   │           │  (swapped out via CardLayout)                    │
 *   └──────────────────────────────────────────────────────────────┘
 *
 * The sidebar contains nav buttons that swap the visible panel.
 * Everything wires together here — services, repos, panels.
 */
public class MainWindow extends JFrame {
 
    // ── Services / repos ─────────────────────────────────────────────────
    private final UserRepository      userRepo    = new UserRepository();
    private final DocumentRepository  docRepo     = new DocumentRepository();
    private final UserService         userService = new UserService(userRepo);
    private final VerificationService verifyService = new VerificationService(docRepo, userService);
 
    // ── Panels ────────────────────────────────────────────────────────────
    private LoginPanel          loginPanel;
    private DashboardPanel      dashboardPanel;
    private VerificationPanel   verifyPanel;
    private UserManagementPanel userMgmtPanel;
    private RecordsPanel        recordsPanel;
 
    // ── Nav ───────────────────────────────────────────────────────────────
    private JPanel   mainArea;
    private JPanel   sidebar;
    private JButton  activeNavBtn;
 
    private static final String CARD_LOGIN   = "LOGIN";
    private static final String CARD_DASH    = "DASHBOARD";
    private static final String CARD_VERIFY  = "VERIFY";
    private static final String CARD_USERS   = "USERS";
    private static final String CARD_RECORDS = "RECORDS";
 
    public MainWindow() {
        super("DocVerify — Document Verification System");
        AppTheme.applyGlobalDefaults();
        initPanels();
        buildFrame();
        showLogin();
    }
 
    private void initPanels() {
        loginPanel    = new LoginPanel(userService, this::onLoginSuccess);
        dashboardPanel = new DashboardPanel(docRepo);
        verifyPanel   = new VerificationPanel(verifyService, userService);
        userMgmtPanel = new UserManagementPanel(userService);
        recordsPanel  = new RecordsPanel(docRepo);
    }
 
    private void buildFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_DARK);
 
        // CardLayout wrapper holds all screens
        mainArea = new JPanel(new CardLayout());
        mainArea.setBackground(AppTheme.BG_DARK);
        mainArea.add(loginPanel,    CARD_LOGIN);
        mainArea.add(buildAppShell(), "SHELL");
 
        setContentPane(mainArea);
    }
 
    /**
     * The "shell" is the full app layout (sidebar + content area).
     * It is only shown after a successful login.
     */
    private JPanel buildAppShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(AppTheme.BG_DARK);
        shell.add(buildSidebar(), BorderLayout.WEST);
        shell.add(buildContentArea(), BorderLayout.CENTER);
        return shell;
    }
 
    // ── Sidebar ───────────────────────────────────────────────────────────
 
    private JPanel buildSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER_COLOR));
 
        // brand
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        brand.setBackground(AppTheme.BG_SIDEBAR);
        brand.setBorder(new EmptyBorder(28, 0, 28, 0));
        JLabel logo = new JLabel("DocVerify");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(AppTheme.ACCENT_INDIGO);
        brand.add(logo);
        sidebar.add(brand);
 
        // nav separator
        sidebar.add(separator());
 
        // nav items
        addNavItem("⊞  Dashboard",   CARD_DASH);
        addNavItem("✔  Verify",       CARD_VERIFY);
        addNavItem("📋  Records",      CARD_RECORDS);
        addNavItem("👤  Users",        CARD_USERS);
 
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(separator());
 
        // user info + logout at bottom
        sidebar.add(buildSidebarFooter());
 
        return sidebar;
    }
 
    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER_COLOR);
        sep.setBackground(AppTheme.BG_SIDEBAR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
 
    private void addNavItem(String label, String card) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setFont(AppTheme.FONT_BODY);
        btn.setBackground(AppTheme.BG_SIDEBAR);
        btn.setForeground(AppTheme.TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setOpaque(true);
 
        btn.addActionListener(e -> {
            setActiveNav(btn);
            showCard(card);
            if (CARD_DASH.equals(card))    dashboardPanel.refresh();
            if (CARD_RECORDS.equals(card)) recordsPanel.refresh();
            if (CARD_USERS.equals(card))   userMgmtPanel.refresh();
        });
 
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(AppTheme.BG_PANEL);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(AppTheme.BG_SIDEBAR);
            }
        });
 
        sidebar.add(btn);
    }
 
    private void setActiveNav(JButton selected) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(AppTheme.BG_SIDEBAR);
            activeNavBtn.setForeground(AppTheme.TEXT_SECONDARY);
        }
        activeNavBtn = selected;
        activeNavBtn.setBackground(AppTheme.ACCENT_INDIGO.darker().darker());
        activeNavBtn.setForeground(Color.WHITE);
    }
 
    private JPanel buildSidebarFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_SIDEBAR);
        p.setBorder(new EmptyBorder(12, 20, 20, 20));
 
        JLabel userLabel = new JLabel();
        userLabel.setFont(AppTheme.FONT_SMALL);
        userLabel.setForeground(AppTheme.TEXT_MUTED);
        if (userService.getCurrentUser() != null) {
            userLabel.setText(userService.getCurrentUser().getUsername()
                    + "  (" + userService.getCurrentUser().getRole() + ")");
        }
 
        JButton logoutBtn = AppTheme.ghostButton("Sign out");
        logoutBtn.addActionListener(e -> logout());
 
        p.add(userLabel,  BorderLayout.NORTH);
        p.add(logoutBtn,  BorderLayout.SOUTH);
        return p;
    }
 
    // ── Content area ──────────────────────────────────────────────────────
 
    private JPanel buildContentArea() {
        JPanel area = new JPanel(new CardLayout());
        area.setBackground(AppTheme.BG_DARK);
        area.add(dashboardPanel, CARD_DASH);
        area.add(verifyPanel,    CARD_VERIFY);
        area.add(recordsPanel,   CARD_RECORDS);
        area.add(userMgmtPanel,  CARD_USERS);
        return area;
    }
 
    private void showCard(String card) {
        // find the content area panel inside the shell and switch it
        JPanel shell = (JPanel) mainArea.getComponent(1);
        JPanel content = (JPanel) shell.getComponent(1);
        ((CardLayout) content.getLayout()).show(content, card);
    }
 
    // ── Login / logout flow ───────────────────────────────────────────────
 
    private void showLogin() {
        ((CardLayout) mainArea.getLayout()).show(mainArea, CARD_LOGIN);
    }
 
    private void onLoginSuccess() {
        // rebuild sidebar footer so it shows the logged-in user name
        rebuildSidebar();
        ((CardLayout) mainArea.getLayout()).show(mainArea, "SHELL");
        dashboardPanel.refresh();
        // highlight dashboard nav button
        // (we'll click the first nav item programmatically)
        Component[] comps = sidebar.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JButton btn && btn.getText().contains("Dashboard")) {
                btn.doClick();
                break;
            }
        }
    }
 
    private void logout() {
        userService.logout();
        loginPanel.reset();
        showLogin();
    }
 
    private void rebuildSidebar() {
        // Update the user label in the footer
        if (userService.getCurrentUser() != null) {
            // walk the sidebar to find the footer and update label
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JPanel footer) {
                    for (Component inner : footer.getComponents()) {
                        if (inner instanceof JLabel lbl &&
                                !lbl.getText().isEmpty() &&
                                lbl.getFont().equals(AppTheme.FONT_SMALL)) {
                            lbl.setText(userService.getCurrentUser().getUsername()
                                    + "  (" + userService.getCurrentUser().getRole() + ")");
                        }
                    }
                }
            }
        }
    }
}
 