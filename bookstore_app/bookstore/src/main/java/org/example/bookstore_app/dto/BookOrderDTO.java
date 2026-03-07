package org.example.bookstore_app.dto;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.OrderStatus;

import java.time.LocalDate;

public class BookOrderDTO {
    private int id;
    private OrderStatus status;
    private LocalDate orderDate;
    private String customerName;
    private String customerContact;
    private double totalPrice;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

}
