package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * Created by shrey on 27/8/15.
 */
public class LoadMyPurchasedDealsEvent {

    private final int page;
    private final boolean used;
    private String location;

    public LoadMyPurchasedDealsEvent(int page, boolean used, Double[] latLng) {
        this.page = page;
        this.used = used;
        if (latLng[0] == null && latLng[1] == null)
            return;
        location = latLng[0] + "," + latLng[1];
    }

    public int getPage() {
        return page;
    }

    public boolean isUsed() {
        return used;
    }

    public String getLocation() {
        return location;
    }
}
