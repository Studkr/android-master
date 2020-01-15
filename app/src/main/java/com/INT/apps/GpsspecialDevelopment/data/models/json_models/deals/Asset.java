package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 17/7/15.
 */
public class Asset
{
    String id;
    @SerializedName("file_url")
    String fileUrl;

    public String getId() {
        return id;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
