package com.jaslieb.scheduleapp.models.enums;

import androidx.annotation.NonNull;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TimeUnitEnum {
    SECONDS(ChronoUnit.SECONDS,0, 59),
    MINUTES(ChronoUnit.MINUTES, 1, 59),
    HOURS(ChronoUnit.HOURS, 2, 23),
    DAYS(ChronoUnit.DAYS, 3, 6),
    WEEKS(ChronoUnit.WEEKS,4, 3),
    MONTHS(ChronoUnit.MONTHS, 5, 11),
    YEARS(ChronoUnit.YEARS, 6, 5);

    public int position;
    public int max;
    private final ChronoUnit unit;

    TimeUnitEnum(ChronoUnit unit, int position, int max) {
        this.position = position;
        this.max = max;
        this.unit = unit;
    }

    @NonNull
    @Override
    public String toString() {
        return unit.name();
    }

    public long toMilliseconds(String s) {
        return Integer.parseInt(s) * unit.getDuration().toMillis();
    }

    public long toMilliseconds(int value) {
        return value * unit.getDuration().toMillis();
    }

    public static List<TimeUnitEnum> values = Arrays.asList(TimeUnitEnum.values());
    public static List<TimeUnitEnum> invertedValues =
            Arrays
                .stream(TimeUnitEnum.values())
                .sorted((o1, o2) -> o2.position - o1.position)
                .collect(Collectors.toList());

    public static String fromMilliseconds(long milli) {
        for(TimeUnitEnum type: invertedValues) {
            long divider = type.toMilliseconds(1);
            if( milli % divider == 0){
                return String.format(
                    "%s %s",
                    Math.floor(milli / divider),
                    type.toString().toLowerCase()
                );
            }
        }
        throw new Error();
    }

    public static TimeUnitEnum find(long position) {
        for(TimeUnitEnum type: values) {
            if(type.position == position){
                return type;
            }
        }
        throw new Error();
    }

    public static TimeUnitEnum find(String name) {
        for(TimeUnitEnum type: values) {
            if(type.name().equals(name)){
                return type;
            }
        }
        throw new Error();
    }
}
