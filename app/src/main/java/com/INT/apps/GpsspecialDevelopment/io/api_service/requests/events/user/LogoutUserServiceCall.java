package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.User;

/**
 * Created by msaqib on 5/9/2017.
 */

public class LogoutUserServiceCall {
    private User user;
    private Double lastLat, lastLng;

    public LogoutUserServiceCall(User user) {
        this.user = user;
    }

    public void setLastLatitude(Double lat) {
        this.lastLat = lat;
    }

    public void setLastLongitude(Double lng) {
        this.lastLng = lng;
    }

    public Double getLastLatitude() {
        return lastLat;
    }

    public Double getLastLongitude() {
        return lastLng;
    }

    public boolean isLastPositionPresent() {
        return lastLat != null && lastLng != null;
    }

    public User getUser() {
        return user;
    }
}
