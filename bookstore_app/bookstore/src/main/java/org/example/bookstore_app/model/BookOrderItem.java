package org.example.bookstore_app.model;

import java.io.Serializable;

public class BookOrderItem implements Serializable {
    private int id;
    private int idBook;
    private int idBookCopy;
    private int idOrder;
    private OrderItemStatus status;
    
    public BookOrderItem(int id, int idBook, int idOrder) {
        this.id=id;
        this.idBook = idBook;
        this.idOrder = idOrder;
        this.status = OrderItemStatus.NEW;

    }
    
    public int getId(){
        return this.id;
    }
    public int getIdBook() {
        return idBook;
    }
    public int getIdBookCopy() {
        return idBookCopy;
    }
    public int getIdOrder(){return idOrder;}
    public OrderItemStatus getStatus() {
        return status;
    }

    public void setId(int id){this.id = id;}
    public void setIdBook(int idBook) {this.idBook = idBook; }
    public void setIdBookCopy(int idBookCopy) {
        this.idBookCopy = idBookCopy;
    }
    public void setIdOrder(int idOrder) { this.idOrder = idOrder; }
    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }


}