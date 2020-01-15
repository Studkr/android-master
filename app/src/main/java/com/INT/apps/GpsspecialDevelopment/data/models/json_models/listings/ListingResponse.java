package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 23/11/15.
 */
public class ListingResponse
{
    @SerializedName("listing")
    Listing listing;

    public Listing getListing() {
        return listing;
    }
}
