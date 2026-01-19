package org.example.bookstore_app.dao;

import org.example.annotation.ConfigProperty;
import org.example.annotation.PropertyType;

public class DBConfig {
    @ConfigProperty( configFileName = "jdbc.properties", propertyName = "url", type = PropertyType.STRING)
    private  String URL = "jdbc:postgresql://localhost:5433/bookstore";

    @ConfigProperty(propertyName = "user", type = PropertyType.STRING)
    private  String USER = "postgress";

    @ConfigProperty(propertyName = "password")
    private  String password = "1235";

    public  String getURL() {
        return URL;
    }

    public  String getUser() {
        return USER;
    }

    public  String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "BookstoreConfig{" +
                "monthsForOldBook=" + URL +
                ", autoCompleteRequests=" + USER +
                ", csvDelimiter='" + password + '\'' +
                '}';
    }
}
