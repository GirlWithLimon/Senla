

public interface IBookStok{
    void addBookToStock(Book book);
    void createOrder(Book book);
    void cancelOrder(BookOrder order);
    void removeBookFromStock(Book book);
}