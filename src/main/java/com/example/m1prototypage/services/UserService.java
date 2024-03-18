package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Enseignant;
import com.example.m1prototypage.entities.Etudiant;
import com.example.m1prototypage.entities.Formation;
import com.example.m1prototypage.entities.User;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;

public class UserService {
    Connection cnx = DataSource.getInstance().getCnx();
    FormationService formationService= new FormationService();


    public User getUser(String username) {

        User user = null;
        try (PreparedStatement statement = cnx.prepareStatement("SELECT id,username, password, email, null AS formation_id FROM `Enseignant` WHERE username = ?\n" +
                "UNION\n" +
                "SELECT id,username, password, null AS email, formation_id FROM `Etudiant` WHERE username = ?;\n")) {
            statement.setString(1, username);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Récupérez les informations de l'utilisateur depuis le résultat de la requête
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
               // int id_formation = resultSet.getInt("formation_id");
                //String mail = resultSet.getS
                //System.out.println(id_formation);
                // Vérifiez si l'utilisateur est un étudiant ou un enseignant

                if (resultSet.getString("email") == null) {
                    // L'utilisateur est un étudiant
                    System.out.println("Cet utilisateur est un etudiant.");
                    int id_formation = resultSet.getInt("formation_id");
                    Formation formation = formationService.getFormationById(id_formation);
                    // Créez une instance de la classe Etudiant
                    user = new Etudiant(id, username,password,formation);
                } else {
                    // L'utilisateur est un enseignant
                    System.out.println("Cet utilisateur est un enseignant.");
                    String mail = resultSet.getString("email");

                    // Créez une instance de la classe Enseignant
                    user = new Enseignant(id, username,password, mail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

}
