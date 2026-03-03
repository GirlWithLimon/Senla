package org.example.bookstore_app.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
public class BookOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "orderdate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "customername", nullable = false)
    private String customerName;

    @Column(name = "customercontact", nullable = false)
    private String customerContact;

    @Column(name = "totalprice", nullable = false)
    private double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookOrderItem> orderItems;

    public BookOrder() {}

    public BookOrder(int id, String customerName, String customerContact) {
        this.id = id;
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDate.now();
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.totalPrice = 0.0;
    }

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

    public List<BookOrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<BookOrderItem> orderItems) { this.orderItems = orderItems; }
}