package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.Postable;

import java.util.HashMap;

/**
 * Created by shrey on 17/6/15.
 */
public class RegisterData implements Postable
{
    private HashMap<String,String> mData = new HashMap<>();

    public RegisterData(HashMap<String,String> data)
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
        return "User";
    }
}
