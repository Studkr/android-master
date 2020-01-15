package com.INT.apps.GpsspecialDevelopment.utils;

import android.content.res.Resources;

import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtils {

    private static String EMPTY_RESULT = "";

    static NumberFormat numberFormat = NumberFormat.getInstance(new Locale("en","US"));

    public static String getStringRes(Resources resources, int stringId, Object... formatArgs) {
        if (formatArgs == null || formatArgs.length == 0) {
            return resources.getString(stringId, formatArgs);
        }

        Object[] formattedArgs = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            formattedArgs[i] = (formatArgs[i] instanceof Number) ?
                    numberFormat.format(formatArgs[i]) :
                    formatArgs[i];
        }
        return resources.getString(stringId, formattedArgs);
    }

    public static Object[] getString(Object... formatArgs) {
        if (formatArgs == null || formatArgs.length == 0) {
            return new String [] {EMPTY_RESULT};
        }

        Object[] formattedArgs = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            formattedArgs[i] = (formatArgs[i] instanceof Number) ?
                    numberFormat.format(formatArgs[i]) :
                    formatArgs[i];
        }
        return formattedArgs;
    }
}
