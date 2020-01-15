package com.INT.apps.GpsspecialDevelopment;

import android.content.res.Resources;
import android.util.TypedValue;

public class CvSettings {

    static public String getServerUrl() {
        return BuildConfig.BASE_URL;
    }

    static public int getScreenWidthInDp() {
        return CrowdvoxApplication.getAppInstance().
                getResources().getConfiguration().screenWidthDp;
    }

    static public int getScreenHeightInDp() {
        return CrowdvoxApplication.getAppInstance().
                getResources().getConfiguration().screenHeightDp;
    }

    static public float dpToPixel(float value) {
        Resources r = CrowdvoxApplication.getAppInstance().getResources();
        float points = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
        return points;
    }
}