package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.*;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceService {

    Connection cnx = DataSource.getInstance().getCnx();

    MatiereService matiereService = new MatiereService();
    SalleSevice salleSevice = new SalleSevice();
    FormationService formationService = new FormationService();
    MemoService memoService = new MemoService();
    TypeService typeService = new TypeService();


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
        List<Enseignant> enseignantsList = new ArrayList<>();


        int formationId = resultSet.getInt("formation_id");
        Formation formation = formationService.getFormationById(formationId);

        int type_id = resultSet.getInt("type_id");
        TYPE type = typeService.getTypeById(type_id);


        int memo_id = resultSet.getInt("memo_id");
        Memo memo = memoService.getMemoById(memo_id);

        // Maintenant, on peut instancier l'objet Seance en utilisant toutes les informations récupérées
        return new Seance(uid, dtStart, dtEnd, matiere, enseignantsList.toArray(new Enseignant[0]), salle, formation, type, memo);

    }

}
