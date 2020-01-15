package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Soyma (Created on 9/19/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class DealInfoObj implements Serializable {

    @SerializedName("deals")
    List<DealInfo> deals = new ArrayList<>();
    @SerializedName("paging")
    Paging paging = new Paging();

    public List<DealInfo> getDeals() {
        return deals;
    }

    public Paging getPaging() {
        return paging;
    }
}
