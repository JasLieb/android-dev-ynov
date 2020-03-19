package com.jaslieb.scheduleapp.models;

public class Task {
    public String name;
    public String childrenId;
    public long begin;
    public long duration;

    public TaskType type;

    public Task() {}

    public Task(
            String name,
            String childrenId,
            long begin,
            long duration,
            TaskType type
    ) {
        this.name = name;
        this.childrenId = childrenId;
        this.begin = begin;
        this.duration = duration;
        this.type = type;
    }
}
