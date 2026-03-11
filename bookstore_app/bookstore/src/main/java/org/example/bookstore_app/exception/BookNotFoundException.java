package org.example.bookstore_app.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(int id) {
        super("Книга с ID " + id + " не найдена");
    }
}