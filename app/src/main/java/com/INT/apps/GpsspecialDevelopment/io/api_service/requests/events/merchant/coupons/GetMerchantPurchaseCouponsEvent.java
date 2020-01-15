package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class GetMerchantPurchaseCouponsEvent {

    private final String dealId;
    private final int page;
    private final boolean isUsed;

    public GetMerchantPurchaseCouponsEvent(String dealId, int page, boolean isUsed) {
        this.dealId = dealId;
        this.page = page;
        this.isUsed = isUsed;
    }

    public String getDealId() {
        return dealId;
    }

    public int getPage() {
        return page;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
