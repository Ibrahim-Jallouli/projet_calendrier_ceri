package com.example.m1prototypage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddSeanceForm {
    @FXML
    private TextField nomSeance;

    @FXML
    private void ajouterSeance() {
        String nom = nomSeance.getText();
        // Logique pour ajouter la séance
        System.out.println("Séance ajoutée : " + nom);
        // Fermez la fenêtre après l'ajout, si souhaité.
    }

}
