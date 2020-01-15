package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 23/4/15.
 */
public class Listing
{
    @Expose
    @SerializedName("Listing")
    private Listing_ listing;

    public Listing_ getListing()
    {
        return listing;
    }

    public void setListing(Listing_ listing)
    {
        this.listing = listing;
    }
}
