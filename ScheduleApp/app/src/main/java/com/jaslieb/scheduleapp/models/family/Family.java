package com.jaslieb.scheduleapp.models.family;

import java.util.ArrayList;

public class Family {
    String name;
    ArrayList<Parent> parents;
    ArrayList<Child> childs;

    public Family() {}

    public Family(String name, ArrayList<Parent> parents, ArrayList<Child> childs) {
        this.name = name;
        this.parents = parents;
        this.childs = childs;
    }
}
