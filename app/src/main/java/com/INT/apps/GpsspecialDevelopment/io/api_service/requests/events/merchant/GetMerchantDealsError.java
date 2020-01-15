package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantDealsError {

    private final String message;

    public GetMerchantDealsError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
