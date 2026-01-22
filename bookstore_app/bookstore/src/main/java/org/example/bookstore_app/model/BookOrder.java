package org.example.bookstore_app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookOrder implements Serializable {
    private final int id;
    private List<BookOrderItem> orderItems;
    private OrderStatus status;
    private LocalDate orderDate;
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
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice;  }
    public  void setOrderItems(List <BookOrderItem> orderItem){
        this.orderItems = orderItem;
    }
}