package com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;

/**
 * @author Michael Soyma (Created on 9/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class Order {

    private Deal.Deal_ deal;
    private Listing_ listing;
    private String tax;
    private String convention_fee;
    private UserProfile.User user;

    public Deal.Deal_ getDeal() {
        return deal;
    }

    public Listing_ getListing() {
        return listing;
    }

    public double getTax() {
        return Double.parseDouble(tax);
    }

    public double getFee() {
        return Double.parseDouble(convention_fee);
    }

    public UserProfile.User getUser() {
        return user;
    }
}
