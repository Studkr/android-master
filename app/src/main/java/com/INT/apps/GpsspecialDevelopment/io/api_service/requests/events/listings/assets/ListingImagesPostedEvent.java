package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.AssetAddResult;

/**
 * Created by shrey on 1/7/15.
 */
public class ListingImagesPostedEvent
{
    AssetAddResult mAssetAddResult;
    public ListingImagesPostedEvent(AssetAddResult result)
    {
        mAssetAddResult = result;
    }

    public AssetAddResult getAssetAddResult()
    {
        return mAssetAddResult;
    }


}
