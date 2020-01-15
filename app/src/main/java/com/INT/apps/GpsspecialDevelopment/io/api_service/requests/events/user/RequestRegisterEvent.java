package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterData;

/**
 * Created by shrey on 17/6/15.
 */
public class RequestRegisterEvent
{
    RegisterData mRegisterData;
    public RequestRegisterEvent(RegisterData registerData)
    {
        mRegisterData = registerData;
    }

    public RegisterData getRegisterData()
    {
        return mRegisterData;
    }
}
