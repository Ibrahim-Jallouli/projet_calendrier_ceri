package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;

public class UserService {
    Connection cnx = DataSource.getInstance().getCnx();
    FormationService formationService= new FormationService();
    public User getUser(String username) {

        User user = null;
        try (PreparedStatement statement = cnx.prepareStatement("SELECT id,username, password, email, null AS formation_id FROM `Enseignant` WHERE username = ?\n" +
                "UNION " +
                "SELECT id,username, password, null AS email, formation_id FROM `Etudiant` WHERE username = ?;")) {
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                if (resultSet.getString("email") != null) {
                    System.out.println("Cet utilisateur est un enseignant.");
                    String email = resultSet.getString("email");
                    user = new Enseignant(id, username, password, email);
                } else {
                    System.out.println("Cet utilisateur est un etudiant.");
                    int id_formation = resultSet.getInt("formation_id");
                    Formation formation = formationService.getFormationById(id_formation);
                    user = new Etudiant(id, username, password, formation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Enseignant getEnseignantById(int id) {
        User user = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM `Enseignant` WHERE id = ?")){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("username");
                String mail = resultSet.getString("email");
                String password = resultSet.getString("password");
                user = new Enseignant(id, nom,password,mail);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return Enseignant.class.cast(user);


    }

}
