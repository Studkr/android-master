package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;


import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;

/**
 * Created by shrey on 17/8/15.
 */
public class UserProfileLoadedEvent
{
    UserProfile.User mUser;
    public UserProfileLoadedEvent(UserProfile.User user)
    {
        mUser = user;
    }

    public UserProfile.User getUser() {
        return mUser;
    }
}
