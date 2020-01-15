package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantPurchaseCouponsResult {

    private final MerchantPurchaseCoupons merchantPurchaseCoupons;
    private final boolean isUsed;

    public GetMerchantPurchaseCouponsResult(MerchantPurchaseCoupons merchantPurchaseCoupons, boolean isUsed) {
        this.merchantPurchaseCoupons = merchantPurchaseCoupons;
        this.isUsed = isUsed;
    }

    public MerchantPurchaseCoupons getMerchantPurchaseCoupons() {
        return merchantPurchaseCoupons;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
