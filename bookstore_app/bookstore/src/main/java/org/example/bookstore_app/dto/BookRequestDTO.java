package org.example.bookstore_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class BookRequestDTO {

    @NotBlank(message = "Название книги обязательно")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Автор обязателен")
    @JsonProperty("author")
    private String author;

    @NotNull(message = "Цена обязательна")
    @Min(value = 0, message = "Цена должна быть положительной")
    @JsonProperty("price")
    private Double price;

    @PastOrPresent(message = "Дата публикации не может быть в будущем")
    @JsonProperty("publication_date")
    private LocalDate publicationDate;

    @JsonProperty("information")
    private String information;

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getInformation() { return information; }
    public void setInformation(String information) { this.information = information; }
}