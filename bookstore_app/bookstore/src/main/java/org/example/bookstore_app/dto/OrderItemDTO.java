package org.example.bookstore_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItemDTO {

    @JsonProperty("id")
    private int id;

    @JsonProperty("book_id")
    private int bookId;

    @JsonProperty("book_name")
    private String bookName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("price")
    private double price;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}