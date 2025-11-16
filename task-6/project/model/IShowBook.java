package project.model;

import java.util.List;

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