public class BookOrder {
    private final Book book;
    private BookCopy bookCopy;
    private String status; 
    
    public BookOrder(Book book) {
        this.book = book;
        this.status = "Новый";
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }
    public BookCopy getBookCopy() {
        return  bookCopy;
    }
    public Book getBook() { return book; }
    public String getStatus() { return status; }
}