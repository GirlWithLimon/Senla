
import java.time.LocalDate;

public class Book{
    private final String name;
    private final String author;
    private Boolean status;
    private  double price;
    private final LocalDate publicationDate;
    private String information;
    public Book(String name, String author, Double price, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = "Автор: "+ author+". Название книги: "+name;
        this.publicationDate = date;
    }

    public Book(String name, String author, Double price, String information, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = information;
        this.publicationDate = date;
    }
    
    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }
    public String getStatus() {
        if(status) return  "В наличии";
        else return "Отсутствует";
    }
    public Boolean getBoolStatus() {
        return  this.status;
    }
    public void setStatusStok(){
        this.status = true;
        System.out.println("Добавлена на склад книга: " + this);
    }
    public void setStatusNo(){
        this.status = false;
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
    public void  setInfo(String info) {
       this.information = info;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    @Override
    public String toString(){
        return this.name;
    }
}