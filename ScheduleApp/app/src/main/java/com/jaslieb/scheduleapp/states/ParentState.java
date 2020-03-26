package com.jaslieb.scheduleapp.states;

import com.jaslieb.scheduleapp.models.Task;

import java.util.ArrayList;
import java.util.List;

public class ParentState {
    public List<Task> tasks;
    public ParentState(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static ParentState Default = new ParentState(new ArrayList<Task>());
}
