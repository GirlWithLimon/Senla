public class BookOrder {
    private final Book book;
    private String status; 
    
    public BookOrder(Book book) {
        this.book = book;
        this.status = "Новый";
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Book getBook() { return book; }
    public String getStatus() { return status; }
}