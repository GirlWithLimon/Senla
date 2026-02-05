package org.example.bookstore_app.controller;

import java.util.List;

public interface ICSVImportExport<T> {
    String getFileName();
    String getHeaders();
    String toCSV(T entity);
    T fromCSV(String csvLine);
    List<T> getAllEntities();
    void saveEntities(List<T> entities);
}
