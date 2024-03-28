package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Salle;
import com.example.m1prototypage.utils.DataSource;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleService {

    Connection cnx = DataSource.getInstance().getCnx();


    public List<Salle> getAllSalles() {
        List<Salle> salles = new ArrayList<>();

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Salle WHERE LENGTH(nom) <= 30;")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                boolean disponible = resultSet.getBoolean("disponible");

                Salle salle = new Salle(id, nom, disponible);
                salles.add(salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }


    public Salle getSalleById(int id) {
        Salle salle = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Salle WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                boolean disponible = resultSet.getBoolean("disponible");
                salle = new Salle(id, nom, disponible);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salle;
    }


    public List<Salle> getSallesDisponibles() {
        List<Salle> salles = new ArrayList<>();

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Salle WHERE disponible = true")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                boolean disponible = resultSet.getBoolean("disponible");

                Salle salle = new Salle(id, nom, disponible);
                salles.add(salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }

}
