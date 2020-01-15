package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class LoadInfoOrderEvent {
    private String mDealId;
    private Double mLat, mLng;

    public LoadInfoOrderEvent(String dealId, Double lat, Double lng) {
        mDealId = dealId;
        mLat = lat;
        mLng = lng;
    }

    public String getDealId() {
        return mDealId;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLng() {
        return mLng;
    }
}
