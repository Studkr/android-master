package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.stripe;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.stripe.RegisterResult;

/**
 * @author Michael Soyma (Created on 9/7/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class OnUserRegisteredInStripeEvent {

    private final RegisterResult registerResult;

    public OnUserRegisteredInStripeEvent(RegisterResult registerResult) {
        this.registerResult = registerResult;
    }

    public RegisterResult getRegisterResult() {
        return registerResult;
    }
}
