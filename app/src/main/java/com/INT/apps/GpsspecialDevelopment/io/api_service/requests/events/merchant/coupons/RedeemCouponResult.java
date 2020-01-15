package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.Code;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RedeemCouponResult {

    private final Code redeemedCode;

    public RedeemCouponResult(Code code) {
        this.redeemedCode = code;
    }

    public Code getRedeemedCode() {
        return redeemedCode;
    }
}
