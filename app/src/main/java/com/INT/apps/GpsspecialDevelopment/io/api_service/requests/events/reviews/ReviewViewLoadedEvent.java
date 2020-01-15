package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews.Review;

/**
 * Created by shrey on 3/6/15.
 */
public class ReviewViewLoadedEvent
{
    Review mReview;
    int mPosition;
    public ReviewViewLoadedEvent(Review review,int position)
    {
        mPosition = position;
        mReview = review;
    }

    public Review getReview()
    {
        return mReview;
    }

    public int getPosition()
    {
        return mPosition;
    }
}
