package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings;


import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listings;

import java.util.ArrayList;


/**
 * Created by shrey on 23/4/15.
 */
public class HotListingLoadedEvent
{
    ArrayList<Listing_> mListings = new ArrayList<Listing_>();
    public HotListingLoadedEvent(Listings listings)
    {
        for(Listing listing: listings.getListings())
        {
            mListings.add(listing.getListing());
        }
    }

    public ArrayList<Listing_> getListings()
    {
        return mListings;
    }

}
