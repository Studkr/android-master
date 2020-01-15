package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shrey on 27/8/15.
 */
public class PurchasedDeal {
    DealOrder DealOrder;
    @SerializedName("Listing")
    Listing_ listing;

    public PurchasedDeal.DealOrder getDealOrder() {
        return DealOrder;
    }

    public Listing_ getListing() {
        return listing;
    }

    public class DealOrder extends DealInfo implements Serializable {
        @SerializedName("deal_id")
        @Expose
        private String dealId;
        @SerializedName("deal_image")
        @Expose
        private String dealImage;
        @Expose
        private String purchased;
        @Expose
        private String created;
        @Expose
        private String quantity;
        @Expose
        private String amount;
        @Expose
        private ArrayList<String> codes = new ArrayList<>();

        public ArrayList<String> getCodes() {
            return codes;
        }

        public String getAmount() {
            return amount;
        }

        public String getCreated() {
            return created;
        }

        public String getDealId() {
            return dealId;
        }

        public int getDealIdInt() {
            return Integer.parseInt(dealId);
        }

        public String getPurchased() {
            return purchased;
        }

        @Override
        public String getImage() {
            return dealImage;
        }

        public String getQuantity() {
            return quantity;
        }

        public int getQuantityValue() {
            return Integer.parseInt(quantity);
        }
    }
}
