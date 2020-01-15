package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class FindCouponResult {

    private final MerchantPurchaseCoupons.Coupon couponResult;

    public FindCouponResult(MerchantPurchaseCoupons.Coupon couponResult) {
        this.couponResult = couponResult;
    }

    public MerchantPurchaseCoupons.Coupon getCouponResult() {
        return couponResult;
    }
}
