package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Singleton
public class DBConnect {
    private static final Logger logger = LoggerFactory.getLogger(DBConnect.class);
    private static DBConnect instance;
    private Connection connection;
    @Inject
    private DBConfig dbConfig;



    public static DBConnect getInstance() {
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
                logger.debug("Закрытие подключения к БД выполнено успешно!");
            } catch (SQLException e) {
                logger.error("Ошибка при закрытии подключения к БД: {}", e.getMessage());
            }
        }
    }

}