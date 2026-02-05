package org.example.bookstore_app.model;


import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {
    //книги
    private int id;
    private String name;
    private String author;
    private BookStatus status;
    private double price;
    private LocalDate publicationDate;
    private String information;
    public Book(int id, String name, String author, Double price, LocalDate date) {
        //создание книг без указания описания
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.information = "Автор: " + author + ". Название книги: " + name;
        this.publicationDate = date;
        this.status = BookStatus.OUT_OF_STOCK;
    }

    public Book(int id, String name, String author, Double price, String information, LocalDate date) {
        //создание книг с указанием описания
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
    public String getName() {
        return name;
    }
    public String getAuthor() {
        return author;
    }
    public Double getPrice() {
        return price;
    }
    public String getInfo() {
        return information;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public BookStatus getStatus() {
        return status;
    }
    public Boolean getBoolStatus() {
        return status == BookStatus.IN_STOCK;
    }

    public void setId(int newId) {
        this.id =newId;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public void setStatusStok() {
        this.status = BookStatus.IN_STOCK;
    }
    public void setStatusNo() {
        this.status = BookStatus.OUT_OF_STOCK;
    }
    public void setInfo(String info) {
        this.information = info;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
