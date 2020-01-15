package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/21/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class LoadListOfDealsError {

    private final String msg;

    public LoadListOfDealsError(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
