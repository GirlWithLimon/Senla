package org.example.bookstore_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequestDTO {

    @NotBlank(message = "Имя клиента обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    @JsonProperty("customer_name")
    private String customerName;

    @NotBlank(message = "Контакт клиента обязателен")
    @Email(message = "Некорректный email")
    @JsonProperty("customer_contact")
    private String customerContact;

    @NotEmpty(message = "Список книг не может быть пустым")
    @JsonProperty("book_ids")
    private List<Integer> bookIds;

    // Геттеры и сеттеры
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }

    public List<Integer> getBookIds() { return bookIds; }
    public void setBookIds(List<Integer> bookIds) { this.bookIds = bookIds; }
}