package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 3/7/15.
 */
public class AssetAddResult
{
    @SerializedName("imagesAddResult")
    private ImagesAddResult imagesAddResult;

    public ImagesAddResult getImagesAddResult() {
        return imagesAddResult;
    }

    public class ImagesAddResult
    {
        Boolean saved;
        Integer savedCount;

        public Boolean getSaved() {
            return saved;
        }

        public Integer getSavedCount() {
            return savedCount;
        }
    }
}
