package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

/**
 * Created by shrey on 18/6/15.
 */
public class LoadReviewFormEvent
{
    String mListingId;

    public LoadReviewFormEvent(String listingId)
    {
        mListingId = listingId;
    }
    public String getListingId() {
        return mListingId;
    }
}
