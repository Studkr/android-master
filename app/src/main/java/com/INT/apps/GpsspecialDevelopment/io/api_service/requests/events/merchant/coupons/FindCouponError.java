package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class FindCouponError {

    private final String message;
    private final boolean notFound;

    public FindCouponError(String message) {
        this.message = message;
        this.notFound = false;
    }

    public FindCouponError(boolean notFound) {
        this.notFound = notFound;
        this.message = null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isNotFound() {
        return notFound;
    }
}
