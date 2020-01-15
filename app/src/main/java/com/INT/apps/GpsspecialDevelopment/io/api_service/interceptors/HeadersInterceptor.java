package com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;

public class HeadersInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("X-Accept-Language", Locale.getDefault().getISO3Language().substring(0, 2))
                .build();
        return chain.proceed(request);
    }
}
