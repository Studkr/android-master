package com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category;

import com.INT.apps.GpsspecialDevelopment.data.models.DynamicCategoryModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by msaqib on 7/14/2016.
 */
public class DynamicCategories {
    // "Dynamic"
    @SerializedName("categories")
    private ArrayList<DynamicCategoryModel> categories = new ArrayList<>();

    public ArrayList<DynamicCategoryModel> getCategories() {
        return categories;
    }
}
