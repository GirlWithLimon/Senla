import java.time.LocalDate;

public interface IOrderOperation{
    void createOrder(Book book);
    void cancelOrder(BookOrder order);
    void showOrdersByDate();
    void showOrdersByPrice();
    void showOrdersByStatus();
    
}