package database;

import model.DocumentRecord;
import model.VerificationSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all DB reads/writes for documents, attempts, and results.
 */
public class DocumentRepository {

    // ── Documents ────────────────────────────────────────────────────────

    public int saveDocument(int submittedBy, String docType, String docNumber,
                            String holderName, String dob) throws SQLException {
        String sql = "INSERT INTO documents (submitted_by, doc_type, doc_number, holder_name, dob) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, submittedBy);
            ps.setString(2, docType);
            ps.setString(3, docNumber);
            ps.setString(4, holderName);
            ps.setString(5, (dob == null || dob.isBlank()) ? null : dob);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean isDuplicate(String docNumber, String docType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM documents WHERE doc_number = ? AND doc_type = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, docNumber);
            ps.setString(2, docType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public boolean isLocked(int docId) throws SQLException {
        String sql = "SELECT is_locked FROM documents WHERE doc_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, docId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBoolean("is_locked");
        }
        return false;
    }

    public void lockDocument(int docId) throws SQLException {
        String sql = "UPDATE documents SET is_locked = TRUE WHERE doc_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, docId);
            ps.executeUpdate();
        }
    }

    // ── Attempts ─────────────────────────────────────────────────────────

    public int logAttempt(int docId, int userId) throws SQLException {
        int next = getAttemptCount(docId) + 1;
        String sql = "INSERT INTO verification_attempts (doc_id, verified_by, attempt_number) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, docId);
            ps.setInt(2, userId);
            ps.setInt(3, next);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public int getAttemptCount(int docId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM verification_attempts WHERE doc_id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, docId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getFailedCount(int docId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM verification_results vr "
                   + "JOIN verification_attempts va ON vr.attempt_id = va.attempt_id "
                   + "WHERE va.doc_id = ? AND vr.status = 'FAILED'";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, docId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // ── Results ───────────────────────────────────────────────────────────

    public void saveResult(int attemptId, int docId, String status,
                           String reason, String failedRule) throws SQLException {
        String sql = "INSERT INTO verification_results "
                   + "(attempt_id, doc_id, status, rejection_reason, failed_rule) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, docId);
            ps.setString(3, status);
            ps.setString(4, reason);
            ps.setString(5, failedRule);
            ps.executeUpdate();
        }
    }

    // ── Dashboard data ────────────────────────────────────────────────────

    public List<VerificationSummary> getRecentResults(int limit) throws SQLException {
        String sql = "SELECT d.doc_type, d.doc_number, d.holder_name, "
                   + "vr.status, vr.rejection_reason, vr.verified_at "
                   + "FROM verification_results vr "
                   + "JOIN verification_attempts va ON vr.attempt_id = va.attempt_id "
                   + "JOIN documents d ON vr.doc_id = d.doc_id "
                   + "ORDER BY vr.verified_at DESC LIMIT ?";
        List<VerificationSummary> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                VerificationSummary s = new VerificationSummary();
                s.setDocType(rs.getString("doc_type"));
                s.setDocNumber(rs.getString("doc_number"));
                s.setHolderName(rs.getString("holder_name"));
                s.setStatus(rs.getString("status"));
                s.setRejectionReason(rs.getString("rejection_reason"));
                Timestamp ts = rs.getTimestamp("verified_at");
                if (ts != null) s.setVerifiedAt(ts.toLocalDateTime());
                list.add(s);
            }
        }
        return list;
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM verification_results WHERE status = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM verification_results";
        try (Connection c = DBConnection.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<DocumentRecord> getAllDocuments() throws SQLException {
        String sql = "SELECT d.*, u.username FROM documents d "
                   + "JOIN users u ON d.submitted_by = u.user_id "
                   + "ORDER BY d.submitted_at DESC";
        List<DocumentRecord> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DocumentRecord r = new DocumentRecord();
                r.setDocId(rs.getInt("doc_id"));
                r.setDocType(rs.getString("doc_type"));
                r.setDocNumber(rs.getString("doc_number"));
                r.setHolderName(rs.getString("holder_name"));
                r.setLocked(rs.getBoolean("is_locked"));
                r.setSubmittedBy(rs.getString("username"));
                Timestamp ts = rs.getTimestamp("submitted_at");
                if (ts != null) r.setSubmittedAt(ts.toLocalDateTime());
                list.add(r);
            }
        }
        return list;
    }
}
