package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import android.os.AsyncTask;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Data;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.push.DeviceInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.RegisterResult;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.ApiRequestFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.CheckUserSessionEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LoadRegisterFormEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.LogoutUserEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnLoginResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.OnRegisterResultEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RegisterDeviceEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RegisterFormLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RequestRegisterEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.SendFbLoginEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.SendLoginEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.SetUserLoginEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserLoggedOutEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.UserSessionLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by shrey on 10/6/15.
 */
public class LoginApiRequest extends ApiRequest {
    public LoginApiRequest(IOBus bus) {
        super(bus);
    }

    @Subscribe
    public void login(SendLoginEvent event) {
        getRequestApi().login(event.getEmail(), event.getPassword(), new Callback<LoginResult>() {
            @Override
            public void success(LoginResult loginResult, Response response) {
                handleLoginResult(loginResult);
            }

            @Override
            public void failure(RetrofitError error) {
                getBus().post(new ApiRequestFailedEvent(error));
            }
        });
    }

    @Subscribe
    public void setUserLoginResult(SetUserLoginEvent event) {
        handleLoginResult(event.getLoginResult());
    }

    private void handleLoginResult(final LoginResult loginResult) {
        getBus().post(new OnLoginResultEvent(loginResult));
        if (loginResult.getLoginResult().getResult() == false) {
            return;
        }
        UserSession.getUserSession().setSessionUser(loginResult.getLoginResult().getUser(), loginResult.getLoginResult().getUserAuthToken().getToken());
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                        .setUserSession(loginResult.getLoginResult());
                //Register device token
                getBus().post(new RegisterDeviceEvent(FirebaseInstanceId.getInstance().getToken()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                getBus().post(new UserSessionLoadedEvent());
                super.onPostExecute(aVoid);
            }
        };
        task.execute();
    }

    @Subscribe
    public void checkSavedUserSession(CheckUserSessionEvent event) {
        AsyncTask<Void, Void, LoginResult.LoginResult_> task = new AsyncTask<Void, Void, LoginResult.LoginResult_>() {
            @Override
            protected LoginResult.LoginResult_ doInBackground(Void... params) {
                LoginResult.LoginResult_ authToken = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance())
                        .getUserSession();
                return authToken;
            }

            @Override
            protected void onPostExecute(LoginResult.LoginResult_ loginResult) {
                if (loginResult != null) {
                    Timber.tag("found user session").d("user session");
                    UserSession.getUserSession().setSessionUser(loginResult.getUser(), loginResult.getUserAuthToken().getToken());
                    getBus().post(new UserSessionLoadedEvent());
                }
                super.onPostExecute(loginResult);
            }
        };
        task.execute();
    }

    @Subscribe
    public void facebookLogin(SendFbLoginEvent event) {
        String token = event.getToken();
        getRequestApi().loginByFacebook(token, new Callback<LoginResult>() {
            @Override
            public void success(LoginResult loginResult, Response response) {
                handleLoginResult(loginResult);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe
    public void loadRegisterForm(LoadRegisterFormEvent event) {
        AsyncTask<Void, Void, FieldsProperties> task = new AsyncTask<Void, Void, FieldsProperties>() {
            @Override
            protected FieldsProperties doInBackground(Void... params) {
                JsonCacheManager cacheManager = JsonCacheManager.
                        getInstance(CrowdvoxApplication.getAppInstance());
                FieldsProperties fieldsProperties = (FieldsProperties) cacheManager.getCachedObject("RegisterForm", "1", FieldsProperties.class);
                if (fieldsProperties == null) {
                    try {
                        fieldsProperties = getRequestApi().getRegistrationFieldsSync();
                    } catch (RetrofitError error) {

                    }
                    if (fieldsProperties != null) {
                        cacheManager.cacheObject("RegisterForm", "1", fieldsProperties);
                    }
                }
                return fieldsProperties;
            }

            @Override
            protected void onPostExecute(FieldsProperties fieldProperties) {
                if (fieldProperties != null) {
                    IOBus.getInstance().post(new RegisterFormLoadedEvent(fieldProperties));
                }
                super.onPostExecute(fieldProperties);
            }
        };
        task.execute();
    }

    @Subscribe
    public void registerUser(RequestRegisterEvent event) {
        getRequestApi().registerUser(event.getRegisterData(), new Callback<RegisterResult>() {
            @Override
            public void success(RegisterResult registerResult, Response response) {
                getBus().post(new OnRegisterResultEvent(registerResult));
            }

            @Override
            public void failure(RetrofitError error) {
                final RegisterResult registerResult = new RegisterResult();
                registerResult.setIsConnectionError(true);
                getBus().post(new OnRegisterResultEvent(registerResult));
            }
        });
    }

    @Subscribe
    public void logoutUser(final LogoutUserEvent event) {
        UserSession.getUserSession().logoutUser();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance()).removeUserSession();
                UserSession.getUserSession().logoutUser();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                IOBus.getInstance().post(new UserLoggedOutEvent(event.isAutoLogOut()));
                super.onPostExecute(aVoid);
            }
        };
        task.execute();
    }

    @Subscribe
    public void registerDevice(final RegisterDeviceEvent registerDeviceEvent) {
        final UserSession userSession = UserSession.getUserSession();
        if (userSession != null && userSession.isLoggedIn()) {
            final String token = UserSession.getUserSession().getAuthToken();
            try {
                final DeviceInfo deviceInfo = getRequestApi().registerDevice(new Data<>(registerDeviceEvent.getDeviceInfo()), token);
                Timber.tag("User").d( "Device is registered: %s", deviceInfo.getDeviceId());
            } catch (RetrofitError ignore) {
            }
        }
    }
}
