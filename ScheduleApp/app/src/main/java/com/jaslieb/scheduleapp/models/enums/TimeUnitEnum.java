package com.jaslieb.scheduleapp.models.enums;

import androidx.annotation.NonNull;

import java.time.temporal.ChronoUnit;

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

    public long toMilliseconds(String s) {
        return Integer.parseInt(s) * unit.getDuration().toMillis();
    }

    public long toMilliseconds(int value) {
        return value * unit.getDuration().toMillis();
    }

    @NonNull
    @Override
    public String toString() {
        return unit.name();
    }

    public static TimeUnitEnum find(long position) {
        for(TimeUnitEnum type: TimeUnitEnum.values()) {
            if(type.position == position){
                return type;
            }
        }
        throw new Error();
    }

    public static TimeUnitEnum find(String name) {
        for(TimeUnitEnum type: TimeUnitEnum.values()) {
            if(type.name() == name){
                return type;
            }
        }
        throw new Error();
    }
}
