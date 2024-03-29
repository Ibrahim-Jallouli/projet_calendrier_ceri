package com.example.m1prototypage;

import com.example.m1prototypage.utils.DataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CalApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CalApplication.class.getResource("GUI/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("Emploi du temps!");

        // Définir la taille minimale de la fenêtre pour s'assurer que tout le contenu est visible
        stage.setMinWidth(500);
        stage.setMinHeight(470);

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        System.out.println("Initializing application...");
        DataSource.getInstance();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
