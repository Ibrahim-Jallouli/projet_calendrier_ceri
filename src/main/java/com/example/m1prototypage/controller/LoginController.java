package com.example.m1prototypage.controller;

import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.entities.UserSession;
import com.example.m1prototypage.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label loginErrorLabel;

    UserService userService = new UserService();

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        // Clear previous error messages
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        loginErrorLabel.setText("");

        // Input validation
        boolean inputValid = true;
        if (usernameField.getText().isEmpty()) {
            usernameErrorLabel.setText("Le champ Identifiant est vide.");
            inputValid = false;
        }

        if (passwordField.getText().isEmpty()) {
            passwordErrorLabel.setText("Le champ Mot de passe est vide.");
            inputValid = false;
        }

        if (!inputValid) {
            return;
        }

        // Attempt to log in
        User user = userService.getUser(usernameField.getText());
        if (user != null && user.getPassword().equals(passwordField.getText())) {
            System.out.println("Identifiants corrects");
            UserSession.getInstance().setCurrentUser(user);
            openCalendarView();
        } else {
            System.out.println("Identifiants incorrects");
            loginErrorLabel.setText("Identifiant ou mot de passe incorrect.");
        }
    }

    private void openCalendarView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/m1prototypage/GUI/calendar-view.fxml"));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setWidth(1050);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameField.setText("uapv2400431");
        passwordField.setText("Ibrahim");
    }
}
