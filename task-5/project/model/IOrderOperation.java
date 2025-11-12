package project.model;

import java.util.List;

public interface IOrderOperation {
    BookOrder createOrder(List<Book> books, String customerName, String customerContact);
    void cancelOrder(BookOrder order);
    void cancelOrderItem(BookOrder order, Book book);
   
}