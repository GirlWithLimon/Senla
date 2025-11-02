public class BookOrderItem {
    private final Book book;
    private BookCopy bookCopy;
    private OrderItemStatus status;
    private double price;
    
    public BookOrderItem(Book book) {
        this.book = book;
        this.status = OrderItemStatus.NEW;
        this.price = book.getPrice();
    }
    
    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }
    
    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }
    
    public BookCopy getBookCopy() {
        return bookCopy;
    }
    
    public Book getBook() { 
        return book; 
    }
    
    public OrderItemStatus getStatus() { 
        return status; 
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}