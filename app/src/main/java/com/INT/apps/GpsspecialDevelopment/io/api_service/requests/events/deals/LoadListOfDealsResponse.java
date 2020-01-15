package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfoObj;

/**
 * @author Michael Soyma (Created on 9/21/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class LoadListOfDealsResponse {

    private final DealInfoObj dealInfo;

    public LoadListOfDealsResponse(DealInfoObj dealInfo) {
        this.dealInfo = dealInfo;
    }

    public DealInfoObj getDealInfo() {
        return dealInfo;
    }
}
