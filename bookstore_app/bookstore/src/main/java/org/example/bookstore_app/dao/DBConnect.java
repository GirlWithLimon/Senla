package org.example.bookstore_app.dao;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.config.DI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static DBConnect instance;
    private Connection connection;
    private DBConfig dbConfig;

    private DBConnect() throws SQLException {
        this.dbConfig = DI.getConfig(DBConfig.class);
        connect();
    }
    public static synchronized DBConnect getInstance() throws Exception {
        if (instance == null) {
            instance = new DBConnect();
        }
        return instance;
    }
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }
    private void connect() throws SQLException {
        this.connection = DriverManager.getConnection(
                dbConfig.getURL(),
                dbConfig.getUser(),
                dbConfig.getPassword()
        );
    }
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Закрытие подключения к БД выполнено успешно!");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии подключения к БД: " + e.getMessage());
            }
        }
    }

}
