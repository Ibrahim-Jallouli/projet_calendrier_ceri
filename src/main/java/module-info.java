module com.example.m1prototypage {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.m1prototypage to javafx.fxml;
    exports com.example.m1prototypage;
    exports com.example.m1prototypage.controller;
    opens com.example.m1prototypage.controller to javafx.fxml;
}