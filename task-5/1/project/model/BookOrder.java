package project.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookOrder {
    private final String orderId;
    private final List<BookOrderItem> orderItems;
    private OrderStatus status;
    private final LocalDate orderDate;
    private String customerName;
    private String customerContact;
    private double totalPrice;
    
    public BookOrder(String customerName, String customerContact) {
        this.orderId = generateOrderId();
        this.orderItems = new ArrayList<>();
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDate.now();
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.totalPrice = 0.0;
    }
    
    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    public void addBookToOrder(BookOrderItem orderItem) {
        orderItems.add(orderItem);
        calculateTotalPrice();
    }
    
    public void removeBookFromOrder(Book book) {
        orderItems.removeIf(item -> item.getBook().equals(book));
        calculateTotalPrice();
    }
    
    private void calculateTotalPrice() {
        totalPrice = orderItems.stream()
                .mapToDouble(BookOrderItem::getPrice)
                .sum();
    }
    
    public List<BookOrderItem> getItemsByStatus(OrderItemStatus status) {
        List<BookOrderItem> result = new ArrayList<>();
        for (BookOrderItem item : orderItems) {
            if (status.equals(item.getStatus())) {
                result.add(item);
            }
        }
        return result;
    }
    
    public boolean isFullyCompleted() {
        return orderItems.stream().allMatch(item -> OrderItemStatus.COMPLETED.equals(item.getStatus()));
    }
    
    public boolean hasPendingItems() {
        return orderItems.stream().anyMatch(item -> OrderItemStatus.PENDING.equals(item.getStatus()));
    }
    
    public String getOrderId() { 
        return orderId; 
    }
    
    public List<BookOrderItem> getOrderItems() { 
        return orderItems; 
    }
    
    public OrderStatus getStatus() { 
        return status; 
    }
    
    public LocalDate getOrderDate() { 
        return orderDate; 
    }
    
    public String getCustomerName() { 
        return customerName; 
    }
    
    public String getCustomerContact() { 
        return customerContact; 
    }
    
    public double getTotalPrice() { 
        return totalPrice; 
    }
    
    public void setStatus(OrderStatus status) { 
        this.status = status; 
    }
    
    public void setCustomerName(String customerName) { 
        this.customerName = customerName; 
    }
    
    public void setCustomerContact(String customerContact) { 
        this.customerContact = customerContact; 
    }
}