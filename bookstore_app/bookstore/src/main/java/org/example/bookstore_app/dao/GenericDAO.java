package org.example.bookstore_app.dao;

import java.util.List;

public interface GenericDAO<T, ID> {

    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void deleteById(ID id);

}