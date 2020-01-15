package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;


import com.INT.apps.GpsspecialDevelopment.BuildConfig;
import com.INT.apps.GpsspecialDevelopment.CvSettings;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.ApiService;
import com.INT.apps.GpsspecialDevelopment.io.api_service.converters.CvGsonConverter;
import com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors.ExpireTokenInterceptor;
import com.INT.apps.GpsspecialDevelopment.io.api_service.interceptors.HeadersInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

/**
 * Created by shrey on 23/4/15.
 */
abstract public class ApiRequest implements TypeAdapterFactory {

    private IOBus mBus;

    public ApiRequest(IOBus bus) {
        mBus = bus;
    }

    protected ApiService getRequestApi() {
        List<Interceptor> interceptors = Arrays.asList(
                new ExpireTokenInterceptor(),
                new HeadersInterceptor()
        );

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().addAll(interceptors);


        return new RestAdapter.Builder().
                setEndpoint(CvSettings.getServerUrl()).
                setConverter(new CvGsonConverter(getGson())).
                setClient(new OkClient(okHttpClient)).
                setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE).
                setLog(new AndroidLog("retrofit")).
                build().create(ApiService.class);
    }

    protected Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(this)
                .create();
        return gson;

    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementAdapter.read(in);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                        jsonElement = jsonObject.get("data");
                    }
                }
                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }

    protected IOBus getBus() {
        return mBus;
    }
}