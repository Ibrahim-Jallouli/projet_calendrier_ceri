package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.User;

import java.util.Map;

public interface CalendarViewController {
    void updateViewWithFilters(Map<String, String> filterCriteria, User currentUser);
}
