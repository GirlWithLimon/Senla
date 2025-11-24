package project.controller;

import java.util.List;

import project.model.Book;
import project.model.BookOrder;

public interface IOrderOperation {
    BookOrder createOrder(String id, List<Book> books, String customerName, String customerContact);
    void cancelOrder(BookOrder order);
    void cancelOrderItem(BookOrder order, Book book);
   
}