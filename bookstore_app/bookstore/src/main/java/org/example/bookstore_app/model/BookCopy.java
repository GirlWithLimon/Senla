package org.example.bookstore_app.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "bookCopy")
public class BookCopy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idBook", nullable = false)
    private Book book;

    @Column(name = "arrivalDate", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "sale", nullable = false)
    private boolean sale;

    public BookCopy() {}

    public BookCopy(int id, Book book, LocalDate date) {
        this.id = id;
        this.book=book;
        this.arrivalDate = date;
        this.sale = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

    public boolean getSale() { return sale; }
    public void setSale(boolean sale) { this.sale = sale; }
}