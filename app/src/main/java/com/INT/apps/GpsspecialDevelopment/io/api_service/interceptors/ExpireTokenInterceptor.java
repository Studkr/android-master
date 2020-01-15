package com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors;

import android.os.Handler;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserEvent;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author Michael Soyma (Created on 9/22/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class ExpireTokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        boolean isMerchant = originalResponse.request().url().toString().contains("/api/merchant/listings.json");
        if (!isMerchant && originalResponse.code() == 401 || originalResponse.code() == 403) {
            new Handler(CrowdvoxApplication.getAppInstance().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    IOBus.getInstance().post(new LogoutUserEvent(true));
                }
            });
        }
        return originalResponse;
    }
}
