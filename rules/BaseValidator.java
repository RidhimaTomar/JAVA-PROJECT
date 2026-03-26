package rules;

import model.DocumentData;
import model.ValidationResult;

public abstract class BaseValidator {

    
    public abstract void validate(DocumentData doc, ValidationResult result);


    protected boolean requireField(DocumentData doc, String field,
                                   String label, ValidationResult result) {
        if (!doc.hasField(field)) {
            result.addError(label + " is required.");
            return false;
        }
        return true;
    }

   
    protected boolean matchesPattern(String value, String regex,
                                     String errorMsg, ValidationResult result) {
        if (value == null || !value.matches(regex)) {
            result.addError(errorMsg);
            return false;
        }
        return true;
    }

    protected int parseYear(String date, String separator) {
        try {
            String[] parts = date.split(separator);
            if (parts.length == 3) return Integer.parseInt(parts[2]);
        } catch (NumberFormatException ignored) { }
        return -1;
    }
}
