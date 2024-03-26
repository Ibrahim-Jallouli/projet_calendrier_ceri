package com.example.m1prototypage.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.ResourceBundle;

public class WeeklyController implements Initializable {

    @FXML
    private GridPane scheduleGrid;

  //  @FXML private Label currentWeekLabel;
  //  @FXML private Button previousWeekButton;
   // @FXML private Button nextWeekButton;

    private LocalDate currentWeekStart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Initialise la semaine courante à la semaine contenant le jour actuel
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        updateScheduleAndLabel();
        initializeWeeklyScheduleGrid();
    }

    private void initializeWeeklyScheduleGrid() {
        addDayHeaders();
        addHourLabels();

    }


    @FXML
    private void handlePreviousWeek() {
        // Déplacez la semaine courante une semaine en arrière
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateScheduleAndLabel();
    }

    @FXML
    private void handleNextWeek() {
        // Déplacez la semaine courante une semaine en avant
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateScheduleAndLabel();
    }

    private void updateScheduleAndLabel() {
        // Mettez à jour le label pour afficher la semaine courante
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        String formattedDate = currentWeekStart.format(formatter);
       // currentWeekLabel.setText("Semaine du " + formattedDate);

        // Mettez à jour l'affichage de l'emploi du temps ici, en fonction de currentWeekStart
        // Cela pourrait impliquer de filtrer votre ensemble de données pour la semaine courante et de reconstruire l'affichage dans le GridPane.
    }

   /* private void addDayHeaders() {
        LocalDate date = currentWeekStart;
        for (int i = 0; i < 5; i++) { // Supposons une semaine de 5 jours : Lundi à Vendredi
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Label label = new Label(dayName + "\n" + date.format(DateTimeFormatter.ofPattern("d MMM")));
            label.getStyleClass().add("day-header");
            scheduleGrid.add(label, i + 1, 0);
            date = date.plusDays(1);
        }
    }

    private void addHourLabels() {
        int rowIndex = 1; // Commence à 1 pour prendre en compte l'en-tête de jour déjà ajouté
        for (int hour = 8; hour <= 19; hour++) {
            // Ajoute l'étiquette de l'heure
            Label label = new Label(hour + ":00");
            label.getStyleClass().add("hour-label");
            scheduleGrid.add(label, 0, rowIndex);

            rowIndex++; // Incrémente l'index de ligne pour le séparateur

            // Ajoute un séparateur pour la demi-heure
            Pane halfHourSeparator = new Pane();
            halfHourSeparator.getStyleClass().add("half-hour-separator");
            scheduleGrid.add(halfHourSeparator, 0, rowIndex, scheduleGrid.getColumnCount(), 1); // Span sur toutes les colonnes

            rowIndex++; // Prépare l'index pour la prochaine heure
        }
    }*/

    private void addDayHeaders() {
        LocalDate date = currentWeekStart;
        for (int i = 0; i < 5; i++) { // Supposons une semaine de 5 jours : Lundi à Vendredi
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            String formattedDate = date.format(DateTimeFormatter.ofPattern("d MMM"));
            Label dayHeader = new Label(dayName + "\n" + formattedDate);
            dayHeader.getStyleClass().add("day-header");
            scheduleGrid.add(dayHeader, i + 1, 0);
            date = date.plusDays(1);
        }
    }

    private void addHourLabels() {
        int rowIndex = 2; // Ligne 1 est pour les en-têtes de jours
        for (int hour = 8; hour <= 19; hour++) {
            Label hourLabel = new Label(hour + ":00");
            hourLabel.getStyleClass().add("hour-label");
            scheduleGrid.add(hourLabel, 0, rowIndex);

            Pane halfHourSeparator = new Pane();
            halfHourSeparator.getStyleClass().add("half-hour-separator");
            scheduleGrid.add(halfHourSeparator, 0, rowIndex + 1, scheduleGrid.getColumnCount(), 1);
            rowIndex += 2; // Incrémente de 2 pour les heures et les séparateurs
        }
    }



}
