package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

/**
 * Created by shrey on 14/8/15.
 */
public class DoBusinessCheckIn
{
    String mListingId;
    String mComment;
    public DoBusinessCheckIn(String listingId,String comment)
    {
        mListingId = listingId;
        mComment =comment;
    }

    public String getComment() {
        return mComment;
    }

    public String getListingId() {
        return mListingId;
    }
}
