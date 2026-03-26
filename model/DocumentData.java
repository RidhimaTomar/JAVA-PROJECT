package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the fields for a single document submission.
 * Think of it as a flexible key-value bag — validators pull
 * fields out of it by name.
 */
public class DocumentData {

    private final String              docType;
    private final Map<String, String> fields;

    public DocumentData(String docType) {
        this.docType = docType.toUpperCase().trim();
        this.fields  = new HashMap<>();
    }

    public void addField(String key, String value) {
        if (key != null && value != null) {
            fields.put(key.trim(), value.trim());
        }
    }

    public String getField(String key) {
        return fields.getOrDefault(key, "");
    }

    public boolean hasField(String key) {
        String val = fields.getOrDefault(key, "").trim();
        return !val.isEmpty();
    }

    public String getDocType() { return docType; }

    public Map<String, String> getAllFields() {
        return new HashMap<>(fields);   // defensive copy
    }
}
