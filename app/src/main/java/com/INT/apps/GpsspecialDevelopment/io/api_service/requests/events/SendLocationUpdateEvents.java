package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events;

/**
 * Created by msaqib on 6/25/2016.
 */
public class SendLocationUpdateEvents {

    public double lat;
    public double lang;


    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }

    public SendLocationUpdateEvents(double lat, double lang) {

        this.lat = lat;
        this.lang = lang;
    }
}
