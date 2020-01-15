package com.INT.apps.GpsspecialDevelopment.data.models.json_models.push;

/**
 * @author Michael Soyma (Created on 10/17/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class DeviceInfo {

    private final String device_id;
    private final String platform = "android";

    public DeviceInfo(String device_id) {
        this.device_id = device_id;
    }

    public String getDeviceId() {
        return device_id;
    }
}
