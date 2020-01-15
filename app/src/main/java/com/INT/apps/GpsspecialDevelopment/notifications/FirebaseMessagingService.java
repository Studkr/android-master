package com.INT.apps.GpsspecialDevelopment.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.MerchantPurchaseCouponsActivity;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Michael Soyma (Created on 10/13/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CUSTOM = "custom";
    public static final String KEY_DEAL_ORDER_ID = "deal_order_id";
    public static final String KEY_DEAL_ID = "deal_id";
    public static final String KEY_DEAL_TITLE = "deal_title";

    @Override
    //Call in background thread
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder mBuilder = null;
        try {
            mBuilder = new NotificationCompat.Builder(this)
                    .setContentIntent(PendingIntent.getActivity(this, 0, prepareIntent(remoteMessage.getData()), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.drawable.deal_logo_new)
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.app_name))
                    .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)
                    .setContentText(remoteMessage.getData().get(KEY_MESSAGE));
            mBuilder.getNotification().flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(NotificationIdGenerator.getInstance().generateNewId(), mBuilder.build());
    }

    private Intent prepareIntent(final Map<String, String> data) throws JSONException {
        final Intent sendIntent = new Intent(this, MerchantPurchaseCouponsActivity.class);
        final JSONObject custom = new JSONObject(data.get(KEY_CUSTOM));
        sendIntent.putExtra(MerchantPurchaseCouponsActivity.ARG_PURCHASE_DEAL_ID, custom.getString(KEY_DEAL_ID));
        sendIntent.putExtra(MerchantPurchaseCouponsActivity.ARG_PURCHASE_TITLE, custom.getString(KEY_DEAL_TITLE));
        return sendIntent;
    }
}
