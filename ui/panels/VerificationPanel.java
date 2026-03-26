package ui.panels;

import model.DocumentData;
import ui.AppTheme;
import usermanagement.UserService;
import usermanagement.VerificationService;
import usermanagement.VerificationService.VerifyResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
public class VerificationPanel extends JPanel {

    private final VerificationService verifyService;
    private final UserService userService;
    private JComboBox<String> docTypeCombo;
    private JPanel            fieldsPanel;
    private JButton           verifyBtn;
    private JButton           clearBtn;
    private JPanel resultCard;
    private JLabel resultIcon;
    private JLabel resultTitle;
    private JLabel resultDetail;
    private JLabel statusBar;

    private static final Map<String, String[][]> DOC_FIELDS = new LinkedHashMap<>();
    static {
        DOC_FIELDS.put("AADHAAR", new String[][]{
            {"Aadhaar Number *",  "aadhaarNumber"},
            {"Full Name *",       "name"},
            {"Date of Birth *",   "dateOfBirth"},
            {"Address *",         "address"}
        });
        DOC_FIELDS.put("PAN", new String[][]{
            {"PAN Number *",      "panNumber"},
            {"Full Name *",       "name"},
            {"Father's Name *",   "fathersName"},
            {"Date of Birth *",   "dateOfBirth"}
        });
        DOC_FIELDS.put("DRIVING_LICENSE", new String[][]{
            {"License Number *",  "licenseNumber"},
            {"Full Name *",       "name"},
            {"Date of Birth *",   "dateOfBirth"},
            {"Issue Date *",      "issueDate"},
            {"Expiry Date *",     "expiryDate"},
            {"Vehicle Class *",   "vehicleClass"},
            {"Address *",         "address"}
        });
        DOC_FIELDS.put("BIRTH_CERTIFICATE", new String[][]{
            {"Certificate No. *", "certificateNumber"},
            {"Child's Name *",    "childName"},
            {"Date of Birth *",   "dateOfBirth"},
            {"Father's Name *",   "fathersName"},
            {"Mother's Name *",   "mothersName"},
            {"Place of Birth *",  "placeOfBirth"},
            {"Issuing Authority *","issuingAuthority"}
        });
    }

