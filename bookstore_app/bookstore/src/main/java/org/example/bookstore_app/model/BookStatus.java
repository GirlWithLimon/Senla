package org.example.bookstore_app.model;

import java.io.Serializable;

public enum BookStatus implements Serializable {
    IN_STOCK("В наличии"),
    OUT_OF_STOCK("Отсутствует");
    
    private final String displayName;
    
    BookStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}