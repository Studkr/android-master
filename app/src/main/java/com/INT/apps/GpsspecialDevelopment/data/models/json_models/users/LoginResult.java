package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 10/6/15.
 */
public class LoginResult
{
    @SerializedName("loginResult")
    LoginResult_ loginResult;

    public LoginResult(LoginResult_ loginResult)
    {
        this.loginResult = loginResult;
    }
    public LoginResult_ getLoginResult() {
        return loginResult;
    }

    public static class LoginResult_
    {
        String message;
        boolean result = false;
        UserAuthToken UserAuthToken;
        @SerializedName("User")
        User user;

        public String getMessage()
        {
            return message;
        }

        public UserAuthToken getUserAuthToken()
        {
            return UserAuthToken;
        }

        public boolean getResult()
        {
            return result;
        }

        public User getUser() {
            return user;
        }
    }
}

