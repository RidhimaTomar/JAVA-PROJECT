package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

/**
 * Validates PAN Card submissions.
 *
 * Rules:
 *   1. PAN number must be present
 *   2. Format: AAAAA9999A — 5 uppercase letters, 4 digits, 1 uppercase letter
 *   3. 4th character must be a valid entity-type code
 *   4. Name must be present
 *   5. Father's name must be present
 *   6. Date of birth must be present
 */
public class PANValidator extends BaseValidator {

    // P=Person, C=Company, H=HUF, A=AOP, B=BOI, G=Govt, J=AJP, L=LocalAuth, F=Firm, T=Trust
    private static final String VALID_ENTITY_CODES = "PCHABGJLFT";

    @Override
    public void validate(DocumentData doc, ValidationResult result) {

        // Rule 1
        if (!requireField(doc, "panNumber", "PAN number", result)) return;

        String pan = doc.getField("panNumber").toUpperCase().trim();

        // Rule 2
        if (!matchesPattern(pan, "[A-Z]{5}[0-9]{4}[A-Z]",
                "PAN format is invalid. Expected format: ABCDE1234F (5 letters + 4 digits + 1 letter).",
                result)) return;

        // Rule 3 — 4th char (index 3) is the entity code
        char entityCode = pan.charAt(3);
        if (VALID_ENTITY_CODES.indexOf(entityCode) == -1) {
            result.addError("Invalid entity code '" + entityCode + "' at position 4 in PAN number.");
        }

        // Rule 4
        requireField(doc, "name", "Holder name", result);

        // Rule 5
        requireField(doc, "fathersName", "Father's name", result);

        // Rule 6
        requireField(doc, "dateOfBirth", "Date of birth", result);
    }
}
