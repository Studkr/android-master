package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

/**
 * Created by shrey on 17/8/15.
 */
public class LoadUserProfileEvent
{
    String mUserId;
    public LoadUserProfileEvent(String userId)
    {
        mUserId = userId;
    }

    public String getUserId()
    {
        return mUserId;
    }
}
