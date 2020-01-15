package com.INT.apps.GpsspecialDevelopment.utils;

import android.text.Spanned;
import android.text.method.DigitsKeyListener;

import timber.log.Timber;

public class DecimalDigitsInputFilter extends DigitsKeyListener {

    private static final int MIN_VALUE = 0;
    private static final int BEFORE_DECIMAL = 6;
    private static final int AFTER_DECIMAL = 2;
    private static final String EMPTY = "";
    private static final String SEPARATOR_COMA = ",";
    private static final String SEPARATOR_PERIOD = ".";
    private static final String CURRENCY_SIGN = "$";

    private double maxValue;

    public DecimalDigitsInputFilter(double maxValue, boolean sign, boolean decimal) {
        super(sign, decimal);
        this.maxValue = maxValue;
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Timber.d("filter source: %s", source);
        double input;
        String unprocessedResult = new StringBuffer(dest).insert(dstart, source).toString();
        if (unprocessedResult.length() == 1 && unprocessedResult.startsWith(CURRENCY_SIGN)) {
            return CURRENCY_SIGN;
        }

        String temp = unprocessedResult.replace(CURRENCY_SIGN, EMPTY);
        if (unprocessedResult.contains(SEPARATOR_COMA)) {
            temp = unprocessedResult.replace(SEPARATOR_COMA, SEPARATOR_PERIOD).replace(CURRENCY_SIGN, EMPTY);
        }
        try {
            input = Double.parseDouble(temp);
        } catch (NumberFormatException e) {
            return EMPTY;
        }

        if (input <= maxValue && input >= MIN_VALUE) {
            if (!temp.contains(SEPARATOR_PERIOD)) {
                if (temp.length() > BEFORE_DECIMAL) {
                    return EMPTY;
                }
            } else {
                temp = temp.substring(temp.indexOf(SEPARATOR_PERIOD) + 1);
                if (temp.length() > AFTER_DECIMAL) {
                    return EMPTY;
                }
            }
        } else {
            return EMPTY;
        }

        return super.filter(source, start, end, dest, dstart, dend);
    }
}
