package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewData;

/**
 * Created by shrey on 19/6/15.
 */
public class ReviewAddRequestEvent
{
    ReviewData mReviewData;
    String mRequestKey;
    String mListingId;
    public ReviewAddRequestEvent(String listingId,ReviewData reviewData,String requestKey)
    {
        mListingId = listingId;
        mReviewData = reviewData;
        mRequestKey = requestKey;
    }

    public ReviewData getReviewData() {
        return mReviewData;
    }

    public String getRequestKey() {
        return mRequestKey;
    }

    public String getListingId() {
        return mListingId;
    }
}
