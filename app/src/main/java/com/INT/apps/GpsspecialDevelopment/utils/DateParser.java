package com.INT.apps.GpsspecialDevelopment.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by shrey on 29/5/15.
 */
public class DateParser {
    public static Date fromStringToDate(String dateString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-d H:m:s", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {

        }
        return date;
    }

    public static String stringDateToPretty(String created) {
        Date date = fromStringToDate(created);
        //Date now = new SimpleDat
        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(date);
    }

    public static String stringDateToDesignPretty(String created) {
        Date date = fromStringToDate(created);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return formatter.format(date);
    }
}
