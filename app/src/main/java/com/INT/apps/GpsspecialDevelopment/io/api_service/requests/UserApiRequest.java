package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.UserProfile;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadUserProfileEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserProfileLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by shrey on 17/8/15.
 */
public class UserApiRequest extends ApiRequest
{
    public UserApiRequest(IOBus bus)
    {
        super(bus);
    }

    @Subscribe
    public void loadUserProfile(LoadUserProfileEvent event)
    {
        String userId = event.getUserId();
        Callback<UserProfile> cb = new Callback<UserProfile>() {
            @Override
            public void success(UserProfile userProfile, Response response)
            {
                getBus().post(new UserProfileLoadedEvent(userProfile.getUser()));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d(error,"@@#");
            }
        };
        if(userId != null)
        {
            getRequestApi().userProfile(userId, UserSession.getUserSession().getAuthToken(),cb);
        }else if(UserSession.getUserSession().getAuthToken() != null)
        {
            getRequestApi().myProfile( UserSession.getUserSession().getAuthToken(),cb);
        }
    }
}
