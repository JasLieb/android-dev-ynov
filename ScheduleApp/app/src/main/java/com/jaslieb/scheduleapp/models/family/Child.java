package com.jaslieb.scheduleapp.models.family;

import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.ArrayList;

public class Child {
    public String name;
    public String password;
    public ArrayList<Task> tasks;

    public Child() {}

    public Child(String name, String password, ArrayList<Task> tasks) {
        this.name = name;
        this.password = password;
        this.tasks = tasks;
    }
}
