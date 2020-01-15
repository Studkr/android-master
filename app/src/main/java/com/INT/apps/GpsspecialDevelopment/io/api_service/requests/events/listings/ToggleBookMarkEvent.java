package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;

/**
 * Created by shrey on 15/7/15.
 */
public class ToggleBookMarkEvent
{

    Listing_ mListing;
    boolean mBookMarked;
    public ToggleBookMarkEvent(Listing_ listing,boolean bookMarked)
    {
        mListing = listing;
        mBookMarked = bookMarked;
    }

    public Listing_ getListing() {
        return mListing;
    }

    public boolean isBookMarked()
    {
        return mBookMarked;
    }
}
