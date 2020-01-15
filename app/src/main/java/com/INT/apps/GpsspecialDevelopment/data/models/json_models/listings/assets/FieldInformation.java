package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets;

import java.io.Serializable;

/**
 * Created by shrey on 7/7/15.
 */
public class FieldInformation implements Serializable {

    public static final String TYPE_URL = "url";

    private String field;
    private String type;
    private String label;
    private String displayValue;
    private String value;

    public String getDisplayValue() {
        if (displayValue == null) {
            return "";
        }
        return displayValue;
    }

    public String getField() {
        return field;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getValue() {
        return value;
    }
}
