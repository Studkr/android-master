package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantPurchaseCouponsError {

    private final String message;
    private final boolean isUsed;

    public GetMerchantPurchaseCouponsError(String message, boolean isUsed) {
        this.message = message;
        this.isUsed = isUsed;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
