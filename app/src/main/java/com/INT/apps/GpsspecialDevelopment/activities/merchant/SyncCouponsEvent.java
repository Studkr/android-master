package com.INT.apps.GpsspecialDevelopment.activities.merchant;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.Code;

/**
 * @author Michael Soyma (Created on 10/10/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class SyncCouponsEvent {

    private final Code code;
    private final String dealId;

    public SyncCouponsEvent(Code redeemedCode, String dealId) {
        this.code = redeemedCode;
        this.dealId = dealId;
    }

    public Code getCode() {
        return code;
    }

    public String getDealId() {
        return dealId;
    }
}
