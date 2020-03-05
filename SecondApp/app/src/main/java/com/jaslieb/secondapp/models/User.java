package com.jaslieb.secondapp.models;

public class User {
    public String name;
    public Boolean isSelected;

    public User(String _name, Boolean _isSelected ) {
        name = _name;
        isSelected = _isSelected;
    }

    public void setIsSelected(Boolean value) {
        isSelected = value;
    }
}
