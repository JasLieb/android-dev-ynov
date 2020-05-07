package com.jaslieb.scheduleapp.models.family;

public class Parent {
    String name;
    String password;
    String familyId;

    public Parent() {}

    public Parent(String name, String password, String familyId) {
        this.name = name;
        this.password = password;
        this.familyId = familyId;
    }
}
