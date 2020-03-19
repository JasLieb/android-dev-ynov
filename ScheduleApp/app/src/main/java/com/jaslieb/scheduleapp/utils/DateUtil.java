package com.jaslieb.scheduleapp.utils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class DateUtil {
    public static String format(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }
}
