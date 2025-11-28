package project.model;
public class Request {
    private final String id;
    private final BookOrderItem orderItem;
    
    public Request(String id, BookOrderItem orderItem) {
        this.id = id;
        this.orderItem = orderItem;
    }
    
    public String getId(){
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