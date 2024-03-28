package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeanceService {

    Connection cnx = DataSource.getInstance().getCnx();

    MatiereService matiereService = new MatiereService();
    SalleService salleSevice = new SalleService();
    FormationService formationService = new FormationService();
    MemoService memoService = new MemoService();
    TypeService typeService = new TypeService();
    UserService userService = new UserService();


    public Seance getSeanceById(String uid) {
        Seance seance = null;
        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Seance WHERE uid = ?")) {
            statement.setString(1, uid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                seance = mapToSeance(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seance;
    }

    public List<Seance> getAllSeances() {
        List<Seance> seances = new ArrayList<>();
        try (Statement statement = cnx.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Seance");
            while (resultSet.next()) {
                Seance seance = mapToSeance(resultSet);
                seances.add(seance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    private Seance mapToSeance(ResultSet resultSet) throws SQLException {
        String uid = resultSet.getString("uid");
        Timestamp dtStart = resultSet.getTimestamp("dtStart");
        Timestamp dtEnd = resultSet.getTimestamp("dtEnd");
        int matiere_id = resultSet.getInt("matiere_id");
        Matiere matiere = matiereService.getMatiereById(matiere_id);
        int salle_id = resultSet.getInt("salle_id");
        Salle salle = salleSevice.getSalleById(salle_id);
        // Récupérer les enseignants
        int enseignant_id = resultSet.getInt("enseignant_id");
        Enseignant enseignant =  userService.getEnseignantById(enseignant_id);

        int formationId = resultSet.getInt("formation_id");
        Formation formation = formationService.getFormationById(formationId);

        int type_id = resultSet.getInt("type_id");
        TYPE type = typeService.getTypeById(type_id);


        int memo_id = resultSet.getInt("memo_id");
        Memo memo = memoService.getMemoById(memo_id);

        // Maintenant, on peut instancier l'objet Seance en utilisant toutes les informations récupérées
        return new Seance(uid, dtStart, dtEnd, matiere, enseignant, salle, formation, type, memo);

    }

    public List<Seance> getSeancesForWeek(LocalDate weekStart, LocalDate weekEnd, User user) {
        List<Seance> seances = new ArrayList<>();
        String query;
        // Determine the type of user and adjust the query accordingly
        if (user instanceof Etudiant) {
            query = "SELECT * FROM Seance WHERE dtStart BETWEEN ? AND ? AND Formation_id = ?";
        } else if (user instanceof Enseignant) {
            query = "SELECT * FROM Seance WHERE dtStart BETWEEN ? AND ? AND enseignant_id  = ?";
        } else {
            // Handle other types of users or throw an exception
            throw new IllegalArgumentException("Unsupported user type");
        }

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(weekStart.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(weekEnd.atTime(23, 59, 59)));

            // Set the third parameter based on the user type
            if (user instanceof Etudiant) {
                Etudiant etudiant = (Etudiant) user;
                statement.setInt(3, etudiant.getFormationId()); // Assuming Etudiant has getFormationId()
            } else if (user instanceof Enseignant) {
                Enseignant enseignant = (Enseignant) user;
                statement.setInt(3, enseignant.getId()); // Assuming Enseignant has getUsername()
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Seance seance = mapToSeance(resultSet);
                seances.add(seance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }


}
