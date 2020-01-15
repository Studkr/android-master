package com.INT.apps.GpsspecialDevelopment.session;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;

import timber.log.Timber;

/**
 * Created by shrey on 10/6/15.
 */
public class UserSession {
    private static UserSession sUserSession;
    private User mUser;
    private String mAuthToken;

    private UserSession() {

    }

    public static UserSession getUserSession() {
        if (sUserSession == null) {
            sUserSession = new UserSession();
        }
        return sUserSession;
    }

    public void retrieveCachedUser() {
        if (mUser == null || mAuthToken == null) {
            LoginResult.LoginResult_ loginResult = JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance()).getUserSession();
            if (loginResult != null) {
                setSessionUser(loginResult.getUser(), loginResult.getUserAuthToken().getToken());
            }
        }
    }

    public boolean isLoggedIn() {
        return mUser != null && !TextUtils.isEmpty(mAuthToken);
    }

    public void setSessionUser(User user, String authToken) {
        Timber.tag("setting ses").d("Session user %s", user.getEmail());
        mUser = user;
        mAuthToken = authToken;
    }

    public User getSessionUser() {
        return mUser;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void logoutUser() {
        mUser = null;
        mAuthToken = null;
    }
}
