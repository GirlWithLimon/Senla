package testforcontrollers;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.controller.ShowBook;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestShowSortBooks {
    @Mock
    private BookstoreConfig config;
    @Mock
    private StockService stockService;
    @InjectMocks
    private ShowBook showBook;

    Book book1 = new Book();
    Book book2 = new Book();
    Book book3 = new Book();

    @BeforeEach
    public void takeBooks(){
        book1.setId(1);
        book1.setName("Book1");
        book1.setAuthor("Author1");
        book2.setId(2);
        book2.setName("Abc");
        book2.setAuthor("Author2");
        book3.setId(3);
        book3.setName("Book3");
        book3.setAuthor("Author3");
    }

    @Test
    public void showBookGood(){

        List<Book> books = Arrays.asList(book1,book2,book3);
        when(stockService.getBooks()).thenReturn(books);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showBook();

        String output = outContent.toString();
        assertTrue(output.contains("Книги по id:"));
        assertTrue(output.contains("1 - Book1"));
        assertTrue(output.contains("2 - Abc"));
        assertTrue(output.contains("3 - Book3"));
        assertFalse(output.contains("Каталог книг пуст"));
        System.setOut(System.out);
        verify(stockService, atLeastOnce()).getBooks();
    }

    @Test
    public void showBookBad(){

        List<Book> books = new ArrayList<>();
        when(stockService.getBooks()).thenReturn(books);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showBook();

        String output = outContent.toString();
        assertFalse(output.contains("Книги по id:"));
        assertFalse(output.contains("1 - Book1"));
        assertFalse(output.contains("2 - Abc"));
        assertFalse(output.contains("3 - Book3"));
        assertTrue(output.contains("Каталог книг пуст"));
        System.setOut(System.out);
        verify(stockService, atLeastOnce()).getBooks();
    }
}
