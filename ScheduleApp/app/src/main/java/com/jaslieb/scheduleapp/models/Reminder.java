package com.jaslieb.scheduleapp.models;

public class Reminder {
    public boolean isBeforeTask;
    public int count;
    public int displayedCount;
    public long duration;

    public Reminder() {};

    public Reminder(boolean isBeforeTask, int count, long duration, int displayedCount) {
        this.isBeforeTask = isBeforeTask;
        this.count = count;
        this.duration = duration;
        this.displayedCount = displayedCount;
    }
}
