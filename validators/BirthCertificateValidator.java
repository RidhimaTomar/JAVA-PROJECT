package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

public class BirthCertificateValidator extends BaseValidator {

    @Override
    public void validate(DocumentData doc, ValidationResult result) {

        requireField(doc, "certificateNumber", "Certificate number", result);

        requireField(doc, "childName", "Child's name", result);

        if (!requireField(doc, "dateOfBirth", "Date of birth", result)) return;
        String dob = doc.getField("dateOfBirth");
        matchesPattern(dob, "\\d{2}/\\d{2}/\\d{4}",
                "Date of birth must be in DD/MM/YYYY format.", result);

        requireField(doc, "fathersName", "Father's name", result);

        requireField(doc, "mothersName", "Mother's name", result);

        requireField(doc, "placeOfBirth", "Place of birth", result);

        requireField(doc, "issuingAuthority", "Issuing authority", result);
    }
}
