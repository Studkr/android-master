package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe;

/**
 * @author Michael Soyma (Created on 9/7/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class OnEphemeralKeyGeneratedError {

    private final String error;

    public OnEphemeralKeyGeneratedError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
