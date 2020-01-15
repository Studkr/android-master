package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 24/8/15.
 */
public class UserCheckins
{
    @SerializedName("checkins")
    List<CheckinInfo> checkins = new ArrayList<>();
    Paging paging;

    public Paging getPaging() {
        return paging;
    }

    public List<CheckinInfo> getCheckins() {
        return checkins;
    }
}
