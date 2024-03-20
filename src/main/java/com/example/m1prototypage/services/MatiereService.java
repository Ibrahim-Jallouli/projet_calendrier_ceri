package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Matiere;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereService {

    Connection cnx = DataSource.getInstance().getCnx();

    public List<Matiere> getAllMatieres() {
        List<Matiere> matieres = new ArrayList<>();

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Matiere")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");

                Matiere matiere = new Matiere(id, nom);
                matieres.add(matiere);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matieres;
    }

    public Matiere getMatiereById(int id) {
        Matiere matiere = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Matiere WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                matiere = new Matiere(id, nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matiere;
    }


}
