package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/25/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class OnMyPurchasedDealsErrorEvent {

    private final String message;
    private boolean isUsed;

    public OnMyPurchasedDealsErrorEvent(String message, boolean isUsed) {
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
