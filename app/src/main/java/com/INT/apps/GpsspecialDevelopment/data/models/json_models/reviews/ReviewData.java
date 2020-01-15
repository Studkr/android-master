package com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.Postable;

import java.util.HashMap;

/**
 * Created by shrey on 19/6/15.
 */
public class ReviewData implements Postable
{
    private HashMap<String,String> mData = new HashMap<>();
    public ReviewData(HashMap<String,String> data)
    {
        mData = data;
    }
    @Override
    public HashMap<String, String> getFieldValues()
    {
        return mData;
    }

    @Override
    public String getPostModelName()
    {
        return "Review";
    }
}
