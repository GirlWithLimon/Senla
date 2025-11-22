package project.controller;
import java.time.LocalDate;

import project.model.Book;
import project.model.BookCopy;

public interface IBookStok {
    void addBookToStock(Book book, LocalDate date);
    void removeBookFromStock(BookCopy book);
    String showBookInformation(Book book);
}