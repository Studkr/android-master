package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

/**
 * Created by shrey on 12/6/15.
 */
public class SendFbLoginEvent
{
    String mToken;

    public SendFbLoginEvent(String token)
    {
        mToken = token;
    }

    public String getToken()
    {
        return mToken;
    }
}
