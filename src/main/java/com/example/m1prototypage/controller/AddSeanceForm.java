package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.services.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddSeanceForm {
    @FXML
    private TextField nomSeance;
    @FXML
    private VBox infoBox;
    @FXML
    DatePicker dateDebutPicker;
    @FXML
    DatePicker dateFinPicker;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> colorComboBox;

    @FXML
    private ComboBox<String> formationComboBox;

    @FXML
    private ComboBox<String> matiereComboBox;

    @FXML
    private ComboBox<String> salleComboBox;

    @FXML
    private ComboBox<String> memoComboBox;

    private SeanceService seanceService;
    private SalleService salleService;
    private MatiereService matiereService;
    private TypeService typeService;
    private MemoService memoService;

    @FXML private ComboBox heureDebutComboBox;
    @FXML private ComboBox heureFinComboBox;
    @FXML private TextField uid;

    FormationService formationService;

    HashMap<String,Integer> matieres = new HashMap<String,Integer>();
    HashMap<String,Integer> formations = new HashMap<String,Integer>();
    HashMap<String,Integer> types = new HashMap<String,Integer>();
    HashMap<String,Integer> salles = new HashMap<String,Integer>();
    HashMap<String,Integer> memos = new HashMap<String,Integer>();

    User currentUser;

    @FXML
    private void initialize() {

        currentUser = UserSession.getInstance().getCurrentUser();
        seanceService = new SeanceService();
        salleService = new SalleService();
        memoService = new MemoService();
        typeService = new TypeService();
        matiereService = new MatiereService();
        formationService = new FormationService();
        colorComboBox.setItems(FXCollections.observableArrayList("RED", "BLUE", "YELLOW"));

        colorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int randomNumber = new Random().nextInt(255) + 1;
                uid.setText(newValue + randomNumber);
            }
        });

        dateDebutPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateFinPicker.setValue(newValue);
            }
        });

        LocalTime heureDebut = LocalTime.of(8, 0);
        LocalTime heureFin = LocalTime.of(19, 0);

        List<String> heures = new ArrayList<>();

        while (heureDebut.isBefore(heureFin.plusMinutes(1))) {
            heures.add(heureDebut.format(DateTimeFormatter.ofPattern("HH:mm")));
            heureDebut = heureDebut.plusMinutes(30);
        }

        heureDebutComboBox.setItems(FXCollections.observableArrayList(heures));
        heureFinComboBox.setItems(FXCollections.observableArrayList(heures));

        //setupDynamicSearchListener(heureDebutComboBox, heures);
        //setupDynamicSearchListener(heureFinComboBox, heures);
    }

    @FXML
    private void saisirInfos() {

        List<Matiere> matiereList;
        List<Type> typeList;
        List<Formation> formationList;
        List<Memo> memoList;

        dateDebutPicker.setOnAction(event -> {
            LocalDate dateDebut = dateDebutPicker.getValue();
            if (dateDebut != null) {
                dateFinPicker.setValue(dateDebut);
            }
        });


        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        LocalTime heureDebut = heureDebutComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureDebutComboBox.getValue()) : null;
        LocalTime heureFin = heureFinComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureFinComboBox.getValue()) : null;

        // Vérifier si les dates et heures sont valides
        if (dateDebut != null && dateFin != null && !dateFin.isBefore(dateDebut)
                && heureDebut != null && heureFin != null && !heureFin.isBefore(heureDebut)) {

            LocalDateTime dateTimeDebut = LocalDateTime.of(dateDebut, heureDebut);
            LocalDateTime dateTimeFin = LocalDateTime.of(dateFin, heureFin);

            List<Salle> sallesDisponibles = determineSallesDisponibles(dateTimeDebut,dateTimeFin);
            for (Salle s:sallesDisponibles){
                salles.put(s.getNom(),s.getId());
            }
            salleComboBox.setItems(FXCollections.observableArrayList(salles.keySet()));

            List<Formation> formationsDisponibles = determineFormationsDisponibles(dateTimeDebut, dateTimeFin);
            for (Formation f : formationsDisponibles) {
                formations.put(f.getNom(), f.getId());
            }
            formationComboBox.setItems(FXCollections.observableArrayList(formations.keySet()));


            matiereList = matiereService.getAllMatieres(); // Remplacer DatabaseService par votre service ou DAO approprié
            for (Matiere m:matiereList){
                matieres.put(m.getNom(),m.getId());
            }

            typeList = typeService.getAllTypes();
            for (Type t:typeList){
                types.put(t.getNom(),t.getId());
            }
            memoList = memoService.getAllMemos();
            for (Memo m:memoList){
                memos.put(m.getDecsription(),m.getId());
            }


            matiereComboBox.setItems(FXCollections.observableArrayList(matieres.keySet()));
            typeComboBox.setItems(FXCollections.observableArrayList(types.keySet()));
            formationComboBox.setItems(FXCollections.observableArrayList(formations.keySet()));
            memoComboBox.setItems(FXCollections.observableArrayList(memos.keySet()));

            // Appeler la méthode setupDynamicSearchListener pour chaque ComboBox
           // setupDynamicSearchListener(matiereComboBox, nomMatieres);
            //setupDynamicSearchListener(typeComboBox, nomTypes);
           // setupDynamicSearchListener(formationComboBox, nomFormations);
            //setupDynamicSearchListener(memoComboBox, memos);

            infoBox.setVisible(true);

        } else {
            // Afficher un message d'erreur si les dates ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Date");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner des dates valides.");

            alert.showAndWait();
        }

    }

    private List<Salle> determineSallesDisponibles(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin) {
        List<Seance> seancesPendantPlageHoraire = seanceService.getSeancesPendantPlageHoraire(dateTimeDebut, dateTimeFin);

        List<Salle> toutesLesSalles = salleService.getAllSalles();
        List<Salle> sallesDisponibles = new ArrayList<>(toutesLesSalles);
        for (Seance seance : seancesPendantPlageHoraire) {
            Iterator<Salle> iterator = sallesDisponibles.iterator();

            while (iterator.hasNext()) {
                Salle salle = iterator.next();
                if (salle.getId() == seance.getSalle().getId()) {
                    iterator.remove(); // This removes the current salle from sallesDisponibles
                }
            }
        }

        return sallesDisponibles;
    }

    private List<Formation> determineFormationsDisponibles(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin) {
        List<Seance> seancesPendantPlageHoraire = seanceService.getSeancesPendantPlageHoraire(dateTimeDebut, dateTimeFin);
        List<Formation> toutesLesFormations = formationService.getAllFormations();
        List<Formation> formationsDisponibles = new ArrayList<>(toutesLesFormations);

        for (Seance seance : seancesPendantPlageHoraire) {
            Iterator<Formation> iterator = formationsDisponibles.iterator();

            while (iterator.hasNext()) {
                Formation formation = iterator.next();
                if (formation.getId() == seance.getFormation().getId()) {
                    iterator.remove();
                }
            }
        }

        return formationsDisponibles;
    }


   /* private void setupDynamicSearchListener(ComboBox<String> comboBox, List<String> dataList) {
        comboBox.setEditable(true);
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                List<String> filteredValues = dataList.stream()
                        .filter(value -> value.toLowerCase().startsWith(newValue.toLowerCase()))
                        .collect(Collectors.toList());
                comboBox.setItems(FXCollections.observableArrayList(filteredValues));
            } else {
                // Si le champ de texte est vide, rétablissez la liste complète des valeurs
                comboBox.setItems(FXCollections.observableArrayList(dataList));
            }
        });
    }*/


    @FXML
    private void ajouterSeance() {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null ||
                heureDebutComboBox.getValue() == null || heureFinComboBox.getValue() == null ||
                matiereComboBox.getValue() == null || formationComboBox.getValue() == null ||
                typeComboBox.getValue() == null || salleComboBox.getValue() == null ||
                memoComboBox.getValue() == null || uid.getText().trim().isEmpty()) {

            // Afficher une alerte à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText("Tous les champs sont requis");
            alert.setContentText("Veuillez remplir tous les champs avant de soumettre.");
            alert.showAndWait();
        } else {
            // Récupérer les noms sélectionnés dans les ComboBox
            String nomMatiere = matiereComboBox.getValue();
            String nomFormation = formationComboBox.getValue();
            String nomType = typeComboBox.getValue();
            String nomSalle = salleComboBox.getValue();
            String memo = memoComboBox.getValue();
            String UID = uid.getText();


        // Récupérer les autres informations nécessaires depuis le formulaire
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();
            LocalTime heureDebut = heureDebutComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureDebutComboBox.getValue()) : null;
            LocalTime heureFin = heureFinComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureFinComboBox.getValue()) : null;

