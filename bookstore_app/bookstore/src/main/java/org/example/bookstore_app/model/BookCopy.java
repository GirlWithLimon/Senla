package org.example.bookstore_app.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BookCopy implements Serializable {
    private final int id;
    private int idBook;
    private final LocalDate arrivalDate;

    public BookCopy(int  id, int idBook, LocalDate date) {
        this.id = id;
        this.idBook = idBook;
        this.arrivalDate = date;
    }
    
    public int getId(){
        return this.id;
    }
    public int getIdBook() {
        return this.idBook;
    }
    
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

}