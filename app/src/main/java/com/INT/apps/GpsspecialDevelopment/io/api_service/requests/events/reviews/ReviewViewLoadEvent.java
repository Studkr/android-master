package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.utils.ReviewPaginationQuery;

/**
 * Created by shrey on 3/6/15.
 */
public class ReviewViewLoadEvent
{
    private ReviewPaginationQuery mQuery;
    private int mPosition;
    public ReviewViewLoadEvent(ReviewPaginationQuery query,int position)
    {
        mQuery = query;
        mPosition = position;
    }

    public ReviewPaginationQuery getQuery() {
        return mQuery;
    }

    public int getPosition()
    {
        return mPosition;
    }
}
