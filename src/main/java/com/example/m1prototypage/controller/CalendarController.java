package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.entities.UserSession;
import com.example.m1prototypage.services.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CalendarController implements Initializable {

    @FXML
    private StackPane scheduleContainer;

    @FXML
    private ComboBox<String> filterTimeBox;

    @FXML
    private ComboBox<String> filterTypeComboBox;

    @FXML
    private ComboBox<String> filterValueComboBox;
    String currentViewFxml;

    @FXML
    private Label currentUserLabel;
    User currentUser;

    @FXML
    private Button toggleDarkModeButton;

    @FXML
    private ComboBox<String> searchTypeComboBox;

    @FXML
    private ComboBox<String> searchValueComboBox;

    @FXML
    private Button clearFilterButton;



    private boolean isDarkModeEnabled = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentViewFxml = "weekly-view.fxml";
        loadScheduleView(currentViewFxml);
        currentUser = UserSession.getInstance().getCurrentUser();
        filterValueComboBox.setVisible(false);
        configureFilterTypeComboBox();
        setupFilterComboBox();
        currentUserLabel.setText("User : " + currentUser.getUsername());
    }

    private void configureFilterTypeComboBox() {
        filterTypeComboBox.getItems().setAll("Matière", "Type", "Salle");
        filterTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterValueComboBox.setVisible(newValue != null);
            if (newValue != null) {
                filterValueComboBox.setVisible(true);
                loadFilterValues(newValue);
            } else {
                filterValueComboBox.setVisible(false);
            }
        });
    }

    private void loadFilterValues(String selectedType) {
        MatiereService matiereService = new MatiereService();
        TypeService typeService = new TypeService();
        SalleService salleService = new SalleService();

        ObservableList<String> values = FXCollections.observableArrayList();

        switch (selectedType) {
            case "Matière":
                matiereService.getAllMatieres().forEach(matiere -> {
                    values.add(matiere.getNom());
                });
                break;
            case "Type":
                typeService.getAllTypes().forEach(type -> {
                    values.add(type.getNom());
                });
                break;
            case "Salle":
                salleService.getAllSalles().forEach(salle -> {
                    values.add(salle.getNom());
                });
                break;
        }

        filterValueComboBox.setItems(values);
    }


    public void handleFilterValueChange() {
        String selectedFilterValue = filterValueComboBox.getValue();
        if (selectedFilterValue != null && !selectedFilterValue.isEmpty()) {
            loadScheduleView(currentViewFxml);
            clearFilterButton.setVisible(true);
        }
    }

    private void setupFilterComboBox() {
        filterTimeBox.getItems().setAll("Jour","Semaine", "Mois");
        filterTimeBox.setValue("Semaine");
        filterTimeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String previousViewFxml = currentViewFxml;
            switch (newValue) {
                case "Semaine":
                    currentViewFxml = "weekly-view.fxml";
                    break;
                case "Mois":
                    currentViewFxml = "monthly-view.fxml";
                    break;
                case "Jour":
                    currentViewFxml = "daily-view.fxml";
                    break;
                default:
                    return; // Early return if newValue is unexpected
            }
            if (!currentViewFxml.equals(previousViewFxml)) {
                loadScheduleView(currentViewFxml);
            }
        });
    }


    private void loadScheduleView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/m1prototypage/GUI/" + fxmlFile));
            Parent view = loader.load();
            scheduleContainer.getChildren().setAll(view);

            Object controller = loader.getController();
            if (controller instanceof CalendarViewController) {
                CalendarViewController viewController = (CalendarViewController) controller;

                // Assuming filterTypeComboBox and filterValueComboBox are for filter criteria
                Map<String, String> filterCriteria = constructCriteria(filterTypeComboBox, filterValueComboBox);
                // Assuming searchTypeComboBox and searchValueComboBox are for search criteria
                Map<String, String> searchCriteria = constructCriteria(searchTypeComboBox, searchValueComboBox);

                // Update the view with both sets of criteria
                viewController.updateViewWithCriteria(filterCriteria, searchCriteria);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> constructCriteria(ComboBox<String> typeComboBox, ComboBox<String> valueComboBox) {
        Map<String, String> criteria = new HashMap<>();
        String type = typeComboBox.getValue();
        String value = valueComboBox.getValue();
        if (type != null && value != null && !value.isEmpty()) {
            criteria.put(type, value);
        }
        return criteria;
    }

    @FXML
    private void handleSearch() {
        loadScheduleView(currentViewFxml);
    }


    @FXML
    private void updateSearchValues() {
        SalleService salleService = new SalleService();
        EnseignantService enseignantService = new EnseignantService();
        FormationService formationService = new FormationService();

        String selectedType = searchTypeComboBox.getValue();
        List<String> items = new ArrayList<>();

        switch (selectedType) {
            case "Formation":
                formationService.getAllFormations().forEach(formation -> items.add(formation.getNom()));
                break;
            case "Enseignant":
                enseignantService.getAllEnseignants().forEach(enseignant -> items.add(enseignant.getUsername()));
                break;
            case "Salle":
                salleService.getAllSalles().forEach(salle -> items.add(salle.getNom()));
                break;
        }

        ObservableList<String> observableItems = FXCollections.observableArrayList(items);
        FilteredList<String> filteredItems = new FilteredList<>(observableItems, p -> true);

        searchValueComboBox.setEditable(true);
        searchValueComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = searchValueComboBox.getEditor();
            final String selected = searchValueComboBox.getSelectionModel().getSelectedItem();

            // This needs run on the GUI thread to avoid timing issues
            Platform.runLater(() -> {
                // If the no item in the list is selected or the selected item isn't equal to the current input, we rebuild the list
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> {
                        // If filter text is empty, display all items.
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return item.toLowerCase().contains(lowerCaseFilter);
                    });
                }
            });
        });

        searchValueComboBox.setItems(filteredItems);
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
        filterTypeComboBox.setValue(null);
        filterValueComboBox.setValue(null);
        filterValueComboBox.setItems(FXCollections.observableArrayList());
        clearFilterButton.setVisible(false);
        loadScheduleView(currentViewFxml);
    }

    @FXML
    private void toggleDarkMode() {
        isDarkModeEnabled = !isDarkModeEnabled;
        applyTheme();
        updateToggleButtonLabel();
    }

    private void applyTheme() {
        Scene scene = toggleDarkModeButton.getScene();
        if (isDarkModeEnabled) {
            scene.getRoot().getStyleClass().add("dark-mode");
        } else {
            scene.getRoot().getStyleClass().remove("dark-mode");

        }
    }

    private void updateToggleButtonLabel() {

        String label = isDarkModeEnabled ? "Désactiver le mode sombre" : "Activer le mode sombre";
        toggleDarkModeButton.setText(label);

    }






}
