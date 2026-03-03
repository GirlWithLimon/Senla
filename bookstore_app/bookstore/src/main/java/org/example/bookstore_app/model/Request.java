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
    @JoinColumn(name = "idorderitem", nullable = false, unique = true)
    private BookOrderItem orderitem;

    public Request() {}

    public Request(int id, BookOrderItem orderitem) {
        this.id = id;
        this.orderitem = orderitem;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BookOrderItem getOrderitem() { return orderitem; }
    public void setOrderitem(BookOrderItem orderitem) {
        this.orderitem = orderitem;
    }
}