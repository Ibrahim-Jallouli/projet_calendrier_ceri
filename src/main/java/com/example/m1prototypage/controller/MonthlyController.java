package com.example.m1prototypage.controller;

import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MonthlyController implements Initializable, CalendarViewController {
    @FXML
    private GridPane monthGrid;
    private LocalDate currentMonthStart;
    private SeanceService seanceService = new SeanceService();
    @FXML
    private Label currentMonthLabel;

    @Override
    public void updateViewWithCriteria(Map<String, String> filterCriteria, Map<String, String> searchCriteria) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentMonthStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        populateMonthView();
    }

    private void updateMonthLabel(LocalDate date) {
        String monthName = date.format(DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH)).toUpperCase();
        currentMonthLabel.setText(monthName);
    }

    private void addDayHeaders() {
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.getStyleClass().add("day-header");
            GridPane.setHalignment(dayLabel, HPos.CENTER);
            monthGrid.add(dayLabel, i, 0); // In the monthly grid, we start at column 0 for Monday
        }
    }


    private void populateMonthView() {
        monthGrid.getChildren().clear(); // Clear the grid before populating it again
        addDayHeaders();
        updateMonthLabel(currentMonthStart);
        // Start with the first day of the week of the month's start
        LocalDate calendarDate = currentMonthStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthEnd = currentMonthStart.with(TemporalAdjusters.lastDayOfMonth());

        for (int week = 1; week < 6; week++) {
            for (int day = 0; day < 5; day++) {
                int sessionCount = seanceService.countSessionsOnDate(calendarDate);
                Label dayLabel = new Label(calendarDate.getDayOfMonth() + "\n" + sessionCount + " séance(s)");
                dayLabel.getStyleClass().add("day-cell");
                GridPane.setHalignment(dayLabel, HPos.CENTER);
                monthGrid.add(dayLabel, day, week);
                calendarDate = calendarDate.plusDays(1);
                if (calendarDate.isAfter(monthEnd)) break; // Stop if we've reached the end of the month
            }
        }
    }

    @FXML
    private void handlePreviousMonth() {
        currentMonthStart = currentMonthStart.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        populateMonthView();
    }

    @FXML
    private void handleNextMonth() {
        currentMonthStart = currentMonthStart.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        populateMonthView();
    }


}
