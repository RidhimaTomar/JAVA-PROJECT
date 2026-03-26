package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

/**
 * Validates Aadhaar Card submissions.
 *
 * Rules:
 *   1. Aadhaar number must be present
 *   2. Must be exactly 12 digits (spaces allowed in input, stripped before check)
 *   3. First digit cannot be 0 or 1 — UIDAI restriction
 *   4. Name must be present
 *   5. Date of birth must be present
 *   6. Address must be present
 */
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
