package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;

/**
 * Created by shrey on 17/6/15.
 */
public class SetUserLoginEvent
{
    LoginResult mLoginResult;
    public SetUserLoginEvent(LoginResult loginResult)
    {
        mLoginResult = loginResult;
    }

    public LoginResult getLoginResult()
    {
        return mLoginResult;
    }
}
