package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 17/7/15.
 */
public class Deal implements Serializable {
    @Expose
    @SerializedName("Deal")
    private Deal_ deal;
    @Expose
    @SerializedName("Listing")
    private Listing_ listing;

    public Deal_ getDeal() {
        return deal;
    }

    public Listing_ getListing() {
        return listing;
    }

    public class Deal_ extends DealInfo {

        @Expose
        private String description;

        @Expose
        private String expires;
        @Expose
        private String status;
        @Expose
        private String created;
        @Expose
        private String modified;

        @SerializedName("")
        @Expose
        private String isFeatured;

        @Expose
        private String terms;

        @SerializedName("currency")
        @Expose
        private String currency;

        @SerializedName("viewUrl")
        String viewUrl;

        @SerializedName("finalPrice")
        String finalPrice;

        @Expose
        private List<Asset> assets = new ArrayList<Asset>();

        /**
         * @return The description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description The description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return The expires
         */
        public String getExpires() {
            return expires;
        }

        /**
         * @param expires The expires
         */
        public void setExpires(String expires) {
            this.expires = expires;
        }

        /**
         * @return The status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return The created
         */
        public String getCreated() {
            return created;
        }

        /**
         * @param created The created
         */
        public void setCreated(String created) {
            this.created = created;
        }

        /**
         * @return The modified
         */
        public String getModified() {
            return modified;
        }

        /**
         * @param modified The modified
         */
        public void setModified(String modified) {
            this.modified = modified;
        }

        /**
         * @return The isFeatured
         */
        public String getIsFeatured() {
            return isFeatured;
        }

        /**
         * @param isFeatured The is_featured
         */
        public void setIsFeatured(String isFeatured) {
            this.isFeatured = isFeatured;
        }

        /**
         * @return The terms
         */
        public String getTerms() {
            return terms;
        }

        /**
         * @param terms The terms
         */
        public void setTerms(String terms) {
            this.terms = terms;
        }


        public String getCurrency() {
            return currency;
        }

        /**
         * @return The assets
         */
        public List<Asset> getAssets() {
            return assets;
        }

        /**
         * @param assets The assets
         */
        public void setAssets(List<Asset> assets) {
            this.assets = assets;
        }

        public String getViewUrl() {
            return viewUrl;
        }

        @Override
        public String getFinalPrice() {
            if (!TextUtils.isEmpty(finalPrice))
                return finalPrice;
            else return super.getFinalPrice();
        }
    }
}
