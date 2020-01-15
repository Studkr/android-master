package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets;

import java.util.ArrayList;

/**
 * Created by shrey on 1/7/15.
 */
public class PostListingImagesEvent
{
    private ArrayList<String> mFilesPath;
    private String mListingId;
    private String mRequestKey;
    public  PostListingImagesEvent(String listingId,ArrayList<String> filesPath,String requestKey)
    {
        mFilesPath = filesPath;
        mListingId = listingId;
        mRequestKey = requestKey;

    }

    public ArrayList<String> getFilesPath()
    {
        return mFilesPath;
    }

    public String getListingId() {
        return mListingId;
    }

    public String getRequestKey()
    {
        return mRequestKey;
    }
}
