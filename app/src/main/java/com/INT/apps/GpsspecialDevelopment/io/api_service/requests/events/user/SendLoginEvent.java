package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

/**
 * Created by shrey on 10/6/15.
 */
public class SendLoginEvent
{
    String mEmail;
    String mPassword;
    public SendLoginEvent(String email,String password)
    {
        mEmail = email;
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }
}
