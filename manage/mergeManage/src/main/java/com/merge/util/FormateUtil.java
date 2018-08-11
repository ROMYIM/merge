package com.merge.util;

import java.util.Date;
import java.util.Calendar;

/**
 * FormateUtl
 */
public class FormateUtil {

    private static Calendar calendar;

    public static Date timeToDate(String hourString, String minuteString, String secondsString) {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONDAY);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = Integer.valueOf(hourString);
        int minute = Integer.valueOf(minuteString);
        int second = Integer.valueOf(secondsString);
        calendar.set(year, month, date, hour, minute, second);
        return calendar.getTime();
    }

    public static long stringToPeriod(String hourString) {
        int hour = Integer.valueOf(hourString);
        return 1000 * 60 * 60 * hour;
    }
}