package com.jaslieb.scheduleapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy hh:mm");

    public static String formatToDateString(long begin) {
        return fmt.format(new Date(begin));
    }
}
