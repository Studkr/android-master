package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Michael Soyma (Created on 9/19/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class DealInfo implements Serializable {
    private String id;
    private String title;
    private String image;
    private String discount;
    private String saving;
    private String currency_symbol;
    private String time_pending_seconds;
    private String regular_price;
    private String discount_type;
    private String total_quantity;
    private String quantity_consumed;
    private String purchase_link_status;
    private String view_count;
    private String fine_print;
    private String validity;
    private String publish_on_date;
    private String publish_on_time;
    private String final_price;

    public String getDiscount() {
        if (discount != null) {
            Double discount = Double.valueOf(this.discount);
            this.discount = String.format(Locale.US, "%.0f", discount);
        }
        return this.discount;
    }

    public double getDiscountValue() {
        return Double.parseDouble(discount);
    }

    public String getSaving() {
        return saving;
    }

    public String getCurrencySymbol() {
        return currency_symbol;
    }

    public String getTimePendingSeconds() {
        return time_pending_seconds;
    }

    public String getRegularPrice() {
        return regular_price;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public String getTotalQuantity() {
        return total_quantity;
    }

    public String getQuantityConsumed() {
        return quantity_consumed;
    }

    public String getPurchase_link_status() {
        return purchase_link_status;
    }

    public String getView_count() {
        return view_count;
    }

    public String getFinePrint() {
        return fine_print;
    }

    public String getValidity() {
        return validity;
    }

    public String getPublish_on_date() {
        return publish_on_date;
    }

    public String getPublish_on_time() {
        return publish_on_time;
    }

    public String getFinalPrice() {
        return final_price;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
