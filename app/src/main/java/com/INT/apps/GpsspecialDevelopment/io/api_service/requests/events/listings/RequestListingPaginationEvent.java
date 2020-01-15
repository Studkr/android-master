package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;

/**
 * Created by shrey on 1/5/15.
 */
public class RequestListingPaginationEvent {
    private ListingPaginationQueryBuilder mQueryBuilder;
    private String mRequestKey;
    private String location;

    public RequestListingPaginationEvent(ListingPaginationQueryBuilder builder, String requestKey, Double[] latLng) {
        mQueryBuilder = builder;
        mRequestKey = requestKey;
        if ((latLng[0] == null && latLng[1] == null) || !TextUtils.isEmpty(builder.getLocationSearchKeyword()))
            return;
        location = latLng[0] + "," + latLng[1];
    }

    public ListingPaginationQueryBuilder getQueryBuilder() {
        return mQueryBuilder;
    }

    public String getRequestKey() {
        return mRequestKey;
    }

    public String getLocation() {
        return location;
    }
}
