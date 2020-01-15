package com.INT.apps.GpsspecialDevelopment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.LoginApiRequest;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadRegisterFormEvent;
import com.INT.apps.GpsspecialDevelopment.utils.ThreadExecutor;

import java.util.concurrent.Executor;

import timber.log.Timber;

public class LanguageChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("Language has changed");
        final JsonCacheManager cacheManager = JsonCacheManager.
                getInstance(context);
        Executor executor = new ThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cacheManager.deleteCaching("RegisterForm", "1");
                cacheManager.deleteCaching("Listing", null);
            }
        });

        LoginApiRequest request = new LoginApiRequest(IOBus.getInstance());
        request.loadRegisterForm(new LoadRegisterFormEvent());
    }
}
