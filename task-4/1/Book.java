
import java.time.LocalDate;

public class Book{
    private final String name;
    private final String author;
    private  double price;
    private final LocalDate publicationDate;

    public Book(String name, String author, Double price, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationDate = date;
    }
    
    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }
    public void setPrice(Double price) {
       this.price = price;
    }

    public Double getPrice() {
        return price;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    @Override
    public String toString(){
        return this.name;
    }
}