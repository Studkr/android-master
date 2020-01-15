package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;

import retrofit.RetrofitError;

/**
 * Created by shrey on 6/11/15.
 */
public class ListingImageUploadFailedEvent extends ApiRequestFailedEvent
{
    public ListingImageUploadFailedEvent(RetrofitError error)
    {
       super(error);
    }
}
