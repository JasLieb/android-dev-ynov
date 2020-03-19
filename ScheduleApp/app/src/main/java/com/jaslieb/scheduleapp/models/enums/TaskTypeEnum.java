package com.jaslieb.scheduleapp.models.enums;

public enum TaskTypeEnum {
    EVERYDAY_LIFE("about everyday life", 0),
    SCHOOL("about school", 1),
    OUTSIDE_SCHOOL("about outside school", 2),
    ALARM("for awakening", 3),
    PUNCTUAL("punctual or special", 4);

    public int position;
    private String label;

    TaskTypeEnum(String label, int position) {
        this.position = position;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("Something %s", label);
    }

    public static TaskTypeEnum find(long position) {
        for(TaskTypeEnum type: TaskTypeEnum.values()) {
            if(type.position == position){
                return type;
            }
        }
        throw new Error();
    }
}
