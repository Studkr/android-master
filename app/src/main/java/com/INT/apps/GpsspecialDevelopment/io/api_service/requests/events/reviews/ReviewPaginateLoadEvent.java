package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;

/**
 * Created by shrey on 27/5/15.
 */
public class ReviewPaginateLoadEvent
{
    ReviewPaginationQuery mReviewPaginationQuery;
    public ReviewPaginateLoadEvent(ReviewPaginationQuery reviewPaginationQuery)
    {
        mReviewPaginationQuery = reviewPaginationQuery;
    }

    public ReviewPaginationQuery getReviewPaginationQuery()
    {
        return mReviewPaginationQuery;
    }
}
