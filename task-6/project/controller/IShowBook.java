package project.controller;

import java.util.List;

import project.model.Book;
import project.model.BookCopy;

public interface IShowBook {
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