package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantPurchasesEvent {

    private final int page;

    public GetMerchantPurchasesEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
