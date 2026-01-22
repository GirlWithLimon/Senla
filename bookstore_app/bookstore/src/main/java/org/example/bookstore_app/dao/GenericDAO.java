package org.example.bookstore_app.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {

    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void deleteById(ID id);

    default Connection getConnection() throws Exception {
        return DBConnect.getInstance().getConnection();
    }

}