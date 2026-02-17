package org.example.bookstore_app.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "request")
public class Request implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrderItem", nullable = false, unique = true)
    private BookOrderItem orderItem;

    public Request() {}

    public Request(int id, BookOrderItem orderItem) {
        this.id = id;
        this.orderItem = orderItem;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BookOrderItem getOrderItem() { return orderItem; }
    public void setOrderItem(BookOrderItem orderItem) {
        this.orderItem = orderItem;
    }
}