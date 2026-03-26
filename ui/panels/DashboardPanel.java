package ui.panels;
 
import database.DocumentRepository;
import model.VerificationSummary;
import ui.AppTheme;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
public class DashboardPanel extends JPanel {
 
    private final DocumentRepository docRepo;
 
    private JLabel totalLabel;
    private JLabel passedLabel;
    private JLabel failedLabel;
    private DefaultTableModel tableModel;
 
    public DashboardPanel(DocumentRepository docRepo) {
        this.docRepo = docRepo;
        buildUI();
    }
 
    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_DARK);
        setBorder(new EmptyBorder(24, 28, 24, 28));
 
        add(buildHeader(),     BorderLayout.NORTH);
        add(buildContent(),    BorderLayout.CENTER);
    }
 
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(0, 0, 8, 0));
 
        JLabel title = new JLabel("Dashboard");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
 
        JLabel sub = new JLabel("Overview of verification activity");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
 
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 3));
        txt.setOpaque(false);
        txt.add(title);
        txt.add(sub);
 
        JButton refresh = AppTheme.ghostButton("⟳  Refresh");
        refresh.addActionListener(e -> refresh());
 
        p.add(txt,     BorderLayout.WEST);
        p.add(refresh, BorderLayout.EAST);
        return p;
    }
 
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(AppTheme.BG_DARK);
        p.add(buildStatCards(), BorderLayout.NORTH);
        p.add(buildTable(),     BorderLayout.CENTER);
        return p;
    }
 
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setBackground(AppTheme.BG_DARK);
 
        totalLabel  = new JLabel("0", SwingConstants.CENTER);
        passedLabel = new JLabel("0", SwingConstants.CENTER);
        failedLabel = new JLabel("0", SwingConstants.CENTER);
 
        row.add(statCard("Total Verifications", totalLabel,  AppTheme.ACCENT_INDIGO));
        row.add(statCard("Passed",              passedLabel, AppTheme.ACCENT_GREEN));
        row.add(statCard("Failed",              failedLabel, AppTheme.ACCENT_RED));
        return row;
    }
 
    private JPanel statCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent.darker(), 1, true),
                new EmptyBorder(20, 24, 20, 24)));
 
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(AppTheme.FONT_LABEL);
        titleLbl.setForeground(AppTheme.TEXT_MUTED);
 
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);
 
        card.add(titleLbl,  BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
 
    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(AppTheme.BG_DARK);
 
        JLabel heading = new JLabel("Recent Verifications");
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        p.add(heading, BorderLayout.NORTH);
 
        String[] cols = {"Document Type", "Document #", "Holder Name", "Status", "Date & Time"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        JTable table = new JTable(tableModel);
        styleTable(table);
 
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                String s = val == null ? "" : val.toString();
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_TABLE_ODD : AppTheme.BG_TABLE_EVEN);
                setForeground("PASSED".equals(s) ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(AppTheme.BG_DARK);
        scroll.getViewport().setBackground(AppTheme.BG_TABLE_ODD);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }
 
    public void refresh() {
        try {
            totalLabel.setText(String.valueOf(docRepo.countTotal()));
            passedLabel.setText(String.valueOf(docRepo.countByStatus("PASSED")));
            failedLabel.setText(String.valueOf(docRepo.countByStatus("FAILED")));
 
            tableModel.setRowCount(0);
            List<VerificationSummary> recent = docRepo.getRecentResults(50);
            for (VerificationSummary s : recent) {
                String ts = s.getVerifiedAt() != null
                        ? s.getVerifiedAt().toString().replace("T", "  ").substring(0, 19)
                        : "—";
                tableModel.addRow(new Object[]{
                        s.getDocType(), s.getDocNumber(), s.getHolderName(), s.getStatus(), ts
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load dashboard data:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
 