package com.INT.apps.GpsspecialDevelopment.utils;

import android.content.Context;

/**
 * Created by shrey on 17/4/15.
 */
public class NetworkDetector
{
    private Context mContext;
    public NetworkDetector(Context context)
    {
        mContext = context;
    }


    public boolean isConnectedToNetwork()
    {
        return false;
    }
}
