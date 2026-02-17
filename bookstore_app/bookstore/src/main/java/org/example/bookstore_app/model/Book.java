package org.example.bookstore_app.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "book")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "author", nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "publicationDate", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "information", length = 1000)
    private String information;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookCopy> copies;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookOrderItem> orderItems;

    // Конструкторы
    public Book() {}
    public Book(String name, String author, Double price, LocalDate date) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = "Автор: " + author + ". Название книги: " + name;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }
    public Book(int id, String name, String author, Double price, LocalDate date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = "Автор: " + author + ". Название книги: " + name;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }

    public Book(int id, String name, String author, Double price, String information, LocalDate date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = information;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }

    // Геттеры и сеттеры (как в исходном коде)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getInfo() { return information; }
    public void setInfo(String information) { this.information = information; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public Boolean getBoolStatus() { return status == BookStatus.IN_STOCK; }

    public void setStatusStok() { this.status = BookStatus.IN_STOCK; }
    public void setStatusNo() { this.status = BookStatus.OUT_OF_STOCK; }

    public List<BookCopy> getCopies() { return copies; }
    public void setCopies(List<BookCopy> copies) { this.copies = copies; }

    public List<BookOrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<BookOrderItem> orderItems) { this.orderItems = orderItems; }

    @Override
    public String toString() {
        return this.name;
    }
}