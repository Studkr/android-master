package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/21/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class LoadListOfDeals {

    private final String listingId;
    private final int page;

    public LoadListOfDeals(String listingId, int page) {
        this.listingId = listingId;
        this.page = page;
    }

    public String getListingId() {
        return listingId;
    }

    public int getPage() {
        return page;
    }
}
