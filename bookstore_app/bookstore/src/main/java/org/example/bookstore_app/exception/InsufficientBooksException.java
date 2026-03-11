package org.example.bookstore_app.exception;

public class InsufficientBooksException extends RuntimeException {

    public InsufficientBooksException(String message) {
        super(message);
    }
}
