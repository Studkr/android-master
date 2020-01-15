package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.ReviewPaging;

/**
 * Created by shrey on 27/5/15.
 */
public class ReviewPaginateLoadedEvent
{
    private ReviewPaging mReviewPaging;
    public ReviewPaginateLoadedEvent(ReviewPaging reviewPaging)
    {
        mReviewPaging = reviewPaging;
    }

    public ReviewPaging getReviewPaging()
    {
        return mReviewPaging;
    }
}
