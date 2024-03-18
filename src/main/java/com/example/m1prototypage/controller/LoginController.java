package com.example.m1prototypage.controller;

import com.example.m1prototypage.HelloApplication;
import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    UserService userService = new UserService();


    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = userService.getUser(username);
        // Vérifier les identifiants

        if (user.getPassword().equals(password)) {
                System.out.println("identifiants corrects");
                // Rediriger vers l'emploi du temps
                openCalendarView();
                return;
        }

        // Afficher un message d'erreur si les identifiants sont incorrects
        System.out.println("Identifiants incorrects");
        // Vous pouvez afficher un message d'erreur à l'utilisateur ici

    }

    private void openCalendarView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/m1prototypage/GUI/calendar-view.fxml"));
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();
        primaryStage.setScene(new Scene(loader.load()));
    }
}
