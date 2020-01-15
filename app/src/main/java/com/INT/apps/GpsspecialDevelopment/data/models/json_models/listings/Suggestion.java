package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 11/5/15.
 */
public class Suggestion
{
    @SerializedName("id")
    int categoryId;
    String name;
    String type;

    public String getName()
    {
        return name;
    }

    public String getType(){
        return type;
    }

    public boolean isCategory()
    {
        return type.equals("category");
    }

    public boolean isKeyword()
    {
        return type.equals("keyword");
    }

    public boolean isLocation()
    {
        return type.equals("location");
    }

    public int getCategoryId() {
        return categoryId;
    }

}
