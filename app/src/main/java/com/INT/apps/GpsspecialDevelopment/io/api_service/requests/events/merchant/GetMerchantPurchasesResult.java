package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchases;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantPurchasesResult {

    private final MerchantPurchases merchantPurchases;

    public GetMerchantPurchasesResult(MerchantPurchases merchantPurchases) {
        this.merchantPurchases = merchantPurchases;
    }

    public MerchantPurchases getMerchantPurchases() {
        return merchantPurchases;
    }
}
