package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RedeemCouponEvent {

    private final String dealId;

    public RedeemCouponEvent(String dealId) {
        this.dealId = dealId;
    }

    public String getDealId() {
        return dealId;
    }
}
