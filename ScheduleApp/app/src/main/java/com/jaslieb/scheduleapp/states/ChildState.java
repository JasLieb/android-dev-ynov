package com.jaslieb.scheduleapp.states;

import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ChildState {
    public List<Task> tasks;
    public ChildState(List<Task> tasks) {
         this.tasks = tasks;
    }

    public static ChildState Default = new ChildState(new ArrayList<>());
}
