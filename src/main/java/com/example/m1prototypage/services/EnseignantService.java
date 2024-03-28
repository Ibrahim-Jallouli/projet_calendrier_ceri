package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Enseignant;
import com.example.m1prototypage.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnseignantService {
    Connection cnx = DataSource.getInstance().getCnx();

    public List<Enseignant> getAllEnseignants() {
        List<Enseignant> enseignants = new ArrayList<>();
        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Enseignant WHERE username NOT LIKE '%,%';\n")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String mail = resultSet.getString("email");
                Enseignant enseignant = new Enseignant(id, username, password, mail);
                enseignants.add(enseignant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enseignants;
    }

}
