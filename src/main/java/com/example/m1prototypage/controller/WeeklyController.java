package com.example.m1prototypage.controller;
import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.services.EnseignantService;
import com.example.m1prototypage.services.FormationService;
import com.example.m1prototypage.services.SalleService;
import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.awt.Desktop;


public class WeeklyController implements Initializable,CalendarViewController {
    private Map<String, String> currentFilterCriteria = new HashMap<>();
    private Map<String, String> currentSearchCriteria = new HashMap<>();
    private User currentUser = UserSession.getInstance().getCurrentUser();


    @Override
    public void updateViewWithCriteria(Map<String, String> filterCriteria, Map<String, String> searchCriteria) {
        currentFilterCriteria = filterCriteria;
        currentSearchCriteria = searchCriteria;
        updateScheduleAndLabel();
    }

    @FXML
    private GridPane scheduleGrid;

    @FXML
    private Button addSeanceButton;

    @FXML
    private Label currentWeekLabel;

    @FXML
    private Button previousWeekButton;

    @FXML
    private Button nextWeekButton;

    private LocalDate currentWeekStart;
    private SeanceService seanceService = new SeanceService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setMargin(previousWeekButton, new Insets(0, 0, 0, 105)); // Adding left margin

        if (currentUser instanceof Enseignant) {
            addSeanceButton.setDisable(false);
        } else {
            addSeanceButton.setDisable(true);
        }
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateScheduleAndLabel();
    }

    private void updateDayLabel() {
        LocalDate today = LocalDate.now(); // Get the current system date
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EE d / MM", Locale.FRENCH));
        currentWeekLabel.setText(formattedDate); // Update the label with the formatted date
    }


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

            if (i < daysOfWeek.length && i!=0) {
                addDaySeparator(i);
            }
        }
    }

    private void addDaySeparator(int columnIndex) {
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(100);
        separator.setPadding(new Insets(5, 0, 0, -7));
        scheduleGrid.add(separator, columnIndex + 1, 1, 1, GridPane.REMAINING);
    }

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
        scheduleGrid.getChildren().clear();
        updateDayLabel();
        addHourLabels();
        addDayHeaders();
        LocalDate weekEnd = currentWeekStart.plusDays(4);
        User currentUser = UserSession.getInstance().getCurrentUser();
        List<Seance> seances = new ArrayList<>();

        if (currentSearchCriteria.containsKey("Enseignant")) {
            EnseignantService enseignantService = new EnseignantService();
            String enseignant = currentSearchCriteria.get("Enseignant");
            String enseignantId = enseignantService.getEnseignantIdByName(enseignant).toString();
            seances = seanceService.getSeancesByCriteriaEnseignant( currentWeekStart, weekEnd, enseignantId);
        } else if (currentSearchCriteria.containsKey("Salle")) {
            SalleService salleService = new SalleService();
            String salle = currentSearchCriteria.get("Salle");
            String salleId = salleService.getSalleIdByName(salle).toString();
            seances = seanceService.getSeancesByCriteriaSalle( currentWeekStart, weekEnd,salleId);
        } else if (currentSearchCriteria.containsKey("Formation")) {
            FormationService formationService = new FormationService();
            String formation = currentSearchCriteria.get("Formation");
            String formationId = formationService.getFormationIdByName(formation).toString();
            seances = seanceService.getSeancesByCriteriaFormation( currentWeekStart, weekEnd,formationId);
        } else {
            seances = seanceService.getSeancesIntervalFrom(currentWeekStart, weekEnd, currentUser);
        }
        // Filter by "Type"
        if (currentFilterCriteria.containsKey("Type")) {
            String typeFilter = currentFilterCriteria.get("Type");
            seances = seances.stream()
                    .filter(seance -> seance.getType().getNom().equals(typeFilter))
                    .collect(Collectors.toList());
        }

        // Filter by "Salle"
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
                String cssClass = (i % 2 == 0) ? "row-light" : "row-dark";
                cell.getStyleClass().add(cssClass);
                scheduleGrid.add(cell, j, i);
                GridPane.setValignment(cell, VPos.TOP);
            }
        }

        for (Seance seance : seances) {
                addSeanceToGrid(seance);
            }
        highlightCurrentHourCell(currentWeekStart, weekEnd);

    }

    private void addSeanceToGrid(Seance seance) {
        ZonedDateTime startZdt = seance.getDtStart().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime endZdt = seance.getDtEnd().toInstant().atZone(ZoneId.systemDefault());

        Month startMonth = startZdt.getMonth();
        Month endMonth = endZdt.getMonth();


        if (startMonth.compareTo(Month.APRIL) >= 0 && startMonth.compareTo(Month.OCTOBER) <= 0) {
            startZdt = startZdt.plusHours(1);
        }

        if (endMonth.compareTo(Month.APRIL) >= 0 && endMonth.compareTo(Month.OCTOBER) <= 0) {
            endZdt = endZdt.plusHours(1);
        }


        int column = startZdt.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue() + 1;
        int startRow = calculateRowForTime(startZdt.toLocalTime());
        int endRow = calculateRowForTime(endZdt.toLocalTime());
        int durationInSlots = endRow - startRow;

        VBox seanceDetails = constructSeanceDetailsPane(seance);
        StackPane seancePane = new StackPane();
        String uidUpper = seance.getUid().toUpperCase();

        if (uidUpper.contains("RED")) {
            seancePane.getStyleClass().add("seance-red");
        } else if (uidUpper.contains("BLUE")) {
            seancePane.getStyleClass().add("seance-blue");
        } else if (uidUpper.contains("YELLOW")) {
            seancePane.getStyleClass().add("seance-yellow");
        } else {
            seancePane.getStyleClass().add("seance-pane");
        }




        // Add the hour cell before adding the seance pane
        // Add seance details to the pane
        Label typeLabel = new Label(seance.getType().getNom());
        typeLabel.getStyleClass().add("type-label");
        StackPane.setAlignment(typeLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(typeLabel, new Insets(0, 5, 5, 0));
        seancePane.getChildren().addAll(seanceDetails, typeLabel);

        StackPane.setAlignment(seanceDetails, Pos.TOP_LEFT);
        StackPane.setMargin(seanceDetails, new Insets(5));

        // Add the seancePane to the schedule grid
        scheduleGrid.add(seancePane, column, startRow, 1, durationInSlots + 1);
        GridPane.setValignment(seancePane, VPos.TOP);
    }

    private void highlightCurrentHourCell(LocalDate weekStart, LocalDate weekEnd) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(weekStart) || today.isAfter(weekEnd)) {
            return;
        }
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(19, 0))) {
            return;
        }

        int currentColumn = today.getDayOfWeek().getValue() - 1;
        int currentRow = calculateRowForTime(now);
        if (currentRow >= 0) {
            Region hourCell = new Region();
            hourCell.setStyle("-fx-background-color: #eedbbf;");
            hourCell.setPrefSize(100, 30);
            scheduleGrid.add(hourCell, currentColumn + 1, currentRow);
            GridPane.setValignment(hourCell, VPos.TOP);
        }
    }

    private VBox constructSeanceDetailsPane(Seance seance) {
        VBox seanceDetails = new VBox(2); // Spacing between elements in VBox

        TextField matiereField = new TextField("Matière: " + seance.getMatiere().getNom());
        matiereField.setEditable(false);
        matiereField.setBorder(null);
        matiereField.setBackground(Background.EMPTY);
        matiereField.getStyleClass().add("seance-label");

        // Create a Hyperlink for the enseignant
        Hyperlink enseignantLink = new Hyperlink("Enseignant: " + seance.getEnseignant().getUsername());
        enseignantLink.setOnAction(event -> {
            String email = seance.getEnseignant().getMail();
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
        return 1 + (int) (minutesFromStartOfDay / 30);
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


    @FXML
    private void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/m1prototypage/GUI/AddSeanceForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une séance");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            updateScheduleAndLabel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
