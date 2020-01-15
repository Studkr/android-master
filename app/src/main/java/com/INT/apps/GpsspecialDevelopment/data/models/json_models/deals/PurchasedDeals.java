package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 27/8/15.
 */
public class PurchasedDeals {
    Paging paging;
    @SerializedName("deals")
    List<PurchasedDeal> deals = new ArrayList<>();

    public Paging getPaging() {
        return paging;
    }

    public List<PurchasedDeal> getPurchasedDeals() {
        return deals;
    }
}
