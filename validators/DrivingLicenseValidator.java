package validators;

import model.DocumentData;
import model.ValidationResult;
import rules.BaseValidator;

public class DrivingLicenseValidator extends BaseValidator {

    private static final String VALID_STATE_CODES =
        "AP AR AS BR CG DL GA GJ HR HP JH JK KA KL LA MP MH MN ML MZ NL OD PY PB RJ SK TN TS TR UP UK WB AN CH DD DN LD";

    private static final String VALID_VEHICLE_CLASSES =
        "LMV MCWG MCWOG HMV HPMV HGMV TR LMV-NT MGV";

    @Override
    public void validate(DocumentData doc, ValidationResult result) {

        if (!requireField(doc, "licenseNumber", "License number", result)) return;

        String raw   = doc.getField("licenseNumber").toUpperCase().replaceAll("\\s+", "");
        String clean = raw.replaceAll("-", "");

        if (!matchesPattern(clean, "[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{7}",
                "License number format is invalid. Expected: SS-RR-YYYY-NNNNNNN (e.g. MH-12-2019-1234567).",
                result)) return;
        String stateCode = clean.substring(0, 2);
        if (!VALID_STATE_CODES.contains(stateCode)) {
            result.addError("'" + stateCode + "' is not a valid Indian state/UT code.");
        }

        requireField(doc, "name", "Holder name", result);

        if (!requireField(doc, "dateOfBirth", "Date of birth", result)) return;
        String dob = doc.getField("dateOfBirth");
        if (!matchesPattern(dob, "\\d{2}/\\d{2}/\\d{4}",
                "Date of birth must be in DD/MM/YYYY format.", result)) return;

        try {
            int issueYear = Integer.parseInt(clean.substring(4, 8));
            int birthYear = Integer.parseInt(dob.split("/")[2]);
            if (issueYear - birthYear < 18) {
                result.addError("Holder must be at least 18 years old at time of license issuance.");
            }
        } catch (NumberFormatException e) {
            result.addWarning("Could not verify age — check license number and DOB formats.");
        }

        if (!requireField(doc, "issueDate", "Issue date", result)) return;
        String issueDate = doc.getField("issueDate");
        if (!matchesPattern(issueDate, "\\d{2}/\\d{2}/\\d{4}",
                "Issue date must be in DD/MM/YYYY format.", result)) return;

        if (!requireField(doc, "expiryDate", "Expiry date", result)) return;
        String expiryDate = doc.getField("expiryDate");
        if (!matchesPattern(expiryDate, "\\d{2}/\\d{2}/\\d{4}",
                "Expiry date must be in DD/MM/YYYY format.", result)) return;

        try {
            int iy = Integer.parseInt(issueDate.split("/")[2]);
            int ey = Integer.parseInt(expiryDate.split("/")[2]);
            if (ey <= iy) {
                result.addError("Expiry date must be after the issue date.");
            }
        } catch (NumberFormatException e) {
            result.addWarning("Could not compare issue and expiry dates — please check formats.");
        }

        if (!requireField(doc, "vehicleClass", "Vehicle class", result)) return;
        String vc = doc.getField("vehicleClass").toUpperCase().trim();
        boolean validClass = false;
        for (String cls : VALID_VEHICLE_CLASSES.split(" ")) {
            if (cls.equals(vc)) { validClass = true; break; }
        }
        if (!validClass) {
            result.addError("Invalid vehicle class '" + vc + "'. Valid: LMV, MCWG, MCWOG, HMV, HPMV, TR, etc.");
        }
        requireField(doc, "address", "Address", result);
    }
}
