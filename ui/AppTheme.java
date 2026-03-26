package ui;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
 
/**
 * Central theme registry.
 * Every color, font, and styled-component factory lives here.
 * Change one thing here and it ripples through the whole app.
 */
public class AppTheme {
 
    // ── Colours ────────────────────────────────────────────────────────────
    public static final Color BG_DARK         = new Color(18, 18, 24);
    public static final Color BG_PANEL        = new Color(28, 28, 38);
    public static final Color BG_CARD         = new Color(36, 36, 50);
    public static final Color BG_TABLE_ODD    = new Color(28, 28, 38);
    public static final Color BG_TABLE_EVEN   = new Color(33, 33, 46);
    public static final Color BG_SIDEBAR      = new Color(22, 22, 32);
    public static final Color BORDER_COLOR    = new Color(55, 55, 75);
    public static final Color BORDER_FOCUS    = new Color(99, 102, 241);
 
    public static final Color ACCENT_INDIGO   = new Color(99,  102, 241);
    public static final Color ACCENT_TEAL     = new Color(20,  184, 166);
    public static final Color ACCENT_GREEN    = new Color(34,  197, 94);
    public static final Color ACCENT_RED      = new Color(239, 68,  68);
    public static final Color ACCENT_AMBER    = new Color(245, 158, 11);
    public static final Color ACCENT_BLUE     = new Color(59,  130, 246);
    public static final Color ACCENT_PURPLE   = new Color(168, 85,  247);
 
    public static final Color TEXT_PRIMARY    = new Color(241, 241, 255);
    public static final Color TEXT_SECONDARY  = new Color(160, 160, 185);
    public static final Color TEXT_MUTED      = new Color(100, 100, 130);
 
    // ── Fonts ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_SUBHEAD  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO     = new Font("Consolas",  Font.PLAIN, 12);
 
    // ── Component factories ────────────────────────────────────────────────
 
    public static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return f;
    }
 
    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return f;
    }
 
    public static JTextArea styledTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setBackground(BG_CARD);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 12, 8, 12));
        return ta;
    }
 
    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, val, idx, sel, foc);
                setBackground(sel ? ACCENT_INDIGO : BG_CARD);
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(6, 12, 6, 12));
                return this;
            }
        });
        return cb;
    }
 
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_SUBHEAD);
        b.setBackground(ACCENT_INDIGO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setOpaque(true);
        return b;
    }
 
    public static JButton successButton(String text) {
        JButton b = primaryButton(text);
        b.setBackground(ACCENT_TEAL);
        b.setForeground(Color.WHITE);
        return b;
    }
 
    public static JButton dangerButton(String text) {
        JButton b = primaryButton(text);
        b.setBackground(ACCENT_RED);
        return b;
    }
 
    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(BG_PANEL);
        b.setForeground(TEXT_SECONDARY);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(7, 16, 7, 16)));
        b.setOpaque(true);
        return b;
    }
 
    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 24, 20, 24)));
        return p;
    }
 
    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBHEAD);
        l.setForeground(TEXT_MUTED);
        return l;
    }
 
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }
 
    /** Sets Swing system look-and-feel defaults so the whole app looks dark. */
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",             BG_DARK);
        UIManager.put("ScrollPane.background",        BG_DARK);
        UIManager.put("Viewport.background",          BG_DARK);
        UIManager.put("ScrollBar.thumb",              BORDER_COLOR);
        UIManager.put("ScrollBar.track",              BG_PANEL);
        UIManager.put("OptionPane.background",        BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background",            BG_PANEL);
        UIManager.put("Button.foreground",            TEXT_PRIMARY);
        UIManager.put("Label.foreground",             TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",    TEXT_PRIMARY);
        UIManager.put("ComboBox.background",          BG_CARD);
        UIManager.put("ComboBox.foreground",          TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT_INDIGO);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("Table.background",             BG_TABLE_ODD);
        UIManager.put("Table.foreground",             TEXT_PRIMARY);
        UIManager.put("Table.gridColor",              BORDER_COLOR);
        UIManager.put("TableHeader.background",       BG_SIDEBAR);
        UIManager.put("TableHeader.foreground",       ACCENT_INDIGO);
        UIManager.put("SplitPane.background",         BG_DARK);
        UIManager.put("TabbedPane.background",        BG_DARK);
        UIManager.put("TabbedPane.foreground",        TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",          BG_PANEL);
    }
}
 