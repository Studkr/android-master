package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewAddResult;

/**
 * Created by shrey on 20/6/15.
 */
public class ReviewAddResultEvent
{
    String mRequestKey;
    ReviewAddResult mReviewAddResult;
    String mListingId;
    public ReviewAddResultEvent(String requestKey,ReviewAddResult reviewAddResult,String listingId)
    {
        mRequestKey = requestKey;
        mReviewAddResult = reviewAddResult;
        mListingId = listingId;
    }

    public String getListingId() {
        return mListingId;
    }

    public String getRequestKey() {
        return mRequestKey;
    }

    public ReviewAddResult getReviewAddResult() {
        return mReviewAddResult;
    }
}
