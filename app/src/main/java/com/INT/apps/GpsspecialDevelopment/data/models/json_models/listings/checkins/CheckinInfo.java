package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;

/**
 * Created by shrey on 24/8/15.
 */
public class CheckinInfo
{
    Checkin Checkin;
    Listing_ Listing;

    public Checkin getCheckin() {
        return Checkin;
    }

    public Listing_ getListing() {
        return Listing;
    }
}
