package com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category;

import com.google.gson.annotations.SerializedName;

/**
 * Created by msaqib on 7/14/2016.
 *
 *
 */
public class DynamicCategoryValues {
    // "Dynamic"

    @SerializedName("id")
    private String id;

    @SerializedName("category_name")
    private String category_name;

    @SerializedName("image_name")
    private String image_name;

    @SerializedName("cv_id")
    private String cv_id;

    @SerializedName("parent_id")
    private String parent_id;

    @SerializedName("children_count")
    private String children_count;
    public String getId() {
        return id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getImage_name() {
        return image_name;
    }

    public String getCv_id() {
        return cv_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public String getChildren_count() {
        return children_count;
    }

    public DynamicCategoryValues(String id, String category_name, String image_name, String cv_id, String parent_id, String children_count) {

        this.id = id;
        this.category_name = category_name;
        this.image_name = image_name;
        this.cv_id = cv_id;
        this.parent_id = parent_id;
        this.children_count = children_count;
    }

}
