package project.model;
import java.time.LocalDate;

public class BookCopy {
    private final int id;
    private final Book book;
    private final LocalDate arrivalDate;

    public BookCopy(int  id, Book book, LocalDate date) {
        this.id = id;
        this.book = book;
        this.arrivalDate = date;
        this.book.setStatusStok();
    }
    
    public int getId(){
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