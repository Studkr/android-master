package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * Created by shrey on 17/7/15.
 */
public class LoadDealInformationEvent {
    private String mDealId;

    public LoadDealInformationEvent(String dealId) {
        mDealId = dealId;
    }

    public String getDealId() {
        return mDealId;
    }
}
