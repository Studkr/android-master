package com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shrey on 15/6/15.
 */
public class FieldsProperty
{
    public static String TYPE_TEXT="text";
    public static String TYPE_PASSWORD="password";
    public static String TYPE_TEXTAREA="textarea";
    public static String TYPE_PHONE_NO="phoneno";
    public static String TYPE_ZIPCODE="zipcode";
    public static String TYPE_EMAIL="email";
    public static String TYPE_RATING="rating";
    public static String TYPE_SELECT="select";
    public static String TYPE_RADIO="radio";
    public static String TYPE_MULTI_CHECKBOX="multipleCheckbox";


    @Expose
    private String label;
    @Expose
    private Boolean required;
    @Expose
    private String field;
    @Expose
    private String type;
    @Expose
    private List<String> validationRules = new ArrayList<>();

    public static boolean isSelectField(String type)
    {
        String[] selectList = new String[]{
               TYPE_SELECT,
               TYPE_RADIO,
                TYPE_MULTI_CHECKBOX

        };
        return Arrays.asList(selectList).contains(type);
    }

    @SerializedName("options")
    private HashMap<String,String> options = new HashMap<>();
    /**
     *
     * @return
     * The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The required
     */
    public Boolean getRequired() {
        return required;
    }

    /**
     *
     * @param required
     * The required
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     *
     * @return
     * The field
     */
    public String getField() {
        return field;
    }

    /**
     *
     * @param field
     * The field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The validationRules
     */
    public List<String> getValidationRules() {
        return validationRules;
    }

    /**
     *
     * @param validationRules
     * The validationRules
     */
    public void setValidationRules(List<String> validationRules) {
        this.validationRules = validationRules;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }
}