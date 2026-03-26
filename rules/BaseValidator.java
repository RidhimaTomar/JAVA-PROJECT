package rules;

import model.DocumentData;
import model.ValidationResult;

/**
 * Every document validator extends this.
 * Contains small helper methods so validators don't repeat themselves.
 */
public abstract class BaseValidator {

    /** Subclasses implement this to run their specific rules. */
    public abstract void validate(DocumentData doc, ValidationResult result);

    // ── Shared helpers ────────────────────────────────────────────────────

    /**
     * Checks that a field exists and isn't blank.
     * Returns true if present so callers can early-return when missing.
     */
    protected boolean requireField(DocumentData doc, String field,
                                   String label, ValidationResult result) {
        if (!doc.hasField(field)) {
            result.addError(label + " is required.");
            return false;
        }
        return true;
    }

    /** Adds an error if the value doesn't match the given regex. */
    protected boolean matchesPattern(String value, String regex,
                                     String errorMsg, ValidationResult result) {
        if (value == null || !value.matches(regex)) {
            result.addError(errorMsg);
            return false;
        }
        return true;
    }

    /**
     * Very simple date parser — just splits on "/" or "-" and checks year.
     * Returns the year as an int, or -1 if unparseable.
     */
    protected int parseYear(String date, String separator) {
        try {
            String[] parts = date.split(separator);
            if (parts.length == 3) return Integer.parseInt(parts[2]);
        } catch (NumberFormatException ignored) { }
        return -1;
    }
}
