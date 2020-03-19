package com.jaslieb.scheduleapp.models;

import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;

public class Task {
    public String name;
    public String childrenId;
    public long begin;
    public long duration;

    public TaskTypeEnum type;

    public Task() {}

    public Task(
            String name,
            String childrenId,
            long begin,
            long duration,
            TaskTypeEnum type
    ) {
        this.name = name;
        this.childrenId = childrenId;
        this.begin = begin;
        this.duration = duration;
        this.type = type;
    }
}
