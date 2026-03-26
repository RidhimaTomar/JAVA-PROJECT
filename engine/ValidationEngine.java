package engine;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;
import validators.AadhaarValidator;
import validators.BirthCertificateValidator;
import validators.DrivingLicenseValidator;
import validators.PANValidator;


public class ValidationEngine {

    public ValidationResult validate(DocumentData doc) {
        ValidationResult result    = new ValidationResult();
        BaseValidator    validator = getValidator(doc.getDocType());

        if (validator == null) {
            result.addError("Unknown document type: " + doc.getDocType());
            return result;
        }

        validator.validate(doc, result);
        return result;
    }

    private BaseValidator getValidator(String docType) {
        return switch (docType.toUpperCase()) {
            case "AADHAAR"            -> new AadhaarValidator();
            case "PAN"                -> new PANValidator();
            case "DRIVING_LICENSE"    -> new DrivingLicenseValidator();
            case "BIRTH_CERTIFICATE"  -> new BirthCertificateValidator();
            default                   -> null;
        };
    }
}
