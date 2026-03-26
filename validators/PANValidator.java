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

        if (!requireField(doc, "panNumber", "PAN number", result)) return;

        String pan = doc.getField("panNumber").toUpperCase().trim();

        if (!matchesPattern(pan, "[A-Z]{5}[0-9]{4}[A-Z]",
                "PAN format is invalid. Expected format: ABCDE1234F (5 letters + 4 digits + 1 letter).",
                result)) return;

        char entityCode = pan.charAt(3);
        if (VALID_ENTITY_CODES.indexOf(entityCode) == -1) {
            result.addError("Invalid entity code '" + entityCode + "' at position 4 in PAN number.");
        }

        requireField(doc, "name", "Holder name", result);

        requireField(doc, "fathersName", "Father's name", result);
        
        requireField(doc, "dateOfBirth", "Date of birth", result);
    }
}
