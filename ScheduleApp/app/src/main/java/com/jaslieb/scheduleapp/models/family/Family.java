package com.jaslieb.scheduleapp.models.family;

import java.util.ArrayList;

public class Family {
    String name;
    ArrayList<Parent> parents;
    ArrayList<Child> children;

    public Family() {}

    public Family(String name, ArrayList<Parent> parents, ArrayList<Child> children) {
        this.name = name;
        this.parents = parents;
        this.children = children;
    }
}
