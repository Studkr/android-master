package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class OrderInfoLoadError {
    private String mMsg;

    public OrderInfoLoadError(String msg) {
        mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }
}
