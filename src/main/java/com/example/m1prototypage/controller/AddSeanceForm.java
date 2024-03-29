package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.services.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        // Initialisation des ComboBox s'ils sont statiques

        // Ajouter un écouteur sur la propriété value de dateDebutPicker
        dateDebutPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Vérifier si la nouvelle valeur n'est pas nulle
            if (newValue != null) {
                // Définir la valeur de la date de fin sur celle de la date de début
                dateFinPicker.setValue(newValue);
            }
        });

        // Définition des heures de début et de fin
        LocalTime heureDebut = LocalTime.of(8, 0);
        LocalTime heureFin = LocalTime.of(19, 0);

        // Création d'une liste pour stocker les valeurs d'heures
        List<String> heures = new ArrayList<>();

        // Boucle pour générer les heures par intervalles de 30 minutes
        while (heureDebut.isBefore(heureFin.plusMinutes(1))) {
            heures.add(heureDebut.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            heureDebut = heureDebut.plusMinutes(30);
        }

        // Ajout de la liste des heures au ComboBox
        heureDebutComboBox.setItems(FXCollections.observableArrayList(heures));
        heureFinComboBox.setItems(FXCollections.observableArrayList(heures));

        //setupDynamicSearchListener(heureDebutComboBox, heures);
        //setupDynamicSearchListener(heureFinComboBox, heures);




    }

    @FXML
    private void saisirInfos() {

        List<Matiere> matiereList;
        List<TYPE> typeList;
        List<Formation> formationList;
        List<Memo> memoList;

        // Ajouter un écouteur sur la propriété value de dateDebutPicker
        dateDebutPicker.setOnAction(event -> {
            // Récupérer la date sélectionnée comme date de début
            LocalDate dateDebut = dateDebutPicker.getValue();

            // Vérifier si la date de début n'est pas nulle
            if (dateDebut != null) {
                // Définir la date de fin sur la date de début
                dateFinPicker.setValue(dateDebut);
            }
        });

        // Récupérer la date de début sélectionnée
        LocalDate dateDebut = dateDebutPicker.getValue();
        // Récupérer la date de fin sélectionnée
        LocalDate dateFin = dateFinPicker.getValue();

        // Récupérer l'heure de début sélectionnée
        LocalTime heureDebut = heureDebutComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureDebutComboBox.getValue()) : null;
        // Récupérer l'heure de fin sélectionnée
        LocalTime heureFin = heureFinComboBox.getValue() != null ? LocalTime.parse((CharSequence) heureFinComboBox.getValue()) : null;

        // Vérifier si les dates et heures sont valides
        if (dateDebut != null && dateFin != null && !dateFin.isBefore(dateDebut)
                && heureDebut != null && heureFin != null && !heureFin.isBefore(heureDebut)) {

            // Convertir les dates et heures en LocalDateTime
            LocalDateTime dateTimeDebut = LocalDateTime.of(dateDebut, heureDebut);
            LocalDateTime dateTimeFin = LocalDateTime.of(dateFin, heureFin);

            List<Salle> sallesDisponibles = determineSallesDisponibles(dateTimeDebut,dateTimeFin);
            for (Salle s:sallesDisponibles){
                salles.put(s.getNom(),s.getId());
            }

            // Peupler le ComboBox avec les salles disponibles
            salleComboBox.setItems(FXCollections.observableArrayList(salles.keySet()));


            matiereList = matiereService.getAllMatieres(); // Remplacer DatabaseService par votre service ou DAO approprié
            for (Matiere m:matiereList){
                matieres.put(m.getNom(),m.getId());
            }

            typeList = typeService.getAllTypes();
            for (TYPE t:typeList){
                types.put(t.getNom(),t.getId());
            }
            formationList = formationService.getAllFormations();
            for (Formation f:formationList){
                formations.put(f.getNom(),f.getId());
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
            System.out.println("Veuillez sélectionner des dates valides.");
        }
    }

    private List<Salle> determineSallesDisponibles(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin) {
        // Récupérer toutes les séances pendant la plage horaire spécifiée
        List<Seance> seancesPendantPlageHoraire = seanceService.getSeancesPendantPlageHoraire(dateTimeDebut, dateTimeFin);
        System.out.println(seancesPendantPlageHoraire);
        // Récupérer toutes les salles disponibles dans la base de données
        List<Salle> toutesLesSalles = salleService.getAllSalles();

        // Liste pour stocker les salles disponibles
        List<Salle> sallesDisponibles = new ArrayList<>(toutesLesSalles);

        // Parcourir toutes les séances pendant la plage horaire spécifiée
        for (Seance seance : seancesPendantPlageHoraire) {
            // Retirer de la liste des salles disponibles les salles associées à chaque séance

            sallesDisponibles.removeIf(salle -> salle.getId() == seance.getSalle().getId());

        }
        return sallesDisponibles;
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

            LocalDateTime dateTimeDebut = LocalDateTime.of(dateDebut, heureDebut);
            LocalDateTime dateTimeFin = LocalDateTime.of(dateFin, heureFin);
            // Récupérer les objets correspondants aux noms sélectionnés
            Date dateDebutConvertie = Date.from(dateTimeDebut.atZone(ZoneId.systemDefault()).toInstant());
            Date dateFinConvertie = Date.from(dateTimeFin.atZone(ZoneId.systemDefault()).toInstant());



            Seance seance = new Seance();
            seance.setUid(UID);
            seance.setDtStart(dateDebutConvertie);
            seance.setDtEnd(dateFinConvertie);
            seance.setEnseignant((Enseignant) currentUser);
            seance.setFormation(new Formation(formations.get(nomFormation),nomFormation));
            seance.setMatiere(new Matiere(matieres.get(nomMatiere),nomMatiere));
            seance.setType(new TYPE(types.get(nomType),nomType));
            seance.setMemo(new Memo(memos.get(memo),memo));
            seance.setSalle(new Salle(salles.get(nomSalle),nomSalle,false));

            seanceService.ajouterSeance(seance);






    }

}
