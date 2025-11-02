import java.time.LocalDate;

public class BookCopy {
    private final Book book;
    private final LocalDate arrivalDate;

    public BookCopy(Book book, LocalDate date) {
        this.book = book;
        this.arrivalDate = date;
        this.book.setStatusStok();
    }
    
    public Book getBook() {
        return this.book;
    }
    
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }
    
    @Override
    public String toString() {
        return this.book.getName();
    }
}