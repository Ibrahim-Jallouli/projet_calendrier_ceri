package com.example.m1prototypage.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private Connection cnx;
    private static DataSource instance;

    private final String URL = "jdbc:mysql://localhost/calCeri";
    private final String PWD = "";
    private final String USER = "root";

    private DataSource() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connexion établie");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }
    public Connection getCnx() {
        return cnx;
    }




}
