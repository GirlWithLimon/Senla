package project.model;

import java.time.LocalDate;
import java.util.List;

public interface IShowOrdersAndRequests {
    void showOrdersByDate();
    void showOrdersByPrice();
    void showOrdersByStatus();
    void showRequestsByCount();
    void showRequestsByAlphabet();
    void showOrderDetails(BookOrder order);
    List<BookOrder> getCompletedOrdersByPeriod(LocalDate start, LocalDate end);
    double getEarnedMoneyByPeriod(LocalDate start, LocalDate end);
    int getCompletedOrdersCountByPeriod(LocalDate start, LocalDate end);
    List<BookOrder> sortOrderByDate();
}
