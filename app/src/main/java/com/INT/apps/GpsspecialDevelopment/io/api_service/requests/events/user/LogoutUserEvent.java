package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

/**
 * Created by shrey on 6/8/15.
 */
public class LogoutUserEvent {

    private boolean isAutoLogOut = false;

    public LogoutUserEvent(boolean isAutoLogOut) {
        this.isAutoLogOut = isAutoLogOut;
    }

    public boolean isAutoLogOut() {
        return isAutoLogOut;
    }
}
