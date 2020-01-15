package com.INT.apps.GpsspecialDevelopment.utils;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.CvSettings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shrey on 23/5/15.
 */
public class CvUrls {

    public static String getImageUrlForSize(String imageUrl, Integer height, Integer width, boolean crop) {
        if (!TextUtils.isEmpty(imageUrl)) {
            String[] parts = TextUtils.split(imageUrl, "/");
            ArrayList<String> urlParts = new ArrayList<>(Arrays.asList(parts));
            String fileName = urlParts.get(urlParts.size() - 1);
            urlParts.remove(urlParts.size() - 1);
            Resources r = CrowdvoxApplication.getAppInstance().getResources();
            float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, r.getDisplayMetrics());
            float widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics());
            String cropString = "";
            if (crop == true) {
                cropString = "crop/";
            }
            int heightInt = Math.round(heightPx);
            int widthInt = Math.round(widthPx);
            String thumbPart = "/thumbs/" + cropString + widthInt + "x" + heightInt;
            String fileUrl = TextUtils.join("/", urlParts) + thumbPart + "/" + fileName;
            return CvSettings.getServerUrl() + fileUrl;
        } else return CvSettings.getServerUrl();
    }

    public static String getImageUrl(String imageUrl) {
        return CvSettings.getServerUrl() + imageUrl;
    }

    public static String getAsUrl(String url) {
        if (url.matches("^http(s)?://.*") == false) {
            url = "http://" + url;
        }
        return url;
    }
}
