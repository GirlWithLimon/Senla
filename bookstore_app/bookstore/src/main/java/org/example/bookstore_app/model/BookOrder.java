package org.example.bookstore_app.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BookOrder implements Serializable {
    private int id;
    private OrderStatus status;
    private LocalDate orderDate;
    private String customerName;
    private String customerContact;
    private double totalPrice;
    
    public BookOrder(int id, String customerName, String customerContact) {
        this.id = id;
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDate.now();
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.totalPrice = 0.0;
    }

    public int getId() { 
        return this.id; 
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

    public void setId(int id){this.id=id;}
    public void setStatus(OrderStatus status) { 
        this.status = status; 
    }
    public void setOrderDate(LocalDate date){this.orderDate=date;}
    public void setCustomerName(String customerName) { 
        this.customerName = customerName; 
    }
    public void setCustomerContact(String customerContact) { 
        this.customerContact = customerContact; 
    }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice;  }
}
