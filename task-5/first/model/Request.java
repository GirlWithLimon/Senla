package first.model;
public class Request {
    private final BookOrderItem orderItem;
    
    public Request(BookOrderItem orderItem) {
        this.orderItem = orderItem;
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