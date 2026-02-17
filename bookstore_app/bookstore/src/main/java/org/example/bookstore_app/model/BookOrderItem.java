package org.example.bookstore_app.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "orderItem")
public class BookOrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idBook", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idBookCopy")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrders", nullable = false)
    private BookOrder order;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Request request;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    public BookOrderItem() {}

    public BookOrderItem(int id, Book book, BookOrder order) {
        this.id = id;
        this.book = book;
        this.order = order;
        this.status = OrderItemStatus.NEW;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) {
        this.book = book;
    }

    public BookCopy getBookCopy() { return bookCopy; }
    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }
    public BookOrder getOrder() { return order; }
    public void setOrder(BookOrder order) {
        this.order = order;
    }

    public OrderItemStatus getStatus() { return status; }
    public void setStatus(OrderItemStatus status) { this.status = status; }
}