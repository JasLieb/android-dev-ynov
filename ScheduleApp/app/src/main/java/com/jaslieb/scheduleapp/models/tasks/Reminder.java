package com.jaslieb.scheduleapp.models.tasks;

public class Reminder {
    public boolean isTriggered;
    public boolean isBeforeTask;
    public int count;
    public long duration;

    public Reminder() {};

    public Reminder(boolean isTriggered, boolean isBeforeTask, int count, long duration) {
        this.isTriggered = isTriggered;
        this.isBeforeTask = isBeforeTask;
        this.count = count;
        this.duration = duration;
    }
}
