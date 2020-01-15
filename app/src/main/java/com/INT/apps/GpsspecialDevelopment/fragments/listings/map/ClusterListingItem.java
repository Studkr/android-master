package com.INT.apps.GpsspecialDevelopment.fragments.listings.map;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * @author Michael Soyma (Created on 9/20/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class ClusterListingItem implements ClusterItem {

    private final Listing_ listing;

    public ClusterListingItem(Listing_ listing) {
        this.listing = listing;
    }

    @Override
    public LatLng getPosition() {
        final Double latitude = listing.getLatitude();
        final Double longitude = listing.getLongitude();
        return (latitude == null || longitude == null) ? null : new LatLng(latitude, longitude);
    }

    public Listing_ getListing() {
        return listing;
    }
}
