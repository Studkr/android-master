package com.INT.apps.GpsspecialDevelopment.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance(Locale.US);
    private static final NumberFormat DECIMAL_FORMATTER = DecimalFormat.getCurrencyInstance(Locale.US);

    public static boolean hasDigitsAfterComma(double convertedValue) {
        BigDecimal bd = new BigDecimal(convertedValue - Math.floor(convertedValue));
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        return bd.floatValue() > 0;
    }

    public static String format(int value) {
        return FORMATTER.format(value);
    }

    public static String format(double value) {
        return DECIMAL_FORMATTER.format(value);
    }
}
