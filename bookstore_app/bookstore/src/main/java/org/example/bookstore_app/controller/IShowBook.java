package org.example.bookstore_app.controller;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;

import java.util.List;

public interface IShowBook {
    void showBook();
    void sortByABC();
    void sortByPublicationDate();
    void sortByPrice();
    void sortByNumberCopies();
    void showOldBooksByDate();
    void showOldBooksByPrice();
    List<BookCopy> getOldBooksSortedByDate();
    List<BookCopy> getOldBooksSortedByPrice();
    void showAllBook();
    List<Book> sortABCBook();
}
