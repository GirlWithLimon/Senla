package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.annotation.Singleton;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.config.DI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Singleton
public class DBConnect {
    private static DBConnect instance;
    private Connection connection;
    @Inject
    private DBConfig dbConfig;


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