module com.example.m1prototypage {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.M1IHM to javafx.fxml;
    exports com.example.M1IHM;
}
