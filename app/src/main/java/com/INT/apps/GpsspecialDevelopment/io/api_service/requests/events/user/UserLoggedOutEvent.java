package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

/**
 * Created by shrey on 6/8/15.
 */
public class UserLoggedOutEvent {

    private boolean isAutoLogOut = false;

    public UserLoggedOutEvent(boolean isAutoLogOut) {
        this.isAutoLogOut = isAutoLogOut;
    }

    public boolean isAutoLogOut() {
        return isAutoLogOut;
    }
}
