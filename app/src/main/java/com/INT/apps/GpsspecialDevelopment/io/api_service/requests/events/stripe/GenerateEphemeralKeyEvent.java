package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe;

/**
 * @author Michael Soyma (Created on 9/7/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class GenerateEphemeralKeyEvent {

    private final String apiVersion;
    private final String customerId;

    public GenerateEphemeralKeyEvent(String apiVersion, String customerId) {
        this.apiVersion = apiVersion;
        this.customerId = customerId;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getCustomerId() {
        return customerId;
    }
}
