import java.time.LocalDate;

public interface IOrderOperation{
   void createOrder(Book book);
   void cancelOrder(BookOrder order);
   public  void showOrdersByDate();
   public  void showOrdersByPrice();
   public  void showOrdersByStatus();
    
}