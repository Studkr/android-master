package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view;

/**
 * Created by shrey on 20/5/15.
 */
public class LoadListingViewEvent {
    private String mListingId;
    private String mLatLng;
    private Boolean mLoadFromServer = false;

    public LoadListingViewEvent(String lisitingId) {
        mListingId = lisitingId;
    }

    public LoadListingViewEvent(String lisitingId, String latLng, boolean loadFromServer) {
        mListingId = lisitingId;
        mLatLng = latLng;
        mLoadFromServer = loadFromServer;
    }

    public String getListingId() {
        return mListingId;
    }

    public String getLatLng() {
        return mLatLng;
    }

    public Boolean shouldLoadFromServer() {
        return mLoadFromServer;
    }
}
