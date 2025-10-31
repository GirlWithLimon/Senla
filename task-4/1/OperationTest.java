import java.time.LocalDate;
public class OperationTest {
    public static void main(String[] args) {
        IBookStok shop = new Stok();
        
        Book book1 = new Book("Война и мир", "Л.Н.Толстой", 250.0, LocalDate.of(2014, 10, 24));
        Book book2 = new Book("Мастер и Маргарита", "М.А.Булгаков", 260.0, LocalDate.of(2014, 10, 24));
        
        shop.addBookToStock(book1);
        shop.addBookToStock(book2);
        shop.addBookToStock(book1);
        
        shop.createOrder(book1); 
        shop.createOrder(book1);
        shop.createOrder(book1);
        shop.addBookToStock(book1);
        shop.removeBookFromStock(book2);
        shop.createOrder(book2); 
    }
}
