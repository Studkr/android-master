package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class FindCouponEvent {

    private final String couponCode;

    public FindCouponEvent(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponCode() {
        return couponCode;
    }
}
