package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

public class PANValidator extends BaseValidator {
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
