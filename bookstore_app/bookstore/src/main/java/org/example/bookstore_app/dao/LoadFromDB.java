package org.example.bookstore_app.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadFromDB {


    private boolean tablesExist(Connection conn) throws SQLException {
        String checkSql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = 'book')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next()) {
                return rs.getBoolean(1); // true если таблица существует
            }
            return false;
        }
    }
    private void loadDate(Connection conn){

    }
}
