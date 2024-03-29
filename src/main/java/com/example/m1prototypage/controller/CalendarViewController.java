package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.User;

import java.util.Map;

public interface CalendarViewController {
    void updateViewWithCriteria(Map<String, String> filterCriteria, Map<String, String> searchCriteria);
}
