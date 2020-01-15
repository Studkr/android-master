package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 17/6/15.
 */
public class RegisterResult {

    @SerializedName("registerResult")
    RegisterResult_ registerResult_;
    private boolean isConnectionError = false;

    public RegisterResult_ getRegisterResult() {
        return registerResult_;
    }

    public static class RegisterResult_
    {
        @SerializedName("errors")
        List<Error> errors = new ArrayList<>();
        User User;
        @SerializedName("loginResult")
        LoginResult.LoginResult_ loginResult;

        boolean result = true;

        public List<Error> getErrors() {
            return errors;
        }

        public boolean getResult() {
            return result;
        }

        public User getUser() {
            return User;
        }

        public LoginResult.LoginResult_ getLoginResult() {
            return loginResult;
        }
    }

    public static class Error {
        String field;
        String error;

        public String getError() {
            return error;
        }

        public String getField() {
            return field;
        }
    }

    public void setIsConnectionError(boolean isConnectionError) {
        this.isConnectionError = isConnectionError;
    }

    public boolean isConnectionError() {
        return isConnectionError;
    }
}
