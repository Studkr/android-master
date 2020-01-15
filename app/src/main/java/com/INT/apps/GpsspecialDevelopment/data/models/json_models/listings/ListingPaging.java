package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shrey on 30/4/15.
 */
public class ListingPaging {
    @SerializedName("listings")
    public List<Listing> listings;

    @SerializedName("paging")
    public Paging paging;

    public List<Listing> getListings() {
        if (listings == null) {
            listings = new ArrayList<>();
        }
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    static public class ListingPagingCache {
        private List<Integer> listingIds;
        private Paging paging;

        public List<Integer> getListingIds() {
            return listingIds;
        }

        public Paging getPaging() {
            return paging;
        }

        public void setListingIds(List<Integer> listingIds) {
            this.listingIds = listingIds;
        }

        public void setPaging(Paging paging) {
            this.paging = paging;
        }
    }
}
