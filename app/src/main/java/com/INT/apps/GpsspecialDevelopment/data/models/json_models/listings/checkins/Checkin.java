package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 24/8/15.
 */
public class Checkin
{
    @Expose
    private String id;
    @SerializedName("entity_type")
    @Expose
    private String entityType;
    @SerializedName("entity_id")
    @Expose
    private String entityId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @Expose
    private String created;

    @SerializedName("checkin_count")
    String checkinCount;

    public String getCheckinCount() {
        if(checkinCount == null)
        {
            checkinCount = "0";
        }
        return checkinCount;
    }

    public String getCreated()
    {
        return created;
    }
}
