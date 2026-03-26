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

    @Override
    public void validate(DocumentData doc, ValidationResult result) {

        // Rule 1 — number must exist
        if (!requireField(doc, "aadhaarNumber", "Aadhaar number", result)) return;

        String raw    = doc.getField("aadhaarNumber");
        String number = raw.replaceAll("\\s+", "");   // strip spaces

        // Rule 2 — exactly 12 digits
        if (!matchesPattern(number, "\\d{12}",
                "Aadhaar number must be exactly 12 digits (got " + number.length() + ").",
                result)) return;

        // Rule 3 — can't start with 0 or 1
        char first = number.charAt(0);
        if (first == '0' || first == '1') {
            result.addError("Aadhaar number cannot start with 0 or 1 as per UIDAI rules.");
        }

        // Rule 4 — name
        requireField(doc, "name", "Holder name", result);

        // Rule 5 — DOB
        requireField(doc, "dateOfBirth", "Date of birth", result);

        // Rule 6 — address
        requireField(doc, "address", "Address", result);
    }
}
