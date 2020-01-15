package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserServiceCall;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserServiceCallResponce;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by shrey on 17/8/15.
 */
public class UserApiLogOutRequest extends ApiRequest
{
    public UserApiLogOutRequest(IOBus bus)
    {
        super(bus);
    }

    @Subscribe
    public void loadUserProfile(LogoutUserServiceCall user)
    {
        String userId = user.getUser().getId();
        Timber.tag("----**").d("userid:%s", userId);
        Callback<JSONObject> cb = new Callback<JSONObject>() {
            @Override
            public void success(JSONObject s, Response response) {
                getBus().post(new LogoutUserServiceCallResponce());
            }

            @Override
            public void failure(RetrofitError error) {
                getBus().post(new LogoutUserEvent(false));
            }
        };

        if(userId != null) {
            String position;

            if (user.isLastPositionPresent())
                position = String.format("%s,%s", user.getLastLatitude(), user.getLastLongitude());
            else position = "";

            getRequestApi().logoutProfile(position, userId, UserSession.getUserSession().getAuthToken(),cb);
        }
    }
}
