package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingPaging;

/**
 * Created by shrey on 1/5/15.
 */
public class ListingPaginationDataEvent
{
    ListingPaging mListingPaging;
    private String requestKey;
    public ListingPaginationDataEvent(ListingPaging listingPaging)
    {
        mListingPaging = listingPaging;
    }
    public ListingPaginationDataEvent(ListingPaging listingPaging,String requestKey)
    {
        mListingPaging = listingPaging;
        this.requestKey = requestKey;
    }

    public String getRequestKey()
    {
        return requestKey;
    }

    public ListingPaging getListingPaging()
    {
        return mListingPaging;
    }
}
