package org.example.bookstore_app.dao;

import org.example.annotation.Component;
import org.example.annotation.ConfigProperty;
import org.example.annotation.PropertyType;

@Component
public class DBConfig {
    //настройки для подключения к базе данных

    @ConfigProperty(configFileName = "jdbc.properties", propertyName = "url", type = PropertyType.STRING)
    private  String URL = "jdbc:postgresql://localhost:5433/bookstore";

    @ConfigProperty(configFileName = "jdbc.properties", propertyName = "user", type = PropertyType.STRING)
    private  String USER = "postgress";

    @ConfigProperty(configFileName = "jdbc.properties", propertyName = "password")
    private  String PASSWORD = "1234";

    public  String getURL() {
        return URL;
    }

    public  String getUser() {
        return USER;
    }

    public  String getPassword() {
        return PASSWORD;
    }


    @Override
    public String toString() {
        return "BookstoreConfig{"
                +"monthsForOldBook=" + URL
                +", autoCompleteRequests=" + USER
                +", csvDelimiter='" + PASSWORD + '\''
                +'}';
    }
}

