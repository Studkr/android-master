package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Data;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditProfileEntity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnEditResultEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileApi extends ApiRequest {
    public EditProfileApi(IOBus bus) {
        super(bus);
    }

    @Subscribe
    public void editProfile(EditProfileEntity entity) {
        String token = UserSession.getUserSession().getAuthToken();
        User user = UserSession.getUserSession().getSessionUser();
        String[] strings = user.getDisplayName().split(" ");
        getRequestApi().editProfile(token, strings[0], strings[1], entity.getPhone(), new Callback<EditResult>() {
            @Override
            public void success(EditResult editResult, Response response) {
                String token = UserSession.getUserSession().getAuthToken();
                UserSession.getUserSession().setSessionUser(editResult.getEditResult().getUser(), token);
                getBus().post(new OnEditResultEvent(editResult));
            }

            @Override
            public void failure(RetrofitError error) {
                getBus().post(new ApiRequestFailedEvent(error));
            }
        });
    }
}