    public VerificationPanel(VerificationService verifyService, UserService userService) {
        this.verifyService = verifyService;
        this.userService   = userService;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_DARK);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 12, 28));

        JLabel title = new JLabel("Verify Document");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Enter document details below and click Verify");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);

        JPanel block = new JPanel(new GridLayout(2, 1, 0, 3));
        block.setOpaque(false);
        block.add(title);
        block.add(sub);
        p.add(block, BorderLayout.WEST);
        return p;
    }

    private JSplitPane buildBody() {
        JScrollPane formSide   = buildFormSide();
        JPanel      resultSide = buildResultSide();

        rebuildFields();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formSide, resultSide);
        split.setDividerLocation(480);
        split.setDividerSize(3);
        split.setBackground(AppTheme.BG_DARK);
        split.setBorder(null);
        return split;
    }

    private JScrollPane buildFormSide() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppTheme.BG_DARK);
        outer.setBorder(new EmptyBorder(8, 28, 8, 12));

        JPanel card = AppTheme.cardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx   = 0;
        JLabel typeLbl = AppTheme.formLabel("Document Type");
        c.gridy = 0; c.insets = new Insets(0, 0, 4, 0);
        card.add(typeLbl, c);

        docTypeCombo = AppTheme.styledCombo(DOC_FIELDS.keySet().toArray(new String[0]));
        c.gridy = 1; c.insets = new Insets(0, 0, 18, 0);
        card.add(docTypeCombo, c);
        fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        c.gridy = 2; c.insets = new Insets(0, 0, 18, 0); c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        card.add(fieldsPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL; c.weighty = 0;

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        btns.setOpaque(false);
        verifyBtn = AppTheme.primaryButton("✔  Verify");
        clearBtn  = AppTheme.ghostButton("✕  Clear");
        btns.add(verifyBtn);
        btns.add(clearBtn);
        c.gridy = 3; c.insets = new Insets(0, 0, 0, 0);
        card.add(btns, c);

        outer.add(card, BorderLayout.NORTH);

        docTypeCombo.addActionListener(e -> rebuildFields());
        verifyBtn.addActionListener(e -> runVerification());
        clearBtn.addActionListener(e  -> clearForm());

        JScrollPane sp = new JScrollPane(outer);
        sp.setBackground(AppTheme.BG_DARK);
        sp.getViewport().setBackground(AppTheme.BG_DARK);
        sp.setBorder(null);
        return sp;
    }
    private JPanel buildResultSide() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppTheme.BG_DARK);
        outer.setBorder(new EmptyBorder(8, 12, 8, 28));

        resultCard = AppTheme.cardPanel();
        resultCard.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;

        resultIcon = new JLabel("⬤", SwingConstants.CENTER);
        resultIcon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        resultIcon.setForeground(AppTheme.BORDER_COLOR);
        c.gridy = 0; c.insets = new Insets(20, 0, 12, 0);
        resultCard.add(resultIcon, c);

        resultTitle = new JLabel("Awaiting verification", SwingConstants.CENTER);
        resultTitle.setFont(AppTheme.FONT_HEADING);
        resultTitle.setForeground(AppTheme.TEXT_MUTED);
        c.gridy = 1; c.insets = new Insets(0, 0, 10, 0);
        resultCard.add(resultTitle, c);

        resultDetail = new JLabel("<html><center>Fill in the form on the left<br>and click Verify.</center></html>",
                SwingConstants.CENTER);
        resultDetail.setFont(AppTheme.FONT_BODY);
        resultDetail.setForeground(AppTheme.TEXT_MUTED);
        c.gridy = 2; c.insets = new Insets(0, 0, 20, 0);
        resultCard.add(resultDetail, c);

        outer.add(resultCard, BorderLayout.NORTH);
        return outer;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(AppTheme.BG_SIDEBAR);
        p.setBorder(new EmptyBorder(4, 20, 4, 20));
        statusBar = new JLabel("Ready");
        statusBar.setFont(AppTheme.FONT_SMALL);
        statusBar.setForeground(AppTheme.TEXT_MUTED);
        p.add(statusBar);
        return p;
    }

    private void rebuildFields() {
        fieldsPanel.removeAll();
        String docType = (String) docTypeCombo.getSelectedItem();
        String[][] spec = DOC_FIELDS.get(docType);
        if (spec == null) return;

        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx   = 0;

        for (int i = 0; i < spec.length; i++) {
            JLabel lbl = AppTheme.formLabel(spec[i][0]);
            c.gridy = i * 2; c.insets = new Insets(0, 0, 3, 0);
            fieldsPanel.add(lbl, c);

            JTextField field = AppTheme.styledField(20);
            field.setName(spec[i][1]);
            addPlaceholder(field, spec[i][0].replace(" *", ""));
            c.gridy = i * 2 + 1; c.insets = new Insets(0, 0, 12, 0);
            fieldsPanel.add(field, c);
        }

        fieldsPanel.revalidate();
        fieldsPanel.repaint();
        showNeutralResult();
    }

    private void addPlaceholder(JTextField field, String hint) {
        field.setForeground(AppTheme.TEXT_MUTED);
        field.setText(hint);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(AppTheme.TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setForeground(AppTheme.TEXT_MUTED);
                    field.setText(hint);
                }
            }
        });
    }
    private void runVerification() {
        String docType = (String) docTypeCombo.getSelectedItem();
        DocumentData data = new DocumentData(docType);

        for (Component comp : fieldsPanel.getComponents()) {
            if (comp instanceof JTextField field) {
                String key   = field.getName();
                String value = field.getText().trim();
                if (!value.isEmpty() && !value.equals(getPlaceholder(field))) {
                    data.addField(key, value);
                }
            }
        }

        verifyBtn.setEnabled(false);
        setStatus("Verifying…");

        new SwingWorker<VerifyResult, Void>() {
            protected VerifyResult doInBackground() {
                return verifyService.verify(data);
            }
               protected void done() {
                try {
                    VerifyResult r = get();
                    showResult(r);
                } catch (Exception ex) {
                    showResultError("Error: " + ex.getMessage());
                } finally {
                    verifyBtn.setEnabled(true);
                    setStatus("Ready");
                }
            }
        }.execute();
    }

    private String getPlaceholder(JTextField field) {
        String docType = (String) docTypeCombo.getSelectedItem();
        String[][] spec = DOC_FIELDS.get(docType);
        if (spec != null) {
            for (String[] entry : spec) {
                if (entry[1].equals(field.getName())) return entry[0].replace(" *", "");
            }
        }
        return "";
    }

    private void showResult(VerifyResult r) {
        switch (r.getState()) {
            case PASSED -> {
                resultIcon.setText("✔");
                resultIcon.setForeground(AppTheme.ACCENT_GREEN);
                resultTitle.setText("Document Verified");
                resultTitle.setForeground(AppTheme.ACCENT_GREEN);
                resultDetail.setText("<html><center>All validation rules passed.<br>Document is authentic.</center></html>");
                resultDetail.setForeground(AppTheme.TEXT_SECONDARY);
                resultCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.ACCENT_GREEN, 1, true),
                        new EmptyBorder(20, 24, 20, 24)));
            }
            case FAILED -> {
                resultIcon.setText("✘");
                resultIcon.setForeground(AppTheme.ACCENT_RED);
                resultTitle.setText("Verification Failed");
                resultTitle.setForeground(AppTheme.ACCENT_RED);
                String errors = String.join("<br>• ", r.getValidationResult().getErrors());
                resultDetail.setText("<html><center>• " + errors + "</center></html>");
                resultDetail.setForeground(AppTheme.ACCENT_RED);
                resultCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.ACCENT_RED, 1, true),
                        new EmptyBorder(20, 24, 20, 24)));
            }
            case LOCKED -> {
                resultIcon.setText("🔒");
                resultIcon.setForeground(AppTheme.ACCENT_AMBER);
                resultTitle.setText("Document Locked");
                resultTitle.setForeground(AppTheme.ACCENT_AMBER);
                resultDetail.setText("<html><center>This document has been locked<br>due to too many failed attempts.</center></html>");
                resultDetail.setForeground(AppTheme.ACCENT_AMBER);
                resultCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.ACCENT_AMBER, 1, true),
                        new EmptyBorder(20, 24, 20, 24)));
            }
            default -> showResultError(r.getErrorMessage());
        }
        resultCard.revalidate();
        resultCard.repaint();
    }

    private void showResultError(String msg) {
        resultIcon.setText("⚠");
        resultIcon.setForeground(AppTheme.ACCENT_AMBER);
        resultTitle.setText("System Error");
        resultTitle.setForeground(AppTheme.ACCENT_AMBER);
        resultDetail.setText("<html><center>" + msg + "</center></html>");
        resultDetail.setForeground(AppTheme.TEXT_SECONDARY);
    }

    private void showNeutralResult() {
        resultIcon.setText("⬤");
        resultIcon.setForeground(AppTheme.BORDER_COLOR);
        resultTitle.setText("Awaiting verification");
        resultTitle.setForeground(AppTheme.TEXT_MUTED);
        resultDetail.setText("<html><center>Fill in the form and click Verify.</center></html>");
        resultDetail.setForeground(AppTheme.TEXT_MUTED);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(20, 24, 20, 24)));
    }

    private void clearForm() {
        rebuildFields();
    }

    private void setStatus(String msg) {
        statusBar.setText(msg);
    }
}
