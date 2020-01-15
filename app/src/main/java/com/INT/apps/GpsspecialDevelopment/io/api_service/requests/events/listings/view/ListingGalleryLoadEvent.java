package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view;

/**
 * Created by shrey on 22/5/15.
 */
public class ListingGalleryLoadEvent
{
    String mListingId;
    int mLimit;
    public ListingGalleryLoadEvent(String listingId,int limit)
    {
        mLimit = limit;
        mListingId = listingId;
    }

    public String getListingId() {
        return mListingId;
    }

    public int getLimit() {
        return mLimit;
    }
}
