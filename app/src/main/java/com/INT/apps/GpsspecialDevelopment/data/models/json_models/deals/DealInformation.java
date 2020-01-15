package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 17/7/15.
 */
public class DealInformation
{
    @Expose
    @SerializedName("deal")
    public Deal deal;

    public Deal getDeal()
    {
        return deal;
    }
}
