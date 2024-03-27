package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class WeeklyController implements Initializable {

    @FXML
    private GridPane scheduleGrid;

    private LocalDate currentWeekStart;
    private SeanceService seanceService = new SeanceService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize currentWeekStart to the start of the current week
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateScheduleAndLabel();
    }

    // Extract day header setup into its own method
    private void addDayHeaders() {
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MM", Locale.FRENCH);
        LocalDate weekDate = currentWeekStart;

        for (int i = 0; i < daysOfWeek.length; i++) {
            String dayDate = daysOfWeek[i] + " " + weekDate.format(dateFormatter);
            Label dayLabel = new Label(dayDate);
            dayLabel.getStyleClass().add("day-header");
            GridPane.setHalignment(dayLabel, HPos.CENTER);
            scheduleGrid.add(dayLabel, i + 1, 0);
            weekDate = weekDate.plusDays(1);

            // Add separators between days
            if (i < daysOfWeek.length && i!=0) {
                Separator separator = new Separator();
                separator.setOrientation(Orientation.VERTICAL);
                separator.setPrefHeight(100);
                separator.setPadding(new Insets(5, 0, 0, -7)); // Example padding, adjust as needed
                scheduleGrid.add(separator, i + 1, 1, 1, GridPane.REMAINING);
            }
        }
    }


    // Extract hour label setup into its own method
    private void addHourLabels() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(19, 0);
        int row = 1;
        while (startTime.isBefore(endTime.plusSeconds(1))) {
            Label timeLabel = new Label(startTime.toString());
            timeLabel.getStyleClass().add("hour-label");
            GridPane.setHalignment(timeLabel, HPos.RIGHT);
            scheduleGrid.add(timeLabel, 0, row);
            startTime = startTime.plusMinutes(30);
            row++;
        }
    }

    private void updateScheduleAndLabel() {
        // Clear the grid for new entries
        scheduleGrid.getChildren().clear();
        addHourLabels();
        addDayHeaders();
        LocalDate weekEnd = currentWeekStart.plusDays(4); // Assuming a week from Monday to Friday
        User currentUser = UserSession.getInstance().getCurrentUser();
        List<Seance> seances;
        if (currentUser instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) currentUser;
            seances = seanceService.getSeancesForWeek(currentWeekStart, weekEnd, etudiant.getFormationId());

            for (int i = 1; i < scheduleGrid.getRowCount(); i++) {
                for (int j = 1; j < scheduleGrid.getColumnCount(); j++) {
                    Region cell = new Region();
                    // Determine the color based on the row index
                    String color = (i % 2 == 0) ? "white" : "#f0f5f5";
                    cell.setStyle("-fx-background-color: " + color + ";");
                    scheduleGrid.add(cell, j, i);
                }
            }

            for (Seance seance : seances) {
                ZonedDateTime startZdt = seance.getDtStart().toInstant().atZone(ZoneId.systemDefault());
                ZonedDateTime endZdt = seance.getDtEnd().toInstant().atZone(ZoneId.systemDefault());

                // Adjustments for daylight saving, if necessary
                if (startZdt.toLocalDate().isAfter(LocalDate.of(startZdt.getYear(), 4, 1))) {
                    startZdt = startZdt.plusHours(1);
                    endZdt = endZdt.plusHours(1);
                }

                LocalDate dateOfSeance = startZdt.toLocalDate();
                LocalTime timeOfSeance = startZdt.toLocalTime();

                DayOfWeek dayOfWeek = dateOfSeance.getDayOfWeek();
                int column = dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue() + 1;

                // Calculate the row based on the start time
                int row = calculateRowForTime(timeOfSeance);
                int startRow = calculateRowForTime(startZdt.toLocalTime());
                int endRow = calculateRowForTime(endZdt.toLocalTime());
                int durationInSlots = endRow - startRow;

                // Constructing the structured visual representation
                VBox seanceDetails = new VBox(2); // 2 is the spacing between elements

                Label matiereLabel = new Label("Matière: " + seance.getMatiere().getNom());
                Label enseignantLabel = new Label("Enseignant: " + seance.getEnseignant().getUsername());
                Label salleLabel = new Label("Salle: " + seance.getSalle().getNom());
                seanceDetails.getChildren().addAll(matiereLabel, enseignantLabel, salleLabel);
                Label seanceLabel = new Label(seance.getType().getNom());
                seanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");

                Pane seancePane = new Pane();
                seancePane.getChildren().add(seanceLabel);
                seancePane.getStyleClass().add("seance-pane");
                double leftPadding = 5; // Adjust this value as needed
                seanceLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                    double heightDifference = seancePane.getHeight() - newBounds.getHeight();
                    seanceLabel.setLayoutX(leftPadding); // Add left padding
                    seanceLabel.setLayoutY(heightDifference);
                });

                seanceDetails.layoutXProperty().bind(seancePane.widthProperty().subtract(seanceDetails.widthProperty()).divide(2));
                seanceDetails.layoutYProperty().bind(seancePane.heightProperty().subtract(seanceDetails.heightProperty()).divide(2));
                seancePane.getChildren().add(seanceDetails);


                // Add the seancePane to the grid, accounting for duration
                scheduleGrid.add(seancePane, column, startRow, 1, durationInSlots + 1);

                seanceDetails.getStyleClass().add("seance-details");
                matiereLabel.getStyleClass().add("seance-label");
                enseignantLabel.getStyleClass().add("seance-label");
                salleLabel.getStyleClass().add("seance-label");
                seancePane.getStyleClass().add("seance-pane");


                // Add separator lines between days, if necessary

            }
        }
    }

    private int calculateRowForTime(LocalTime startTime) {
        long minutesFromStartOfDay = ChronoUnit.MINUTES.between(LocalTime.of(8, 0), startTime);
        int row = 1 + (int) (minutesFromStartOfDay / 30);
        return row;
    }



    @FXML
    private void handlePreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateScheduleAndLabel();
    }

    @FXML
    private void handleNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateScheduleAndLabel();
    }
}

