package com.INT.apps.GpsspecialDevelopment;

import android.app.Application;
import android.content.Intent;

import com.INT.apps.GpsspecialDevelopment.activities.HomeActivity;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.DealApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.DynamicCategoryRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.EditProfileApi;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.ListingApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.LocationUpdateRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.LoginApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.MerchantApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.ReviewApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.UserApiLogOutRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.UserApiRequest;
import com.google.firebase.FirebaseApp;

import co.lokalise.android.sdk.LokaliseSDK;
import timber.log.Timber;

/**
 * Created by shrey on 23/4/15.
 */
public class CrowdvoxApplication extends Application {
    private static volatile CrowdvoxApplication mApplication;

    public static CrowdvoxApplication getAppInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        FirebaseApp.initializeApp(this);
        initLocalize();

        IOBus bus = IOBus.getInstance();
        ListingApiRequest apiRequest = new ListingApiRequest(bus);
        ReviewApiRequest reviewApiRequest = new ReviewApiRequest(bus);
        LoginApiRequest loginApiRequest = new LoginApiRequest(bus);
        EditProfileApi editProfileApi = new EditProfileApi(bus);
        DealApiRequest dealApiRequest = new DealApiRequest(bus);
        MerchantApiRequest merchantApiRequest = new MerchantApiRequest(bus);
        UserApiRequest userApiRequest = new UserApiRequest(bus);
        UserApiLogOutRequest userApiLogOutRequest = new UserApiLogOutRequest(bus);
        bus.register(userApiRequest);
        bus.register(apiRequest);
        bus.register(loginApiRequest);
        bus.register(editProfileApi);
        bus.register(reviewApiRequest);
        bus.register(dealApiRequest);
        bus.register(merchantApiRequest);
        bus.register(userApiLogOutRequest);
        LocationUpdateRequest locationUpdateRequest = new LocationUpdateRequest(bus);
        bus.register(locationUpdateRequest);

        DynamicCategoryRequest dynamicCategoryRequest = new DynamicCategoryRequest(getApplicationContext(), bus);
        bus.register(dynamicCategoryRequest);

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        /*MainActivity activity = new MainActivity();
        bus.register(activity);*/
    }

    public void restartApp() {
        final Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(HomeActivity.EXTRA_NEED_DISPLAY_LOGIN, true);
        startActivity(intent);
    }

    private void initLocalize() {
        LokaliseSDK.init("6ad80f7472b561e4c7ac6486c041aa5019023b7a", "504803275ad059b1d649e7.45609499", this);
        LokaliseSDK.updateTranslations();
    }
}
