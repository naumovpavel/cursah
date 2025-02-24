package com.wiftwift.controller;

public class ExpenseRequest {
    private String name;
    private String value;

    // Конструкторы, геттеры и сеттеры
    public ExpenseRequest() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}

