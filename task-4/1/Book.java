import java.time.LocalDate;

public class Book {
    private final String name;
    private final String author;
    private BookStatus status;
    private double price;
    private final LocalDate publicationDate;
    private String information;
    
    public Book(String name, String author, Double price, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = "Автор: " + author + ". Название книги: " + name;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }

    public Book(String name, String author, Double price, String information, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = information;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }
    
    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public Boolean getBoolStatus() {
        return status == BookStatus.IN_STOCK;
    }
    
    public void setStatusStok() {
        this.status = BookStatus.IN_STOCK;
        System.out.println("Добавлена на склад книга: " + this);
    }
    
    public void setStatusNo() {
        this.status = BookStatus.OUT_OF_STOCK;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }
    
    public String getInfo() {
        return information;
    }
    
    public void setInfo(String info) {
        this.information = info;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}