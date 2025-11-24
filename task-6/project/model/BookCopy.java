package project.model;
import java.time.LocalDate;

public class BookCopy {
    private final String id;
    private final Book book;
    private final LocalDate arrivalDate;

    public BookCopy(String id, Book book, LocalDate date) {
        this.id = id;
        this.book = book;
        this.arrivalDate = date;
        this.book.setStatusStok();
    }
    
    public String getId(){
        return this.id;
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