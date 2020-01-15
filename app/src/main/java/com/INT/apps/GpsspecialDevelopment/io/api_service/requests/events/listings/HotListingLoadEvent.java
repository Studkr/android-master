package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

/**
 * Created by shrey on 23/4/15.
 */
public class HotListingLoadEvent
{
    private double mLatitude;
    private double mLongitude;

    public HotListingLoadEvent(double latitude,double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

}
