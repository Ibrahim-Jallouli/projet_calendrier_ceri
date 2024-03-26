package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.Seance;
import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Line;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class WeeklyController implements Initializable {

    @FXML
    private GridPane scheduleGrid;

    @FXML private Label currentWeekLabel;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;

    private SeanceService seanceService = new SeanceService(); // Assuming SeanceService is properly implemented


    private LocalDate currentWeekStart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Initialise la semaine courante à la semaine contenant le jour actuel
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        updateScheduleAndLabel();
        //initializeWeeklyScheduleGrid();
    }

    private void initializeWeeklyScheduleGrid() {
        addDayHeaders();
        addHourLabels();


    }


    @FXML
    private void handlePreviousWeek() {
        // Déplacer la semaine courante une semaine en arrière
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateScheduleAndLabel();
    }

    @FXML
    private void handleNextWeek() {
        // Déplacer la semaine courante une semaine en avant
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateScheduleAndLabel();
    }

    private void updateScheduleAndLabel() {
        scheduleGrid.getChildren().clear(); // Clear the grid for new entries
        addDayHeaders();
        addHourLabels();
        LocalDate weekEnd = currentWeekStart.plusDays(4); // Assuming a 5-day week
        List<Seance> weeklySeances = seanceService.getSeancesForWeek(currentWeekStart, weekEnd);
        for (Seance seance : weeklySeances) {
            displaySeanceInGrid(seance);
        }
    }


    private void addDayHeaders() {
        LocalDate date = currentWeekStart;
        for (int i = 0; i < 5; i++) { // Supposons une semaine de 5 jours : Lundi à Vendredi
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            String formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM"));
            Label dayHeader = new Label(dayName + " " + formattedDate);
            dayHeader.getStyleClass().add("day-header");
            dayHeader.setMaxWidth(Double.MAX_VALUE); // Permet au texte de s'étendre sur toute la largeur disponible
            dayHeader.setMaxHeight(Double.MAX_VALUE);
            scheduleGrid.add(dayHeader, i + 1, 0);
            date = date.plusDays(1);

        }
    }

    private void addHourLabels() {
        int rowIndex = 1; // La première ligne est pour les en-têtes de jours

        for (int hour = 8; hour <= 19; hour++) { // De 8h à 19h
            if (hour == 19) {
                String time = String.format("%02d:00", hour); // Format HH:mm
                Label hourLabel = new Label(time);
                hourLabel.getStyleClass().add("hour-label");
                scheduleGrid.add(hourLabel, 0, rowIndex);
            } else {
                for (int minute = 0; minute < 60; minute += 30) { // Toutes les 30 minutes
                    String time = String.format("%02d:%02d", hour, minute); // Format HH:mm
                    Label hourLabel = new Label(time);
                    hourLabel.getStyleClass().add("hour-label");
                    scheduleGrid.add(hourLabel, 0, rowIndex);

                    Pane halfHourSeparator = new Pane();
                    halfHourSeparator.getStyleClass().add("half-hour-separator");
                    scheduleGrid.add(halfHourSeparator, 0, rowIndex + 1, scheduleGrid.getColumnCount(), 1);
                    rowIndex += 2; // Incrémente de 2 pour les heures et les séparateurs
                }
            }
        }
    }


  /*  private void addCurrentTimeMarker() {
        // Obtenez l'heure actuelle
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
        int currentMinute = currentTime.getMinute();

        // Calculez l'index de la ligne correspondant à l'heure actuelle dans votre grille
        int rowIndex = (currentHour - 8) * 2; // Supposons que votre grille commence à 8h

        if (currentMinute >= 30) {
            rowIndex++; // Si l'heure actuelle est après 30 minutes, ajoutez 1 pour la demi-heure suivante
        }

        // Ajoutez une ligne rouge ou un autre élément visuel à la cellule correspondante
        Line currentTimeLine = new Line();
        currentTimeLine.getStyleClass().add("current-time-marker"); // Ajoutez une classe CSS pour styler la ligne
        scheduleGrid.add(currentTimeLine, /* index de la colonne , rowIndex);
    }*/

    private void displaySeanceInGrid(Seance seance) {
        Timestamp startTimestamp = Timestamp.valueOf(seance.getDtStart().toString());
        Timestamp endTimestamp = Timestamp.valueOf(seance.getDtEnd().toString());

        LocalTime startTime = startTimestamp.toLocalDateTime().toLocalTime();
        LocalTime endTime = endTimestamp.toLocalDateTime().toLocalTime();

        int startRow = timeSlotToGridRow(startTime);
        int durationInSlots = timeSlotToGridRow(endTime) - startRow;

        if (durationInSlots <= 0) {
            durationInSlots = 1; // Assure au minimum une durée d'un créneau
        }

        int dayOfWeek = startTimestamp.toLocalDateTime().getDayOfWeek().getValue() - 1;

        Pane seancePane = new Pane();
        seancePane.getStyleClass().add("seance-pane"); // Classe CSS pour le pane
        scheduleGrid.add(seancePane, dayOfWeek + 1, startRow, 1, durationInSlots);

        Label seanceLabel = new Label(seance.getMatiere().getNom() + " - " + seance.getSalle().getNom());
        seanceLabel.getStyleClass().add("seance-label");
        seancePane.getChildren().add(seanceLabel);
        // Positionnez votre label dans le Pane si nécessaire
    }

    private int timeSlotToGridRow(LocalTime time) {
        // Base time is 8:00, with each row representing a 30-minute slot
        LocalTime baseTime = LocalTime.of(8, 0);
        long minutesBetween = Duration.between(baseTime, time).toMinutes();
        // Convert minutes to row numbers, where each row represents 30 minutes
        return (int) (minutesBetween / 30) + 1; // +1 pour commencer à la ligne correcte si les en-têtes prennent la première ligne
    }



}
