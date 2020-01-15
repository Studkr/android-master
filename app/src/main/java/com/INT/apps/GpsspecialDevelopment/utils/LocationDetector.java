package com.INT.apps.GpsspecialDevelopment.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by shrey on 16/4/15.
 */
public class LocationDetector implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Context mContext;
    final static public int LOCATION_INTENT = 100;
    static private LocationDetector sLocationDetector;
    private GoogleApiClient mGoogleApiClient;
    private Double lastLatitude = null;
    private Double lastLongitude = null;
    private boolean dummyLocation = false;

    public LocationDetector(Context context) {
        mContext = context;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gpsEnabled || networkEnabled;
    }

    public static LocationDetector getInstance(Context context) {
        if (sLocationDetector == null) {
            sLocationDetector = new LocationDetector(context);
        }
        return sLocationDetector;
    }

    public void showSettingsDialog(final Activity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.location_services_message);
        dialog.setMessage(R.string.location_disabled_message);
        dialog.setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, LOCATION_INTENT);
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public Double[] getLastLocation() {
        return new Double[]{lastLatitude, lastLongitude};
    }

    public void getLocation() {
        getLocation(false);
    }

    synchronized public void getLocation(boolean forceLoad) {
        if (dummyLocation && lastLongitude != null) {
            notifyLocation(lastLatitude, lastLongitude);
            return;
        }
        if (!forceLoad && lastLongitude == null && mGoogleApiClient != null) {
            IOBus.getInstance().post(new OnLocationFetchErrorEvent());
            return;
        }
        if (forceLoad || lastLongitude == null) {
            buildGoogleApiClient();
        } else if (lastLongitude != null) {
            notifyLocation(lastLatitude, lastLongitude);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude;
        double longitude;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            latitude = 0;
            longitude = 0;
        }
        lastLatitude = latitude;
        lastLongitude = longitude;
        notifyLocation(latitude, longitude);
        mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }

    @Override
    public void onConnectionSuspended(int i) {
        IOBus.getInstance().post(new OnLocationFetchErrorEvent());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        IOBus.getInstance().post(new OnLocationFetchErrorEvent());
    }

    public void notifyLocation(double latitude, double longitude) {
        IOBus.getInstance().post(new OnLocationEvent(latitude, longitude));
    }

    public void setDummyLocation(double latitude, double longitude) {
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;
        dummyLocation = true;
    }

    public class OnLocationEvent {
        private double latitude;
        private double longitude;

        protected OnLocationEvent(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public class OnLocationFetchErrorEvent {

    }
}
