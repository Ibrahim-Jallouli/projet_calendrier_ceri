package com.example.m1prototypage.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {


    @FXML
    private GridPane scheduleGrid;

    @FXML
    private ComboBox<String> filterTypeComboBox;

    @FXML
    private ComboBox<String> filterValueComboBox;
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


    // Méthode appelée lorsqu'un type de filtre est sélectionné
    @FXML
    private void handleFilterTypeSelection() {
        // Récupérer le type sélectionné
        String selectedType = filterTypeComboBox.getValue();

        // Afficher la ComboBox des valeurs de filtre et charger les valeurs correspondantes
        if (selectedType != null) {
            // Afficher la ComboBox des valeurs de filtre
            filterValueComboBox.setVisible(true);
            // Charger les valeurs correspondantes au type sélectionné
            loadFilterValues(selectedType);
        } else {
            // Cacher la ComboBox des valeurs de filtre si aucun type n'est sélectionné
            filterValueComboBox.setVisible(false);
        }
    }

    private void loadFilterValues(String selectedType) {
        // Charger les valeurs de filtre selon le type sélectionné
        // Implémentez cette méthode pour charger les valeurs en fonction du type sélectionné
        // Ensuite, vous ajoutez ces valeurs à la ComboBox des valeurs de filtre
    }

    // Méthode appelée lorsqu'une valeur de filtre est sélectionnée
    public void handleFilterValueChange() {
        // Appliquez le filtre en fonction de la valeur sélectionnée
        String selectedFilterValue = filterValueComboBox.getValue();
        // Implémentez la logique pour appliquer le filtre
    }
}
