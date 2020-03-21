package com.jaslieb.scheduleapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy hh:mm");
    public static String format(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    public static String formatToDateString(long begin) {
        return fmt.format(new Date(begin));
    }
}
