package bookstore_app.controller;
import java.time.LocalDate;

import bookstore_app.model.Book;
import bookstore_app.model.BookCopy;

public interface IBookStok {
    void addBookToStock(int id, Book book, LocalDate date);
    void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date);
    void removeBookFromStock(BookCopy book);
    String showBookInformation(Book book);
}