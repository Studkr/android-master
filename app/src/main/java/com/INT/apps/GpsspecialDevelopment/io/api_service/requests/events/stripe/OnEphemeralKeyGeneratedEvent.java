package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe;

/**
 * @author Michael Soyma (Created on 9/7/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class OnEphemeralKeyGeneratedEvent {

    private final String key;

    public OnEphemeralKeyGeneratedEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
