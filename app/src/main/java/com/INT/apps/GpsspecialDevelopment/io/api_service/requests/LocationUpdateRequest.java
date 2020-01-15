package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;


import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.LocationUpdateEventResult;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.SendLocationUpdateEvents;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by msaqib on 6/25/2016.
 */
public class LocationUpdateRequest extends ApiRequest {
    private String userId = "";

    public LocationUpdateRequest(IOBus bus) {
        super(bus);
        LoginResult.LoginResult_ authToken = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                .getUserSession();
        try{
            userId = authToken.getUser().getId();
            Timber.d("----user id :%s", userId);
        }catch (Exception e){
            //if user is not registerd
            userId = "0";
            Timber.d("----user id catch:%s", userId);
        }

     //   System.out.println("-------------login id request: " + authToken.getUser().getId());
    }

    @Subscribe
    public void locationUpdate(SendLocationUpdateEvents loc) {
        Timber.d("----Location update to server request received ");
        getRequestApi().updateUserLocation(userId, String.valueOf(loc.getLat()), String.valueOf(loc.getLang()), new Callback<Object>() {
            @Override
            public void success(Object s, Response response) {
                Timber.d("----Location update to server success");
                handleLoginResult(s);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.d(error, "----Location update to server failed ");
                handleLoginResult(error);
            }
        });
    }

    public void handleLoginResult(Object o) {
        //here we can handle login result
        Timber.d("----Send result to home activity ");
        getBus().post(new LocationUpdateEventResult(o));
    }
}
