package org.example.bookstore_app.model;

import java.io.Serializable;

public enum OrderItemStatus implements Serializable {
    NEW("Новый"),
    PENDING("В ожидании"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");
    
    private final String displayName;
    
    OrderItemStatus(String displayName) {
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
