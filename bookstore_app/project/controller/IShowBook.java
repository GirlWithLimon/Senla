package bookstore_app.project.controller;

import java.util.List;

import bookstore_app.project.model.Book;
import bookstore_app.project.model.BookCopy;

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