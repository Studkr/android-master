package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;

import retrofit.RetrofitError;

/**
 * Created by shrey on 29/10/15.
 */
public class ListingPaginationFailedEvent extends ApiRequestFailedEvent
{
    public ListingPaginationFailedEvent(RetrofitError error)
    {
        super(error);
    }
}
