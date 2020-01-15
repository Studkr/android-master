package com.INT.apps.GpsspecialDevelopment.notifications;

import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.RegisterDeviceEvent;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

/**
 * @author Michael Soyma (Created on 10/13/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.tag("Token").d( "token: %s", refreshedToken);
        if (!TextUtils.isEmpty(refreshedToken))
            IOBus.getInstance().post(new RegisterDeviceEvent(refreshedToken));
    }
}
