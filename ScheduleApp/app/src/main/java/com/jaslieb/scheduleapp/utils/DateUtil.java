package com.jaslieb.scheduleapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy hh:mm");

    public static String formatToDateString(long begin) {
        return fmt.format(new Date(begin));
    }

    public static String formatToDateStringPrefixed(String prefix, long begin) {
        return
            String.format(
                "%s %s",
                prefix,
                fmt.format(new Date(begin))
            );
    }
}
