package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 17/8/15.
 */
public class UserProfile {
    private User user;

    public User getUser() {
        return user;
    }

    public class User implements Serializable {
        private String id;
        @Expose
        private String username;

        @Expose
        private String email;

        @Expose
        private String first_name;

        @Expose
        private String last_name;

        @Expose
        private String avatar;

        @Expose
        private String phone;

        @SerializedName("review_count")
        @Expose
        private String reviewCount;
        @SerializedName("listing_asset_count")
        @Expose
        private String listingAssetCount;
        @SerializedName("checkin_count")
        @Expose
        private Integer checkinCount;
        @SerializedName("purchased_deal_count")
        @Expose
        private Integer purchasedDealCount;
        @SerializedName("stripe_account")
        @Expose
        private String stripeAccount;
        @SerializedName("fav_listing_count")
        @Expose
        private Integer favListingCount;

        @SerializedName("last_login_date")
        private String lastLoginDate;

        private String created;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getAvatar() {
            return avatar;
        }

        public Integer getReviewCount() {
            return Integer.parseInt(reviewCount);
        }

        public String getLastLoginDate() {
            return lastLoginDate;
        }

        public String getCreated() {
            return created;
        }

        public Integer getFavListingCount() {
            return favListingCount;
        }

        public Integer getCheckinCount() {
            return checkinCount;
        }

        public Integer getPurchasedDealCount() {
            return purchasedDealCount;
        }

        public String getEmail() {
            return email;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getStripeAccount() {
            return stripeAccount;
        }

        public void setStripeAccount(String stripeAccount) {
            this.stripeAccount = stripeAccount;
        }

        public String getPhone() {
            return phone;
        }
    }
}
