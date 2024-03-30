package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Type;
import com.example.m1prototypage.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeService {

    Connection cnx = DataSource.getInstance().getCnx();

    public List<Type> getAllTypes() {
        List<Type> types = new ArrayList<>();

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Type")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("name");
                Type type = new Type(id, nom);
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public Type getTypeById(int id) {
        Type type = null;

        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Type WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("name");
                type = new Type(id, nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return type;
    }


}
