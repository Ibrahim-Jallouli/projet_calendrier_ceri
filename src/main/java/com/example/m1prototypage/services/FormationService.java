package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Enseignant;
import com.example.m1prototypage.entities.Etudiant;
import com.example.m1prototypage.entities.Formation;
import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FormationService {

    Connection cnx = DataSource.getInstance().getCnx();
    public Formation getFormationByNom(String nom) {
        Formation formation = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Formation WHERE nom = ?")) {
            statement.setString(1, nom);

            ResultSet resultSet = statement.executeQuery();
            // Si une formation correspondante est trouvée
            if (resultSet.next()) {
                // Récupérez les informations de la formation depuis le résultat de la requête
                int id = resultSet.getInt("id");

                // Créez une instance de la classe Formation avec les informations récupérées
                formation = new Formation(id, nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formation;
    }

    public Formation getFormationById(int id) {
        Formation formation = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Formation WHERE id = ?")) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            // Si une formation correspondante est trouvée
            if (resultSet.next()) {
                // Récupérez les informations de la formation depuis le résultat de la requête
                String nom = resultSet.getString("nom");

                // Créez une instance de la classe Formation avec les informations récupérées
                formation = new Formation(id, nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formation;
    }

    public List<Formation> getAllFormations(){
        List<Formation> formations = new ArrayList<>();

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Formation")) {

            ResultSet resultSet = statement.executeQuery();
            // Tant qu'il y a des formations dans le résultat
            while (resultSet.next()) {
                // Récupérer les informations de la formation depuis le résultat de la requête
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");

                // Créer une instance de la classe Formation avec les informations récupérées
                Formation formation = new Formation(id, nom);

                // Ajouter la formation à la liste des formations
                formations.add(formation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return formations;
    }
}
