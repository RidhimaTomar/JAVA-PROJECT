package ui.panels;
 
import database.DocumentRepository;
import model.DocumentRecord;
import ui.AppTheme;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class RecordsPanel extends JPanel {
 
    private final DocumentRepository docRepo;
 
    private DefaultTableModel  tableModel;
    private JTable             table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel             statusBar;
 
    public RecordsPanel(DocumentRepository docRepo) {
        this.docRepo = docRepo;
        buildUI();
        refresh();
    }
 
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_DARK);
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTable(),     BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }
 
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 12, 28));
 
        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("Document Records");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("All submitted documents and their verification history");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(sub);
 
        JTextField search = AppTheme.styledField(24);
        search.setPreferredSize(new Dimension(260, 36));
        addPlaceholder(search, "Search by name, type, document #…");
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(search); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(search); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(search); }
        });
 
        JButton refreshBtn = AppTheme.ghostButton("⟳  Refresh");
        refreshBtn.addActionListener(e -> refresh());
 
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(search);
        right.add(refreshBtn);
 
        p.add(titleBlock, BorderLayout.WEST);
        p.add(right,      BorderLayout.EAST);
        return p;
    }
 
    private JScrollPane buildTable() {
        String[] cols = {"Doc ID", "Type", "Document #", "Holder Name", "Submitted By", "Submitted At", "Locked"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        table  = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        styleTable(table);
   
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel) setBackground(row % 2 == 0 ? AppTheme.BG_TABLE_ODD : AppTheme.BG_TABLE_EVEN);
                boolean locked = Boolean.TRUE.equals(val);
                setText(locked ? "🔒 Yes" : "No");
                setForeground(locked ? AppTheme.ACCENT_RED : AppTheme.ACCENT_GREEN);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(AppTheme.BG_DARK);
        sp.getViewport().setBackground(AppTheme.BG_TABLE_ODD);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_COLOR));
        return sp;
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
 
    public void refresh() {
        tableModel.setRowCount(0);
        try {
            List<DocumentRecord> records = docRepo.getAllDocuments();
            for (DocumentRecord r : records) {
                String ts = r.getSubmittedAt() != null
                        ? r.getSubmittedAt().toString().replace("T", "  ").substring(0, 19)
                        : "—";
                tableModel.addRow(new Object[]{
                        r.getDocId(), r.getDocType(), r.getDocNumber(),
                        r.getHolderName(), r.getSubmittedBy(), ts, r.isLocked()
                });
            }
            statusBar.setText("Showing " + records.size() + " record(s).");
            statusBar.setForeground(AppTheme.TEXT_MUTED);
        } catch (Exception e) {
            statusBar.setText("Error loading records: " + e.getMessage());
            statusBar.setForeground(AppTheme.ACCENT_RED);
        }
    }
 
    private void filter(JTextField searchBox) {
        String text = searchBox.getText().trim();
        if (text.isEmpty() || text.equals("Search by name, type, document #…")) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
 
    private void addPlaceholder(JTextField f, String hint) {
        f.setForeground(AppTheme.TEXT_MUTED);
        f.setText(hint);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(AppTheme.TEXT_PRIMARY); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isBlank()) { f.setText(hint); f.setForeground(AppTheme.TEXT_MUTED); }
            }
        });
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
 