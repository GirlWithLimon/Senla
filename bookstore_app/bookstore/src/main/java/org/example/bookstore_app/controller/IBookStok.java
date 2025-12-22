package org.example.bookstore_app.controller;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;

import java.time.LocalDate;

public interface IBookStok {
    void addBookToStock(int id, Book book, LocalDate date);
    void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date);
    void removeBookCopyfromstock(BookCopy book);
    String showBookInformation(Book book);
}