package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe;

/**
 * @author Michael Soyma (Created on 9/7/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RegisterUserInStripeEvent {

    private final String userId;

    public RegisterUserInStripeEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
