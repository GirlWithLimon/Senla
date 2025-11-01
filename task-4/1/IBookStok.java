import java.time.LocalDate;

public interface IBookStok{
    void addBookToStock(Book book, LocalDate date);
    void createOrder(Book book);
    void cancelOrder(BookOrder order);
    void removeBookFromStock(BookCopy book);
}