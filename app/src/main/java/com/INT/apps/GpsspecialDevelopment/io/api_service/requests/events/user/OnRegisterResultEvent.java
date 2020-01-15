package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterResult;

/**
 * Created by shrey on 17/6/15.
 */
public class OnRegisterResultEvent
{
    RegisterResult mRegisterResult;
    public OnRegisterResultEvent(RegisterResult registerResult)
    {
        mRegisterResult = registerResult;
    }

    public RegisterResult getRegisterResult()
    {
        return mRegisterResult;
    }
}
