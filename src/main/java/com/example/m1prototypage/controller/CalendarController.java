package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.Matiere;
import com.example.m1prototypage.entities.Salle;
import com.example.m1prototypage.entities.TYPE;
import com.example.m1prototypage.services.MatiereService;
import com.example.m1prototypage.services.SalleSevice;
import com.example.m1prototypage.services.TypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CalendarController implements Initializable {


    @FXML
    private GridPane scheduleGrid;

    @FXML
    private StackPane scheduleContainer; // Conteneur pour les vues de l'emploi du temps

    @FXML
    private ComboBox<String> filterTimeBox; // Choix de la vue (Semaine, Mois, Jour)

    @FXML
    private ComboBox<String> filterTypeComboBox;

    @FXML
    private ComboBox<String> filterValueComboBox;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setupFilterComboBox();
        loadScheduleView("weekly-view.fxml");
        configureFilterTypeComboBox();
    }

    private void configureFilterTypeComboBox() {
        filterValueComboBox.setEditable(true);
        filterTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterValueComboBox.setVisible(newValue != null);
            if (newValue != null) {
                filterValueComboBox.setVisible(true);
                loadFilterValues(newValue); // Charge les valeurs basées sur le type sélectionné
            } else {
                filterValueComboBox.setVisible(false);
            }

        });
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
            filterTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println(newVal);
                loadFilterValues(newVal);
            });
            //loadFilterValues(selectedType);
        } else {
            // Cacher la ComboBox des valeurs de filtre si aucun type n'est sélectionné
            filterValueComboBox.setVisible(false);
        }
    }

  private void loadFilterValues(String selectedType) {
      MatiereService matiereService = new MatiereService();
      TypeService typeService = new TypeService();
      SalleSevice salleSevice = new SalleSevice();

      ObservableList<String> values = FXCollections.observableArrayList();
      switch (selectedType) {
          case "Matière":
              matiereService.getAllMatieres().forEach(matiere -> values.add(matiere.getNom()));
              break;
          case "Type":
              typeService.getAllTypes().forEach(type -> values.add(type.getNom()));
              break;
          case "Salle":
              salleSevice.getAllSalles().forEach(salle -> values.add(salle.getNom()));
              break;
      }
      System.out.println(values);
     // setupFilterValueComboBoxListener(values); // Ajouté pour gérer le filtrage basé sur la saisie
      filterValueComboBox.setItems(values); // Remplacez les éléments existants par les nouveaux
      filterValueComboBox.setEditable(true); // Rend la ComboBox éditable
      setupFilterValueComboBoxListener(values); // Configure le filtrage basé sur la saisie

  }

    private void setupFilterValueComboBoxListener(ObservableList<String> originalItems) {
        // Crée une FilteredList à partir de 'originalItems'
        FilteredList<String> filteredItems = new FilteredList<>(originalItems, p -> true);

        // Réagit à la saisie de l'utilisateur pour filtrer les éléments
        filterValueComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final String currentText = newValue.toLowerCase();

            filteredItems.setPredicate(item -> item.toLowerCase().contains(currentText));
        });

        // Liez la FilteredList filtrée à la ComboBox pour afficher les éléments filtrés
        filterValueComboBox.setItems(filteredItems);
    }

    public void handleFilterValueChange() {
        // Récupère la valeur sélectionnée dans la ComboBox des valeurs de filtre
        String selectedFilterValue = filterValueComboBox.getValue();

        if (selectedFilterValue != null && !selectedFilterValue.isEmpty()) {
            // Déterminez le type de filtre sélectionné pour savoir comment filtrer les données
            String selectedFilterType = filterTypeComboBox.getValue();

            switch (selectedFilterType) {
                case "Matière":
                    // Appliquez le filtre pour l'emploi du temps basé sur la matière sélectionnée
                    filterSeancesByCriteria("Matière", selectedFilterValue);
                    break;
                case "Type":
                    // Appliquez le filtre pour l'emploi du temps basé sur le type de séance sélectionné
                    filterSeancesByCriteria("Type", selectedFilterValue);
                    break;
                case "Salle":
                    // Appliquez le filtre pour l'emploi du temps basé sur la salle sélectionnée
                    filterSeancesByCriteria("Salle", selectedFilterValue);
                    break;
                // Ajoutez d'autres cas si nécessaire
            }
        }
    }

    private void filterSeancesByCriteria(String criteria, String value){

    }

    private void setupFilterComboBox() {
        filterTimeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Semaine":
                    loadScheduleView("weekly-view.fxml");
                    break;
                case "Mois":
                    loadScheduleView("monthly-view.fxml");
                    break;
                case "Jour":
                    loadScheduleView("daily-view.fxml");
                    break;
                default:
                    break;
            }
        });
    }

    private void loadScheduleView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/m1prototypage/GUI/" + fxmlFile));
            Parent view = loader.load();
            scheduleContainer.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
