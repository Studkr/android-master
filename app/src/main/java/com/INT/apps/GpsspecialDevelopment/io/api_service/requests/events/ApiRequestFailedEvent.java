package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events;

import retrofit.RetrofitError;

/**
 * Created by shrey on 23/4/15.
 */
public class ApiRequestFailedEvent
{
    RetrofitError mError;
    public ApiRequestFailedEvent(RetrofitError error)
    {
        mError = error;
    }

    public RetrofitError getError() {
        return mError;
    }
}
