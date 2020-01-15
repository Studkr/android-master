package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by shrey on 17/8/15.
 */
public class UserCustomField implements Serializable {
    private String field;
    @Expose
    private Object value;
    @Expose
    private String type;
    @Expose
    private String label;

    public Object getValue() {
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getField() {
        return field;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }
}
