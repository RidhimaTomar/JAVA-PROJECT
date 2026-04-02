package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

public class AadhaarValidator extends BaseValidator {

    public void validate(DocumentData doc, ValidationResult result) {

        if (!requireField(doc, "aadhaarNumber", "Aadhaar number", result)) return;

        String raw    = doc.getField("aadhaarNumber");
        String number = raw.replaceAll("\\s+", "");

        if (!matchesPattern(number, "\\d{12}",
                "Aadhaar number must be exactly 12 digits (got " + number.length() + ").",
                result)) return;

        char first = number.charAt(0);
        if (first == '0' || first == '1') {
            result.addError("Aadhaar number cannot start with 0 or 1 as per UIDAI rules.");
        }

        requireField(doc, "name", "Holder name", result);

        requireField(doc, "dateOfBirth", "Date of birth", result);
        
        requireField(doc, "address", "Address", result);
    }
}
