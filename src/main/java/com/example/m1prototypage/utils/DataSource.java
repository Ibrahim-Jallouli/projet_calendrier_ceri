package com.example.m1prototypage.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private Connection cnx;
    private static DataSource instance;

    private final String URL = "jdbc:mysql://localhost/calCeri";
    private final String USER = "root";
    private final String PASSWORD = "";

    private DataSource() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException ex) {
            System.err.println("Error establishing database connection: " + ex.getMessage());
            ex.printStackTrace();
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

    public void closeConnection() {
        if (cnx != null) {
            try {
                cnx.close();
                System.out.println("Connection closed");
            } catch (SQLException ex) {
                System.err.println("Error closing database connection: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
