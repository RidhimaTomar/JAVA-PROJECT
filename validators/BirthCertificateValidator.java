package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

/**
 * Validates Birth Certificate submissions.
 *
 * Rules:
 *   1. Certificate number must be present
 *   2. Child's name must be present
 *   3. Date of birth must be present and in DD/MM/YYYY format
 *   4. Father's name must be present
 *   5. Mother's name must be present
 *   6. Place of birth must be present
 *   7. Issuing authority must be present
 */
public class BirthCertificateValidator extends BaseValidator {

    @Override
    public void validate(DocumentData doc, ValidationResult result) {

        // Rule 1
        requireField(doc, "certificateNumber", "Certificate number", result);

        // Rule 2
        requireField(doc, "childName", "Child's name", result);

        // Rule 3
        if (!requireField(doc, "dateOfBirth", "Date of birth", result)) return;
        String dob = doc.getField("dateOfBirth");
        matchesPattern(dob, "\\d{2}/\\d{2}/\\d{4}",
                "Date of birth must be in DD/MM/YYYY format.", result);

        // Rule 4
        requireField(doc, "fathersName", "Father's name", result);

        // Rule 5
        requireField(doc, "mothersName", "Mother's name", result);

        // Rule 6
        requireField(doc, "placeOfBirth", "Place of birth", result);

        // Rule 7
        requireField(doc, "issuingAuthority", "Issuing authority", result);
    }
}
