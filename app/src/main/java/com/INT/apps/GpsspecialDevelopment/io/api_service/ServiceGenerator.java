package com.INT.apps.GpsspecialDevelopment.io.api_service;

import com.INT.apps.GpsspecialDevelopment.BuildConfig;
import com.INT.apps.GpsspecialDevelopment.io.api_service.converters.CvGsonConverter;
import com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors.ExpireTokenInterceptor;
import com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors.HeadersInterceptor;
import com.INT.apps.GpsspecialDevelopment.io.api_service.typeadapters.DataTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.Converter;

public class ServiceGenerator {

    private static final String BASE_URL = BuildConfig.BASE_URL;

    public static ApiService api =
            new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setConverter(getConverter())
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .setLog(new AndroidLog("retrofit"))
                    .setClient(new OkClient(getHttpClient()))
                    .build()
                    .create(ApiService.class);

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new DataTypeAdapter())
                .create();
    }

    private static Converter getConverter() {
        return new CvGsonConverter(getGson());
    }


    private static OkHttpClient getHttpClient() {
        List<Interceptor> interceptors = Arrays.asList(
                new ExpireTokenInterceptor(),
                new HeadersInterceptor()
        );

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient
                .interceptors()
                .addAll(interceptors);

        return okHttpClient;
    }
}
