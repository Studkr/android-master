package com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;
import com.INT.apps.GpsspecialDevelopment.utils.DateParser;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantPurchaseCoupons {

    private List<Coupon> coupons = new ArrayList<>();
    private Paging paging;

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public Paging getPaging() {
        return paging;
    }

    public final class Coupon implements Serializable {

        @SerializedName("DealOrderCode")
        private Code dealOrderCode;
        @SerializedName("DealOrder")
        private MerchantPurchases.PurchaseDeal dealOrder;
        @SerializedName("Deal")
        private DealInfo deal;
        @SerializedName("Listing")
        private Listing_ listing;
        @SerializedName("Buyer")
        private UserProfile.User user;

        public void setDealOrderCode(Code dealOrderCode) {
            this.dealOrderCode = dealOrderCode;
        }

        public String getUserName() {
            return String.format("%s %s", user.getFirst_name(), user.getLast_name());
        }

        public UserProfile.User getUser() {
            return user;
        }

        public String getDatePurchased() {
            return DateParser.stringDateToDesignPretty(dealOrder.getCreated());
        }

        public String getDatePurchasedAgo() {
            return DateParser.stringDateToPretty(dealOrder.getCreated());
        }

        public Code getDealOrderCode() {
            return dealOrderCode;
        }

        public Listing_ getListing() {
            return listing;
        }

        public DealInfo getDeal() {
            return deal;
        }

        public MerchantPurchases.PurchaseDeal getDealOrder() {
            dealOrder.setTitle(deal.getTitle());
            dealOrder.setImage(deal.getImage());
            return dealOrder;
        }
    }
}
