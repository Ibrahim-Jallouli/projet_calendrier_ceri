package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.entities.UserSession;
import com.example.m1prototypage.services.MatiereService;
import com.example.m1prototypage.services.SalleService;
import com.example.m1prototypage.services.TypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
        //filterValueComboBox.setEditable(true);
    }


    public void handleFilterValueChange() {
        String selectedFilterValue = filterValueComboBox.getValue();
        if (selectedFilterValue != null && !selectedFilterValue.isEmpty()) {
            loadScheduleView(currentViewFxml);
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

            // Check if the loaded view is the Weekly View
            if ("weekly-view.fxml".equals(fxmlFile)) {
                Object controller = loader.getController();
                if (controller instanceof WeeklyController) {
                    CalendarViewController weeklyController = (CalendarViewController) controller;

                    Map<String, String> filterCriteria = constructFilterCriteria();

                    weeklyController.updateViewWithFilters(filterCriteria, currentUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Map<String, String> constructFilterCriteria() {
        Map<String, String> filterCriteria = new HashMap<>();
        String filterType = filterTypeComboBox.getValue();
        String filterValueName = filterValueComboBox.getValue();

        if (filterType != null && filterValueName != null) {
            filterCriteria.put(filterType, filterValueName );
        }
        System.out.println("Filter Criteria: " + filterCriteria.toString());
        return filterCriteria;
    }

}
