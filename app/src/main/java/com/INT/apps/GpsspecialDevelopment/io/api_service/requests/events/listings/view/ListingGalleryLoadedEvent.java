package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.view;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.Assets;

/**
 * Created by shrey on 23/5/15.
 */
public class ListingGalleryLoadedEvent
{
    Assets mAssets;
    public ListingGalleryLoadedEvent(Assets assets)
    {
        mAssets = assets;
    }

    public Assets getAssets()
    {
        return mAssets;
    }
}
