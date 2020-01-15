package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantDealsEvent {

    private final int page;

    public GetMerchantDealsEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
