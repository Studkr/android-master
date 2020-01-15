package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class DealsPaging {
    @SerializedName("deals")
    public List<Deal> deals;

    @SerializedName("paging")
    public Paging paging;

    public List<Deal> getDeals() {
        if (deals == null) {
            deals = new ArrayList<>();
        }
        return deals;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
