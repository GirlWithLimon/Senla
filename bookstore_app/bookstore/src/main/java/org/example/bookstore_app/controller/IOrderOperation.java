package org.example.bookstore_app.controller;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.model.BookOrderItem;

import java.util.List;

public interface IOrderOperation {
    BookOrder createOrder(List<Book> bookList, String customerName, String customerContact);
    BookOrderItem createOrderItem(BookOrder order,Book book);
    void cancelOrder(int idOrder);
    void cancelOrderItem(BookOrder order, Book book);
   
}
