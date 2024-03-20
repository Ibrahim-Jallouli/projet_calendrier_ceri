package com.example.m1prototypage.services;

import com.example.m1prototypage.entities.Memo;
import com.example.m1prototypage.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemoService {

    Connection cnx = DataSource.getInstance().getCnx();


    public Memo getMemoById(int memoId) {
        Memo memo = null;
        try (PreparedStatement statement = cnx.prepareStatement("SELECT * FROM Memo WHERE id = ?")) {
            statement.setInt(1, memoId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                memo = mapToMemo(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memo;
    }

    private Memo mapToMemo(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String description = resultSet.getString("description");
        return new Memo(id, description);
    }

    public List<Memo> getAllMemos() {
        List<Memo> memos = new ArrayList<>();
        try (Statement statement = cnx.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Memo");
            while (resultSet.next()) {
                Memo memo = mapToMemo(resultSet);
                memos.add(memo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memos;
    }


}
