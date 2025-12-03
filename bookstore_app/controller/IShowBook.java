package bookstore_app.controller;

import java.util.List;

import bookstore_app.model.Book;
import bookstore_app.model.BookCopy;

public interface IShowBook {
    void showBook();
    void sortByABC();
    void sortByPublicationDate();
    void sortByPrice();
    void sortByNumberCopies();
    void showOldBooks();
    List<BookCopy> getOldBooksSortedByDate();
    List<BookCopy> getOldBooksSortedByPrice();
    void showAllBook();
    List<Book> sortABCBook();
}