// Test pour ajuster l'heure si la date de début ou de fin est entre avril et octobre
            Month monthDebut = dateDebut.getMonth();
            Month monthFin = dateFin.getMonth();
            if ((monthDebut.compareTo(Month.APRIL) >= 0 && monthDebut.compareTo(Month.OCTOBER) <= 0) ||
                    (monthFin.compareTo(Month.APRIL) >= 0 && monthFin.compareTo(Month.OCTOBER) <= 0)) {
                // Soustraire une heure
                heureDebut = heureDebut.minusHours(1);
                heureFin = heureFin.minusHours(1);
            }

            LocalDateTime dateTimeDebut = LocalDateTime.of(dateDebut, heureDebut);
            LocalDateTime dateTimeFin = LocalDateTime.of(dateFin, heureFin);

// Convertir LocalDateTime en Date pour seance.setDtStart et seance.setDtEnd
            Date dateDebutConvertie = Date.from(dateTimeDebut.atZone(ZoneId.systemDefault()).toInstant());
            Date dateFinConvertie = Date.from(dateTimeFin.atZone(ZoneId.systemDefault()).toInstant());

            Seance seance = new Seance();
            seance.setUid(UID);
            seance.setDtStart(dateDebutConvertie);
            seance.setDtEnd(dateFinConvertie);
            seance.setEnseignant((Enseignant) currentUser);
            seance.setFormation(new Formation(formations.get(nomFormation),nomFormation));
            seance.setMatiere(new Matiere(matieres.get(nomMatiere),nomMatiere));
            seance.setType(new Type(types.get(nomType),nomType));
            seance.setMemo(new Memo(memos.get(memo),memo));
            seance.setSalle(new Salle(salles.get(nomSalle),nomSalle,false));

            seanceService.ajouterSeance(seance);



    }
    }

}
