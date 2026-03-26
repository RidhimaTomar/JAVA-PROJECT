package usermanagement;

import database.AuditLogger;
import database.DocumentRepository;
import engine.ValidationEngine;
import model.DocumentData;
import model.ValidationResult;
public class VerificationService {

    private static final int MAX_FAILURES_BEFORE_LOCK = 3;

    private final DocumentRepository docRepo;
    private final ValidationEngine   engine;
    private final UserService        userService;

    public VerificationService(DocumentRepository docRepo, UserService userService) {
        this.docRepo     = docRepo;
        this.engine      = new ValidationEngine();
        this.userService = userService;
    }

    public VerifyResult verify(DocumentData doc) {

        if (userService.getCurrentUser() == null) {
            return VerifyResult.failure("You must be logged in to verify documents.");
        }

        int userId = userService.getCurrentUser().getUserId();

        try {
            String dob = doc.hasField("dateOfBirth") ? doc.getField("dateOfBirth")
                       : doc.hasField("dob")         ? doc.getField("dob")
                       : "";

            String holderName = doc.hasField("name")      ? doc.getField("name")
                              : doc.hasField("childName") ? doc.getField("childName")
                              : "";

            String docNumber = doc.hasField("aadhaarNumber")      ? doc.getField("aadhaarNumber").replaceAll("\\s+", "")
                             : doc.hasField("panNumber")           ? doc.getField("panNumber")
                             : doc.hasField("licenseNumber")       ? doc.getField("licenseNumber")
                             : doc.hasField("certificateNumber")   ? doc.getField("certificateNumber")
                             : "";

            int docId = docRepo.saveDocument(userId, doc.getDocType(), docNumber, holderName, dob);
            if (docId == -1) return VerifyResult.failure("Failed to save document to database.");

            if (docRepo.isLocked(docId)) {
                return VerifyResult.failure("This document is locked due to too many failed attempts.");
            }
            ValidationResult vr = engine.validate(doc);

            int attemptId = docRepo.logAttempt(docId, userId);
            if (attemptId == -1) return VerifyResult.failure("Failed to log verification attempt.");
            String status = vr.isPassed() ? "PASSED" : "FAILED";
            String reason = vr.isPassed() ? null : vr.getFirstError();
            docRepo.saveResult(attemptId, docId, status, reason, null);

            AuditLogger.log("VERIFY_" + status, doc.getDocType(),
                    "Doc=" + docNumber + ", By=userId:" + userId);
            if (!vr.isPassed()) {
                int failCount = docRepo.getFailedCount(docId);
                if (failCount >= MAX_FAILURES_BEFORE_LOCK) {
                    docRepo.lockDocument(docId);
                    AuditLogger.log("DOC_LOCKED", docNumber,
                            "Locked after " + failCount + " failures");
                    return VerifyResult.locked(vr);
                }
            }

            return vr.isPassed() ? VerifyResult.passed() : VerifyResult.failed(vr);

        } catch (Exception e) {
            AuditLogger.log("VERIFY_ERROR", doc.getDocType(), e.getMessage());
            return VerifyResult.failure("Unexpected error: " + e.getMessage());
        }
    }
    public static class VerifyResult {

        public enum State { PASSED, FAILED, LOCKED, ERROR }

        private final State            state;
        private final ValidationResult validationResult;
        private final String           errorMessage;

        private VerifyResult(State state, ValidationResult vr, String err) {
            this.state            = state;
            this.validationResult = vr;
            this.errorMessage     = err;
        }

        public static VerifyResult passed()                     { return new VerifyResult(State.PASSED, null, null); }
        public static VerifyResult failed(ValidationResult vr)  { return new VerifyResult(State.FAILED, vr, null); }
        public static VerifyResult locked(ValidationResult vr)  { return new VerifyResult(State.LOCKED, vr, null); }
        public static VerifyResult failure(String msg)          { return new VerifyResult(State.ERROR, null, msg); }

        public boolean isPassed()    { return state == State.PASSED; }
        public boolean isFailed()    { return state == State.FAILED; }
        public boolean isLocked()    { return state == State.LOCKED; }
        public boolean isError()     { return state == State.ERROR; }

        public ValidationResult getValidationResult() { return validationResult; }
        public String           getErrorMessage()     { return errorMessage; }
        public State            getState()            { return state; }
    }
}
