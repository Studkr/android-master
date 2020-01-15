package com.INT.apps.GpsspecialDevelopment.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by msaqib on 7/13/2016.
 */
public class DynamicCategoryModel {
    @SerializedName("id")
    public int _id;
    @SerializedName("category_name")
    public String category_name;
    @SerializedName("image_name")
    public String image_name;
    @SerializedName("cv_id")
    public int cv_id;
    @SerializedName("parent_id")
    public Integer parent_id;
    @SerializedName("children_count")
    public int children_count;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getCv_id() {
        return cv_id;
    }

    public void setCv_id(int cv_id) {
        this.cv_id = cv_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public int getChildren_count() {
        return children_count;
    }

    public void setChildren_count(int children_count) {
        this.children_count = children_count;
    }

    public DynamicCategoryModel(int _id, String category_name, String image_name, int cv_id, Integer parent_id, int children_count) {

        this._id = _id;
        this.category_name = category_name;
        this.image_name = image_name;
        this.cv_id = cv_id;
        this.parent_id = parent_id;
        this.children_count = children_count;
    }



}
