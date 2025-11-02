import java.time.LocalDate;

public interface IBookStok{
    void addBookToStock(Book book, LocalDate date);
    void removeBookFromStock(BookCopy book);
}