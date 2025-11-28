package project.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookOrder {
    private final int id;
    private final List<BookOrderItem> orderItems;
    private OrderStatus status;
    private final LocalDate orderDate;
    private String customerName;
    private String customerContact;
    private double totalPrice;
    
    public BookOrder(int id, String customerName, String customerContact) {
        this.id = id;
        this.orderItems = new ArrayList<>();
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDate.now();
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.totalPrice = 0.0;
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
        return orderItems.stream()
                .filter(item -> status.equals(item.getStatus()))
                .collect(Collectors.toList());
    }
    
    public boolean isFullyCompleted() {
        return orderItems.stream()
                .allMatch(item -> OrderItemStatus.COMPLETED.equals(item.getStatus()));
    }
    
    public boolean hasPendingItems() {
        return orderItems.stream()
                .anyMatch(item -> OrderItemStatus.PENDING.equals(item.getStatus()));
    }
    
    public int getId() { 
        return this.id; 
    }
    
    public List<BookOrderItem> getOrderItems() { 
        return new ArrayList<>(orderItems); 
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