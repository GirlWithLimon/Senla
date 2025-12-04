package bookstore_app.model;
public class Request {
    private final int id;
    private final BookOrderItem orderItem;
    
    public Request(int id, BookOrderItem orderItem) {
        this.id = id;
        this.orderItem = orderItem;
    }
    
    public int getId(){
        return this.id;
    }
    public Book getBook() {
        return orderItem.getBook();
    }
    
    public BookOrderItem getOrderItem() {
        return orderItem;
    }
    
    public void ContinueRequest(BookCopy bookCopy) {
        orderItem.setBookCopy(bookCopy);
        orderItem.setStatus(OrderItemStatus.COMPLETED);
    }
}