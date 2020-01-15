package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;

/**
 * Created by shrey on 20/5/15.
 */
public class ListingViewLoadedEvent
{
      private Listing_ mListing;
      public ListingViewLoadedEvent(Listing_ listing)
      {
            mListing = listing;
      }

      public Listing_ getListing()
      {
          return mListing;
      }
}
