package com.jaslieb.scheduleapp.models.enums;

import androidx.annotation.NonNull;

import java.time.temporal.ChronoUnit;

public enum TimeUnitEnum {
    SECONDS(ChronoUnit.SECONDS,0, 60),
    MINUTES(ChronoUnit.MINUTES, 1, 60),
    HOURS(ChronoUnit.HOURS, 2, 24),
    DAYS(ChronoUnit.DAYS, 3, 6),
    WEEKS(ChronoUnit.WEEKS,4, 3),
    MONTHS(ChronoUnit.MONTHS, 5, 11),
    YEARS(ChronoUnit.YEARS, 6, 5);

    public int position;
    public int max;
    private ChronoUnit unit;

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
}
