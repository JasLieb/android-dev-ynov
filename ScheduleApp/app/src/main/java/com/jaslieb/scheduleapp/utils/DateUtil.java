package com.jaslieb.scheduleapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.forLanguageTag("fr"));

    public static String formatToDateString(long begin) {
        return fmt.format(new Date(begin));
    }
}
