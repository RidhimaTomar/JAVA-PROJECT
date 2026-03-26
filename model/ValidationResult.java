package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

   
    public String getFirstError() {
        return errors.isEmpty() ? "" : errors.get(0);
    }

   
    public String toString() {
        if (isPassed()) return "[PASSED]";
        return "[FAILED] " + String.join(" | ", errors);
    }
}

