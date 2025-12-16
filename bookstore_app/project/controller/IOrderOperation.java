package bookstore_app.project.controller;

import java.util.List;

import bookstore_app.project.model.Book;
import bookstore_app.project.model.BookOrder;

public interface IOrderOperation {
    BookOrder createOrder(int id, List<Book> books, String customerName, String customerContact);
    void cancelOrder(BookOrder order);
    void cancelOrderItem(BookOrder order, Book book);
   
}