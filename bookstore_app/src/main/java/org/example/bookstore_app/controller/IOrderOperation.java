package org.example.bookstore_app.controller;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookOrder;

import java.util.List;

public interface IOrderOperation {
    BookOrder createOrder(int id, List<Book> books, String customerName, String customerContact);
    void cancelOrder(BookOrder order);
    void cancelOrderItem(BookOrder order, Book book);
   
}