package org.example.bookstore_app.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(int id) {
        super("Заказ с ID " + id + " не найден");
    }
}