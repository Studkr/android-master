package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class ListingsPaging {
    @SerializedName("listings")
    public List<Listing_> listings;

    @SerializedName("paging")
    public Paging paging;

    public List<Listing_> getListings() {
        if (listings == null) {
            listings = new ArrayList<>();
        }
        return listings;
    }

    public void setListings(List<Listing_> listings) {
        this.listings = listings;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
