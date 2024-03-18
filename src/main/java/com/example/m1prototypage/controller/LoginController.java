package com.example.m1prototypage.controller;

import com.example.m1prototypage.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
       /* // Charger le fichier FXML de l'emploi du temps
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/calendar-view.fxml"));
        Parent emploiDuTempsParent = loader.load();

        // Obtenir la scène actuelle
        Scene currentScene = loginButton.getScene();

        // Changer la racine de la scène pour afficher la nouvelle vue (l'emploi du temps)
        currentScene.setRoot(emploiDuTempsParent);*/

        // Charger le fichier FXML de l'emploi du temps avec le controller approprié
        FXMLLoader loader = new FXMLLoader(CalendarController.class.getResource("GUI/calendar-view.fxml"));
        Parent emploiDuTempsParent = loader.load();

        // Obtenir la scène actuelle à partir du bouton de connexion
        Scene currentScene = loginButton.getScene();

        // Créer une nouvelle scène avec le parent de l'emploi du temps
        Scene calendarScene = new Scene(emploiDuTempsParent);

        // Obtenir la fenêtre (stage) à partir de la scène actuelle
        Stage primaryStage = (Stage) currentScene.getWindow();

        // Changer de scène (aller à l'emploi du temps)
        primaryStage.setScene(calendarScene);
        primaryStage.show();


    }
}
