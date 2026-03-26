package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects every error and warning that came out of a validation run.
 * After all validators have run, call isPassed() to get the final verdict.
 */
public class ValidationResult {

    private final List<String> errors   = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public void addWarning(String message) {
        warnings.add(message);
    }

    public boolean isPassed() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    /** First error message, or empty string if there are none. */
    public String getFirstError() {
        return errors.isEmpty() ? "" : errors.get(0);
    }

    @Override
    public String toString() {
        if (isPassed()) return "[PASSED]";
        return "[FAILED] " + String.join(" | ", errors);
    }
}

