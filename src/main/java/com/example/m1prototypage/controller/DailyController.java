package com.example.m1prototypage.controller;
import com.example.m1prototypage.entities.Enseignant;
import com.example.m1prototypage.entities.Seance;
import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.entities.UserSession;
import com.example.m1prototypage.services.EnseignantService;
import com.example.m1prototypage.services.FormationService;
import com.example.m1prototypage.services.SalleService;
import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class DailyController implements Initializable, CalendarViewController {
    private Map<String, String> currentFilterCriteria = new HashMap<>();
    private Map<String, String> currentSearchCriteria = new HashMap<>();
    private User currentUser = UserSession.getInstance().getCurrentUser();

    @FXML
    private GridPane scheduleGrid;
    @FXML
    private Button addSeanceButton;

    private LocalDate currentDay;
    private SeanceService seanceService = new SeanceService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser instanceof Enseignant) {
            addSeanceButton.setDisable(false); // Enable the button if user is an instance of Enseignant
        } else {
            addSeanceButton.setDisable(true); // Otherwise, disable it
        }
        currentDay = LocalDate.now(); // Start with the current day
        updateScheduleAndLabel();
    }

    @Override
    public void updateViewWithCriteria(Map<String, String> filterCriteria, Map<String, String> searchCriteria) {
        currentFilterCriteria = filterCriteria;
        currentSearchCriteria = searchCriteria;
        updateScheduleAndLabel();
    }

    private void addDayHeader() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH);
        String dayDate = currentDay.format(dateFormatter);
        Label dayLabel = new Label(dayDate);
        dayLabel.getStyleClass().add("day-header");
        GridPane.setHalignment(dayLabel, HPos.CENTER);
        scheduleGrid.add(dayLabel, 1, 0); // Adjusted for daily view
    }

    private void addHourLabels() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(19, 0);
        int row = 1; // Start from the second row after the header
        while (startTime.isBefore(endTime.plusSeconds(1))) {
            Label timeLabel = new Label(startTime.toString());
            timeLabel.getStyleClass().add("hour-label");
            GridPane.setHalignment(timeLabel, HPos.RIGHT);
            scheduleGrid.add(timeLabel, 0, row++);
            startTime = startTime.plusMinutes(30);
        }
    }

    private void updateScheduleAndLabel() {
        scheduleGrid.getChildren().clear();
        addDayHeader();
        addHourLabels();
        LocalDate dayEnd = currentDay; // For daily view, the start and end are the same
        User currentUser = UserSession.getInstance().getCurrentUser();
        List<Seance> seances = new ArrayList<>();

        // Apply search criteria
        if (currentSearchCriteria.containsKey("Enseignant")) {
            EnseignantService enseignantService = new EnseignantService();
            String enseignant = currentSearchCriteria.get("Enseignant");
            String enseignantId = enseignantService.getEnseignantIdByName(enseignant).toString();
            seances = seanceService.getSeancesByCriteriaEnseignant(currentDay, dayEnd, enseignantId);
        } else if (currentSearchCriteria.containsKey("Salle")) {
            SalleService salleService = new SalleService();
            String salle = currentSearchCriteria.get("Salle");
            String salleId = salleService.getSalleIdByName(salle).toString();
            seances = seanceService.getSeancesByCriteriaSalle(currentDay, dayEnd, salleId);
        } else if (currentSearchCriteria.containsKey("Formation")) {
            FormationService formationService = new FormationService();
            String formation = currentSearchCriteria.get("Formation");
            String formationId = formationService.getFormationIdByName(formation).toString();
            seances = seanceService.getSeancesByCriteriaFormation(currentDay, dayEnd, formationId);
        } else {
            seances = seanceService.getSeancesIntervalFrom(currentDay,currentDay, currentUser);
        }

        // Apply filter criteria
        if (currentFilterCriteria.containsKey("Type")) {
            String typeFilter = currentFilterCriteria.get("Type");
            seances = seances.stream()
                    .filter(seance -> seance.getType().getNom().equals(typeFilter))
                    .collect(Collectors.toList());
        }

        if (currentFilterCriteria.containsKey("Salle")) {
            String salleFilter = currentFilterCriteria.get("Salle");
            seances = seances.stream()
                    .filter(seance -> seance.getSalle().getNom().equals(salleFilter))
                    .collect(Collectors.toList());
        }

        if (currentFilterCriteria.containsKey("Matière")) {
            String matiereFilter = currentFilterCriteria.get("Matière");
            seances = seances.stream()
                    .filter(seance -> seance.getMatiere().getNom().equals(matiereFilter))
                    .collect(Collectors.toList());
        }

        for (int i = 1; i < scheduleGrid.getRowCount(); i++) {
            for (int j = 1; j < scheduleGrid.getColumnCount(); j++) {
                Region cell = new Region();
                String color = (i % 2 == 0) ? "white" : "#f0f5f5"; // Adjust colors as needed
                cell.setStyle("-fx-background-color: " + color + ";");
                scheduleGrid.add(cell, j, i);
                GridPane.setValignment(cell, VPos.TOP); // Ensure alignment matches seancePane
            }
        }
        // Populate the grid
        for (Seance seance : seances) {
            addSeanceToGrid(seance);
        }

        // Optionally, add cell shading or other visual adjustments as needed
    }
    private void addSeanceToGrid(Seance seance) {
        ZonedDateTime startZdt = seance.getDtStart().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime endZdt = seance.getDtEnd().toInstant().atZone(ZoneId.systemDefault());

        // Adjust for daylight saving time from April to October
        if (startZdt.getMonth().getValue() >= Month.APRIL.getValue() && startZdt.getMonth().getValue() <= Month.OCTOBER.getValue()) {
            startZdt = startZdt.plusHours(1);
        }

        if (endZdt.getMonth().getValue() >= Month.APRIL.getValue() && endZdt.getMonth().getValue() <= Month.OCTOBER.getValue()) {
            endZdt = endZdt.plusHours(1);
        }

        int startRow = calculateRowForTime(startZdt.toLocalTime());
        int endRow = calculateRowForTime(endZdt.toLocalTime());
        int durationInSlots = endRow - startRow;

        VBox seanceDetails = constructSeanceDetailsPane(seance);
        StackPane seancePane = new StackPane();
        seancePane.getStyleClass().add("seance-pane");

        // Highlight the current hour cell, if applicable
        highlightCurrentHourCell();

        // Add seance details to the pane
        Label typeLabel = new Label(seance.getType().getNom());
        typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");
        StackPane.setAlignment(typeLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(typeLabel, new Insets(0, 5, 5, 0)); // Adjust padding for type label
        seancePane.getChildren().addAll(seanceDetails, typeLabel);

        StackPane.setAlignment(seanceDetails, Pos.TOP_LEFT);
        StackPane.setMargin(seanceDetails, new Insets(5));

        // Since it's a daily view, we use a fixed column index for adding seancePane to the schedule grid
        scheduleGrid.add(seancePane, 1, startRow, 1, durationInSlots);
        GridPane.setValignment(seancePane, VPos.TOP);
    }

    private void highlightCurrentHourCell() {
        LocalTime now = LocalTime.now();

        int highlightColumnIndex = 1; // Adjust based on your actual layout

        // Ensure we're within the grid's time range
        if (!(now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(19, 0)))) {
            int currentRow = calculateRowForTime(now);
            if (currentRow >= 0) {
                Region hourCell = new Region();
                hourCell.setStyle("-fx-background-color: #eedbbf;"); // Highlight color
                scheduleGrid.add(hourCell, highlightColumnIndex, currentRow);
                GridPane.setValignment(hourCell, VPos.TOP);
                GridPane.setFillWidth(hourCell, true);
                GridPane.setFillHeight(hourCell, true);
            }
        }
    }



    private VBox constructSeanceDetailsPane(Seance seance) {
        VBox seanceDetails = new VBox(2); // Consistent spacing
        TextField matiereField = new TextField("Matière: " + seance.getMatiere().getNom());
        matiereField.setEditable(false);
        matiereField.setBorder(null);
        matiereField.setBackground(Background.EMPTY);
        matiereField.getStyleClass().add("seance-label");

        // Create a Hyperlink for the enseignant
        Hyperlink enseignantLink = new Hyperlink("Enseignant: " + seance.getEnseignant().getUsername());
        enseignantLink.setOnAction(event -> {
            String email = seance.getEnseignant().getMail(); // Make sure to use getEmail() or the correct method to fetch email
            if (email != null && !email.isEmpty()) {
                try {
                    String sanitizedEmail = email.replace(" ", "_");
                    Desktop.getDesktop().mail(new URI("mailto:" + sanitizedEmail));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        TextField salleField = new TextField("Salle: " + seance.getSalle().getNom());
        salleField.setEditable(false);
        salleField.setBorder(null);
        salleField.setBackground(Background.EMPTY);
        salleField.getStyleClass().add("seance-label");

        seanceDetails.getChildren().addAll(matiereField, enseignantLink, salleField);
        seanceDetails.getStyleClass().add("seance-details");
        return seanceDetails;
    }

    private int calculateRowForTime(LocalTime time) {
        long minutesFromStartOfDay = ChronoUnit.MINUTES.between(LocalTime.of(8, 0), time);
        return 1 + (int) (minutesFromStartOfDay / 30); // Each row represents 30 minutes
    }

    @FXML
    private void handlePreviousDay() {
        currentDay = currentDay.minusDays(1);
        updateScheduleAndLabel();
    }

    @FXML
    private void handleNextDay() {
        currentDay = currentDay.plusDays(1);
        updateScheduleAndLabel();
    }

    @FXML
    private void ouvrirFormulaireAjout() {
        // Your implementation for opening a form to add seances
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/your/AddSeanceForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une séance");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



