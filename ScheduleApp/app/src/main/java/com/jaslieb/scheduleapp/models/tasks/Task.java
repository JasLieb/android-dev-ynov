package com.jaslieb.scheduleapp.models.tasks;

import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;

public class Task {
    public String name;
    public String childrenId;
    public long begin;
    public long duration;

    public TaskTypeEnum type;
    public TimeUnitEnum recurrence;
    public Reminder reminder;

    public boolean parentWarned;

    public Task() {}

    public Task(
        String name,
        String childrenId,
        long begin,
        long duration,
        TaskTypeEnum type,
        TimeUnitEnum recurrence,
        Reminder reminder,
        boolean parentWarned
    ) {
        this.name = name;
        this.childrenId = childrenId;
        this.begin = begin;
        this.duration = duration;
        this.type = type;
        this.recurrence = recurrence;
        this.reminder = reminder;
        this.parentWarned = parentWarned;
    }

    public static Task makeDefault() {
        return new Task();
    }
}
