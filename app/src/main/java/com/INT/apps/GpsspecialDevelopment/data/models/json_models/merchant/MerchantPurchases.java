package com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Soyma (Created on 10/3/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantPurchases {

    private List<Purchase> purchases = new ArrayList<>();
    private Paging paging;

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public Paging getPaging() {
        return paging;
    }

    public final class Purchase {

        @SerializedName("DealOrder")
        private PurchaseDeal deal;
        @SerializedName("Listing")
        private Listing_ listing;

        public Listing_ getListing() {
            return listing;
        }

        public PurchaseDeal getDeal() {
            return deal;
        }
    }

    public class PurchaseDeal extends DealInfo implements Serializable {
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
        private int unused_coupons_count;
        @Expose
        private int used_coupons_count;

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

        public String getDealImage() {
            return dealImage;
        }

        public void setDealImage(String image) {
            dealImage = image;
        }

        public String getQuantity() {
            return quantity;
        }

        public int getQuantityValue() {
            return Integer.parseInt(quantity);
        }

        public int getUnusedCouponsCount() {
            return unused_coupons_count;
        }
        public int getUsedCouponsCount() {
            return used_coupons_count;
        }

        public void setUsedCouponsCount(int used_coupons_count) {
            this.used_coupons_count = used_coupons_count;
        }

        public void setUnusedCouponsCount(int unused_coupons_count) {
            this.unused_coupons_count = unused_coupons_count;
        }

        @Override
        public String getImage() {
            if (!TextUtils.isEmpty(dealImage))
                return dealImage;
            else return super.getImage();
        }
    }
}
