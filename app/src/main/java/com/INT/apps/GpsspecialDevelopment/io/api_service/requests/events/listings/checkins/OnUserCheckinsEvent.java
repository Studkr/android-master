package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.checkins;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.checkins.UserCheckins;

/**
 * Created by shrey on 24/8/15.
 */
public class OnUserCheckinsEvent
{
    UserCheckins mUserCheckins;
    public OnUserCheckinsEvent(UserCheckins userCheckins)
    {
        mUserCheckins = userCheckins;
    }

    public UserCheckins getUserCheckins() {
        return mUserCheckins;
    }
}
