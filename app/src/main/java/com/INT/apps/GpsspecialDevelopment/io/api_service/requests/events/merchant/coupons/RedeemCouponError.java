package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RedeemCouponError {

    private final String message;
    private final boolean alreadyRedeemed;

    public RedeemCouponError(String message) {
        this.message = message;
        this.alreadyRedeemed = false;
    }

    public RedeemCouponError(boolean alreadyRedeemed) {
        this.alreadyRedeemed = alreadyRedeemed;
        this.message = null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAlreadyRedeemed() {
        return alreadyRedeemed;
    }
}
