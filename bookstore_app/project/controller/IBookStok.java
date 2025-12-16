package bookstore_app.project.controller;
import java.time.LocalDate;

import bookstore_app.project.model.Book;
import bookstore_app.project.model.BookCopy;

public interface IBookStok {
    void addBookToStock(int id, Book book, LocalDate date);
    void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date);
    void removeBookCopyfromstock(BookCopy book);
    String showBookInformation(Book book);
}