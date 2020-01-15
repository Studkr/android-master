package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;

import retrofit.RetrofitError;

/**
 * Created by shrey on 23/11/15.
 */
public class ListingLoadFailedEvent extends ApiRequestFailedEvent
{
    public ListingLoadFailedEvent(RetrofitError error)
    {
        super(error);
    }
}
