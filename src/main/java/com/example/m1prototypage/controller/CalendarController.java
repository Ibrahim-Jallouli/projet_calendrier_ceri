package com.example.m1prototypage.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {


    @FXML
    private GridPane scheduleGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Exemple : ajout de quelques éléments à l'emploi du temps
        addEvent("Meeting", "Monday", "10:00", "12:00");
        addEvent("Lunch", "Tuesday", "12:00", "13:00");
        addEvent("Presentation", "Wednesday", "14:00", "16:00");
    }

    private void addEvent(String eventName, String day, String startTime, String endTime) {
        // Créer une nouvelle étiquette pour l'événement
        Label eventLabel = new Label(eventName);

        // Définir les contraintes de la rangée pour que la hauteur de chaque événement soit uniforme
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);

        // Ajouter l'événement à la grille avec les informations sur le jour et l'heure
        scheduleGrid.addRow(scheduleGrid.getRowCount(), new Label(day), new Label(startTime + " - " + endTime), eventLabel);

        // Appliquer les contraintes de la rangée à la nouvelle rangée
        scheduleGrid.getRowConstraints().add(rowConstraints);
    }
}
