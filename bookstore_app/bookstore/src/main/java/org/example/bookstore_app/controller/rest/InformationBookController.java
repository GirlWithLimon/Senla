package org.example.bookstore_app.controller.rest;

import jakarta.validation.Valid;
import org.example.bookstore_app.dto.BookRequestDTO;
import org.example.bookstore_app.dto.BookResponseDTO;
import org.example.bookstore_app.exception.BookNotFoundException;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books/sort")
public class InformationBookController {

    private static final Logger logger = LoggerFactory.getLogger(BookRestController.class);

    @Autowired
    private StockService stockService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        logger.info("GET /api/books - запрос на получение всех книг");

        List<Book> books = stockService.getBooks();
        List<BookResponseDTO> dtos = books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") int id) {
        logger.info("GET /api/books/{} - запрос на получение книги", id);

        Book book = stockService.getBooksById(id);
        if (book == null) {
            throw new BookNotFoundException("Книга с ID " + id + " не найдена");
        }

        return ResponseEntity.ok(convertToDTO(book));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO bookRequest) {
        logger.info("POST /api/books - создание книги: {}", bookRequest.getName());

        Book book = new Book(
                bookRequest.getName(),
                bookRequest.getAuthor(),
                bookRequest.getPrice(),
                bookRequest.getPublicationDate() != null ? bookRequest.getPublicationDate() : LocalDate.now()
        );

        if (bookRequest.getInformation() != null) {
            book.setInfo(bookRequest.getInformation());
        }

        stockService.addBook(book);
        logger.info("Книга создана с ID: {}", book.getId());

        return new ResponseEntity<>(convertToDTO(book), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable("id") int id,
                                                      @Valid @RequestBody BookRequestDTO bookRequest) {
        logger.info("PUT /api/books/{} - обновление книги", id);

        Book existingBook = stockService.getBooksById(id);
        if (existingBook == null) {
            throw new BookNotFoundException("Книга с ID " + id + " не найдена");
        }

        existingBook.setName(bookRequest.getName());
        existingBook.setAuthor(bookRequest.getAuthor());
        existingBook.setPrice(bookRequest.getPrice());

        if (bookRequest.getInformation() != null) {
            existingBook.setInfo(bookRequest.getInformation());
        }

        if (bookRequest.getPublicationDate() != null) {
            existingBook.setPublicationDate(bookRequest.getPublicationDate());
        }

        stockService.updateBook(existingBook);
        logger.info("Книга с ID {} обновлена", id);

        return ResponseEntity.ok(convertToDTO(existingBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) {
        logger.info("DELETE /api/books/{} - удаление книги", id);

        Book book = stockService.getBooksById(id);
        if (book == null) {
            throw new BookNotFoundException("Книга с ID " + id + " не найдена");
        }

        stockService.removeBook(book);
        logger.info("Книга с ID {} удалена", id);

        return ResponseEntity.noContent().build();
    }

    private BookResponseDTO convertToDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setName(book.getName());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setInformation(book.getInfo());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setStatus(book.getStatus().name());

        int copiesCount = stockService.findCountByIdBook(book.getId());
        dto.setAvailableCopies(copiesCount);

        return dto;
    }
}
