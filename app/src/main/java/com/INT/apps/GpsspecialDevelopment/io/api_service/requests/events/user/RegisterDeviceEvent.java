package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.push.DeviceInfo;

/**
 * @author Michael Soyma (Created on 10/17/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RegisterDeviceEvent {

    private final DeviceInfo deviceInfo;

    public RegisterDeviceEvent(final String token) {
        this.deviceInfo = new DeviceInfo(token);
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
