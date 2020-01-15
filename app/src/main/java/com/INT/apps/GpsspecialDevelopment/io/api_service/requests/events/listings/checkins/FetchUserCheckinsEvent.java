package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins;

/**
 * Created by shrey on 24/8/15.
 */
public class FetchUserCheckinsEvent
{
    String mUserId;
    Integer mPage;
    public FetchUserCheckinsEvent(String userId,Integer page)
    {
        mUserId = userId;
        mPage = page;
    }

    public String getUserId()
    {
        return mUserId;
    }

    public Integer getPage() {
        return mPage;
    }
}
