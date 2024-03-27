package com.example.m1prototypage.controller;
import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.services.SeanceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateScheduleAndLabel();
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
        addHourLabels();
        addDayHeaders();
        LocalDate weekEnd = currentWeekStart.plusDays(4); // Monday to Friday
        User currentUser = UserSession.getInstance().getCurrentUser();
        List<Seance> seances;

        if (currentUser instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) currentUser;
            seances = seanceService.getSeancesForWeek(currentWeekStart, weekEnd, etudiant.getFormationId());
            for (Seance seance : seances) {
                System.out.println(seance);
            }

            // First, fill the grid with empty cells with alternate background colors
            for (int i = 1; i < scheduleGrid.getRowCount(); i++) {
                for (int j = 1; j < scheduleGrid.getColumnCount(); j++) {
                    Region cell = new Region();
                    String color = (i % 2 == 0) ? "white" : "#f0f5f5"; // Adjust colors as needed
                    cell.setStyle("-fx-background-color: " + color + ";");
                    scheduleGrid.add(cell, j, i);
                    GridPane.setValignment(cell, VPos.TOP); // Ensure alignment matches seancePane
                }
            }

            // Then, render each seance, which will override the default cell backgrounds
            for (Seance seance : seances) {
                addSeanceToGrid(seance); // Assumes this method correctly sets up each seancePane
            }
        }
    }


 /*   private List<Seance> getSeancesForCurrentUser(LocalDate weekEnd, User currentUser) {
        Etudiant etudiant = (Etudiant) currentUser;
        return seanceService.getSeancesForWeek(currentWeekStart, weekEnd, etudiant.getFormationId());
    }

    private void renderSeances(List<Seance> seances) {
        for (Seance seance : seances) {
            addSeanceToGrid(seance);
        }
    }*/

    private void addSeanceToGrid(Seance seance) {
        ZonedDateTime startZdt = seance.getDtStart().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime endZdt = seance.getDtEnd().toInstant().atZone(ZoneId.systemDefault());

// Adjust for daylight saving time from April to October
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
        seancePane.getStyleClass().add("seance-pane");

        // Add the hour cell before adding the seance pane
        highlightCurrentHourCell();

        // Add seance details to the pane
        Label typeLabel = new Label(seance.getType().getNom());
        typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");
        StackPane.setAlignment(typeLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(typeLabel, new Insets(0, 5, 5, 0)); // Adjust padding for type label
        seancePane.getChildren().addAll(seanceDetails, typeLabel);

        StackPane.setAlignment(seanceDetails, Pos.TOP_LEFT);
        StackPane.setMargin(seanceDetails, new Insets(5));

        // Add the seancePane to the schedule grid
        scheduleGrid.add(seancePane, column, startRow, 1, durationInSlots + 1);
        GridPane.setValignment(seancePane, VPos.TOP);
    }

    private void highlightCurrentHourCell() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        int currentColumn = today.getDayOfWeek().getValue();
        int currentRow = calculateRowForTime(now);
        Region hourCell = new Region();
        hourCell.setStyle("-fx-background-color: #faf0f0;"); // Set the background color
        scheduleGrid.add(hourCell, currentColumn, currentRow);
        GridPane.setValignment(hourCell, VPos.TOP); // Ensure alignment matches seancePane
    }


    private VBox constructSeanceDetailsPane(Seance seance) {
        VBox seanceDetails = new VBox(2); // Spacing between elements in VBox
        Label matiereLabel = new Label("Matière: " + seance.getMatiere().getNom());
        Label enseignantLabel = new Label("Enseignant: " + seance.getEnseignant().getUsername());
        Label salleLabel = new Label("Salle: " + seance.getSalle().getNom());

        // Styling labels, for example, can be added here or via CSS
        matiereLabel.getStyleClass().add("seance-label");
        enseignantLabel.getStyleClass().add("seance-label");
        salleLabel.getStyleClass().add("seance-label");

        seanceDetails.getChildren().addAll(matiereLabel, enseignantLabel, salleLabel);
        seanceDetails.getStyleClass().add("seance-details");
        return seanceDetails;
    }

    private int calculateRowForTime(LocalTime time) {
        long minutesFromStartOfDay = ChronoUnit.MINUTES.between(LocalTime.of(8, 0), time);
        return 1 + (int) (minutesFromStartOfDay / 30); // Assumes each row represents 30 minutes
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
