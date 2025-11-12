package project.model;

import java.util.List;
import java.time.LocalDate;

public interface IOrderOperation {
    BookOrder createOrder(List<Book> books, String customerName, String customerContact);
    void cancelOrder(BookOrder order);
    void cancelOrderItem(BookOrder order, Book book);
    void showOrdersByDate();
    void showOrdersByPrice();
    void showOrdersByStatus();
    void showRequestsByCount();
    void showRequestsByAlphabet();
    void showOrderDetails(BookOrder order);
    List<BookOrder> getCompletedOrdersByPeriod(LocalDate start, LocalDate end);
    double getEarnedMoneyByPeriod(LocalDate start, LocalDate end);
    int getCompletedOrdersCountByPeriod(LocalDate start, LocalDate end);
}