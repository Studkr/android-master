package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PayResult;

/**
 * @author Michael Soyma (Created on 9/13/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class OnRequestToPayResult {

    private PayResult payResult;
    private String errorMsg;

    public OnRequestToPayResult(PayResult payResult) {
        this.payResult = payResult;
    }

    public OnRequestToPayResult(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public PayResult getPayResult() {
        return payResult;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isSuccess() {
        return payResult != null;
    }
}
