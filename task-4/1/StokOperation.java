import java.time.LocalDate;

public interface StokOperation{
    void addBookToStock(Book book, LocalDate date);
    void removeBookFromStock(BookAtStok book);
}