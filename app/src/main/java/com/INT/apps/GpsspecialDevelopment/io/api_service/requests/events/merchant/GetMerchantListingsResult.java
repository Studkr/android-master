package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingsPaging;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class GetMerchantListingsResult {

    private final ListingsPaging listingsPaging;

    public GetMerchantListingsResult(ListingsPaging listingsPaging) {
        this.listingsPaging = listingsPaging;
    }

    public ListingsPaging getListingsPaging() {
        return listingsPaging;
    }
}
