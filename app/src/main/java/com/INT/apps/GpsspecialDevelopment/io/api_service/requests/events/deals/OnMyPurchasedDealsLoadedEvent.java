package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeals;

/**
 * Created by shrey on 27/8/15.
 */
public class OnMyPurchasedDealsLoadedEvent {

    private PurchasedDeals mPurchasedDeals;
    private boolean isUsed;

    public OnMyPurchasedDealsLoadedEvent(PurchasedDeals mPurchasedDeals, boolean isUsed) {
        this.mPurchasedDeals = mPurchasedDeals;
        this.isUsed = isUsed;
    }

    public PurchasedDeals getPurchasedDeals() {
        return mPurchasedDeals;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
