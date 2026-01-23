package org.example.bookstore_app.model;

import java.io.Serializable;

public class Request implements Serializable {
    private  int id;
    private  int idOrderItem;
    
    public Request(int id, int idOrderItem) {
        this.id = id;
        this.idOrderItem = idOrderItem;
    }
    
    public int getId(){
        return this.id;
    }
    public int getIdOrderItem() {
        return this.idOrderItem;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setIdOrderItem(int idOrderItem) {
        this.idOrderItem = idOrderItem;
    }
}