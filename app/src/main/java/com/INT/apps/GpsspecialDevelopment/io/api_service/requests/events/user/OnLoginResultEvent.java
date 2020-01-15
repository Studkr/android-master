package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;

/**
 * Created by shrey on 10/6/15.
 */
public class OnLoginResultEvent
{
    LoginResult mLoginResult;
    public OnLoginResultEvent(LoginResult loginResult)
    {
        mLoginResult = loginResult;
    }

    public LoginResult getLoginResult()
    {
        return mLoginResult;
    }
}
