package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;


import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealsPaging;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantDealsResult {

    private final DealsPaging dealsPaging;

    public GetMerchantDealsResult(DealsPaging dealsPaging) {
        this.dealsPaging = dealsPaging;
    }

    public DealsPaging getDealsPaging() {
        return dealsPaging;
    }
}
