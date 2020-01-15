package com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category;

/**
 * Created by msaqib on 7/14/2016.
 */
public class SetDynamicCategory {
    // "Dynamic"
    public DynamicCategories dynamicCategories;

    public DynamicCategories getDynamicCategories() {
        return dynamicCategories;
    }

    public SetDynamicCategory(DynamicCategories dynamicCategories) {
        this.dynamicCategories = dynamicCategories;
    }
}
