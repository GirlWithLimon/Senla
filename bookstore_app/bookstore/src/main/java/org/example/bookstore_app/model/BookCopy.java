package org.example.bookstore_app.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BookCopy implements Serializable {
    private  int id;
    private  int idBook;
    private  LocalDate arrivalDate;
    private boolean sale;

    public BookCopy(int  id, int idBook, LocalDate date) {
        this.id = id;
        this.idBook = idBook;
        this.arrivalDate = date;
        this.sale = false;
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
    public boolean getSale(){return  sale;}

    public void setId(int id){this.id = id;}
    public void setSale(boolean sale){this.sale=sale;}

}
