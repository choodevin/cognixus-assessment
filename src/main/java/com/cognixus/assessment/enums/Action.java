package com.cognixus.assessment.enums;

public enum Action {
    DONE("done"),
    UNDONE("undone"),
    DELETE("delete");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
