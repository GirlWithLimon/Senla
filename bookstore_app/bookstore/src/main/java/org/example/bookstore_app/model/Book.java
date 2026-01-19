package org.example.bookstore_app.model;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private final String name;
    private final String author;
    private BookStatus status;
    private double price;
    private final LocalDate publicationDate;
    private String information;
    
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
    
    public  int getId(){
        return this.id;
    }
    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public Boolean getBoolStatus() {
        return status == BookStatus.IN_STOCK;
    }
    
    public void setStatusStok() {
        this.status = BookStatus.IN_STOCK;
    }
    
    public void setStatusNo() {
        this.status = BookStatus.OUT_OF_STOCK;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }
    
    public String getInfo() {
        return information;
    }
    
    public void setInfo(String info) {
        this.information = info;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public void setId(int newId) {
        this.id =newId;
    }
}