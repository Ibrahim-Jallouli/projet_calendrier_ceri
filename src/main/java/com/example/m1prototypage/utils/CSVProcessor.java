package com.example.m1prototypage.utils;

import java.io.FileReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.example.m1prototypage.entities.User;
import com.opencsv.CSVReader;

public class CSVProcessor {

    // Initialize sets to store unique values for each entity
    private static Set<String> matieresSet = new HashSet<>();
    private static Set<String> enseignantsSet = new HashSet<>();
    private static Set<String> typesSet = new HashSet<>();
    private static Set<String> memosSet = new HashSet<>();
    private static Set<String> sallesSet = new HashSet<>();

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost/calCeri";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");

            // Read CSV file
            try (CSVReader reader = new CSVReader(new FileReader("src/main/java/com/example/m1prototypage/utils/output.csv"))) {
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // Process each row here
                    String uid = nextLine[2];
                    String dtStart = nextLine[0];
                    String dtEnd = nextLine[1];
                    String matiere = nextLine[4];
                    String enseignant = nextLine[5];
                    String type = nextLine[6];
                    String memo = nextLine[7];
                    String salle = nextLine[3];
                    String formation = nextLine[8];

                    // Insert the row into the Seance table
                    insertSeance(connection, uid, dtStart, dtEnd, matiere, enseignant, salle, formation, type, memo);

                    // Insert only if the value is not already in the set
                   /* if (!matieresSet.contains(matiere)) {
                        insertMatiere(connection, matiere);
                        matieresSet.add(matiere);
                    }
                    if (!enseignantsSet.contains(enseignant)) {
                        insertEnseignant(connection, enseignant);
                        enseignantsSet.add(enseignant);
                    }
                    if (!typesSet.contains(type)) {
                        insertType(connection, type);
                        typesSet.add(type);
                    }
                    if (!sallesSet.contains(salle)) {
                        insertSalle(connection, salle);
                        sallesSet.add(salle);
                    }
                    if (!memosSet.contains(memo)) {
                        insertMemo(connection, memo);
                        memosSet.add(memo);
                    }*/
                }
            }
            // Close database connection
            connection.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertMatiere(Connection connection, String matiere) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Matiere (nom) VALUES (?)")) {
            // Check if the matiere already exists in the database
            int matiereId = getMatiereIdByName(connection, matiere);

            // If the matiere doesn't exist, insert it into the database
            if (matiereId == -1) {
                statement.setString(1, matiere);
                statement.executeUpdate();
                System.out.println("Matiere inserted: " + matiere);
            } else {
                System.out.println("Matiere already exists: " + matiere);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getMatiereIdByName(Connection connection, String matiere) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Matiere WHERE nom = ?")) {
            statement.setString(1, matiere);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1; // Matiere doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void insertEnseignant(Connection connection, String enseignant) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Enseignant (username) VALUES (?)")) {
            // Check if the enseignant already exists in the database
            int enseignantId = getEnseignantIdByName(connection, enseignant);

            // If the enseignant doesn't exist, insert it into the database
            if (enseignantId == -1) {
                statement.setString(1, enseignant);
                statement.executeUpdate();
                System.out.println("Enseignant inserted: " + enseignant);
            } else {
                System.out.println("Enseignant already exists: " + enseignant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getEnseignantIdByName(Connection connection, String enseignant) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Enseignant WHERE username = ?")) {
            statement.setString(1, enseignant);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1; // Enseignant doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void insertType(Connection connection, String type) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Type (name) VALUES (?)")) {
            // Check if the type already exists in the database
            int typeId = getTypeIdByName(connection, type);

            // If the type doesn't exist, insert it into the database
            if (typeId == -1) {
                statement.setString(1, type);
                statement.executeUpdate();
                System.out.println("Type inserted: " + type);
            } else {
                System.out.println("Type already exists: " + type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getTypeIdByName(Connection connection, String type) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Type WHERE name = ?")) {
            statement.setString(1, type);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1; // Type doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void insertMemo(Connection connection, String memo) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Memo (description) VALUES (?)")) {
            // Check if the memo already exists in the database
            int memoId = getMemoIdByDescription(connection, memo);

            // If the memo doesn't exist, insert it into the database
            if (memoId == -1) {
                statement.setString(1, memo);
                statement.executeUpdate();
                System.out.println("Memo inserted: " + memo);
            } else {
                System.out.println("Memo already exists: " + memo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getMemoIdByDescription(Connection connection, String memo) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Memo WHERE description = ?")) {
            statement.setString(1, memo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1; // Memo doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static void insertSalle(Connection connection, String salle) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Salle (nom) VALUES (?)")) {
            // Check if the salle already exists in the database
            int salleId = getSalleIdByName(connection, salle);

            // If the salle doesn't exist, insert it into the database
            if (salleId == -1) {
                statement.setString(1, salle);
                statement.executeUpdate();
                System.out.println("Salle inserted: " + salle);
            } else {
                System.out.println("Salle already exists: " + salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getSalleIdByName(Connection connection, String salle) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Salle WHERE nom = ?")) {
            statement.setString(1, salle);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1; // Salle doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static int getFormationIdByName(Connection connection, String formation) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM Formation WHERE nom = ?")) {
            statement.setString(1, formation);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                System.err.println("Formation not found: " + formation);
                return -1; // Formation doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    private static void insertSeance(Connection connection, String uid, String dtStart, String dtEnd, String matiere, String enseignant, String salle, String formation, String type, String memo) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Seance (uid, dtStart, dtEnd, matiere_id, enseignant_id, salle_id, formation_id, type_id, memo_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            // Convert date strings to Date objects (You may need to use SimpleDateFormat or other methods depending on your date format)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date startDate = dateFormat.parse(dtStart);
            Date endDate = dateFormat.parse(dtEnd);

            // Get IDs for matiere, enseignant, salle, formation, type, and memo
            int matiereId = getMatiereIdByName(connection, matiere);
            int enseignantId = getEnseignantIdByName(connection, enseignant);
            int salleId = getSalleIdByName(connection, salle);
            int formationId = getFormationIdByName(connection, formation);
            int typeId = getTypeIdByName(connection, type);
            int memoId = getMemoIdByDescription(connection, memo);

            statement.setString(1, uid);
            statement.setTimestamp(2, new Timestamp(startDate.getTime()));
            statement.setTimestamp(3, new Timestamp(endDate.getTime()));
            statement.setInt(4, matiereId);
            statement.setInt(5, enseignantId);
            statement.setInt(6, salleId);
            statement.setInt(7, formationId);
            statement.setInt(8, typeId);
            statement.setInt(9, memoId);

            statement.executeUpdate();
            System.out.println("Seance inserted: " + uid);
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }





}
