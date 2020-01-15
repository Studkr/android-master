package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.google.gson.annotations.SerializedName;

/**
 * @author Michael Soyma (Created on 9/13/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class PayResult {

    public String status;
    @SerializedName("message")
    public String errorMsg;
    @SerializedName("errors")
    public String[] errorsValidation;
    public String charge_id;
}
