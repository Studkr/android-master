package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import com.INT.apps.GpsspecialDevelopment.BonusInfoCallback;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses.BonusInfo;
import com.INT.apps.GpsspecialDevelopment.io.api_service.ApiService;
import com.INT.apps.GpsspecialDevelopment.io.api_service.ServiceGenerator;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class BonusesApi {

    private static ApiService api = ServiceGenerator.api;
    private static BonusesApi instance;

    public static BonusesApi getInstance() {
        if (instance == null) {
            instance = new BonusesApi();
        }
        return instance;
    }

    public void getBonusInfo(final BonusInfoCallback callback) {
        String token = UserSession.getUserSession().getAuthToken();
        api.getBonuses(token, new Callback<BonusInfo>() {
            @Override
            public void success(BonusInfo info, Response response) {
                callback.onBonusPointsReceived(info);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error);
                callback.onBonusPointsReceivingError(error);
            }
        });
    }
}
