package testforservices;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestStokServiceBooks {
    @Mock
    private IBookService bookService;
    @InjectMocks
    private StockService stockService;

    Book book1;
    Book book2;

    @BeforeEach
    public void books(){
        book1 = new Book();
        book1.setId(1);
        book1.setName("Book1");
        book1.setAuthor("Author1");
        book1.setPrice(40.0);
        book1.setStatusStok();

        book2 = new Book();
        book2.setId(2);
        book2.setName("Book2");
        book2.setAuthor("Author2");
        book2.setPrice(50.0);
        book2.setStatusStok();
    }
    @Test
    public void TestGetBooks(){
        List<Book> books = Arrays.asList(book1,book2);
        when(bookService.findAll()).thenReturn(books);

        List<Book> result = stockService.getBooks();
        assertEquals(2, result.size());
        assertSame("Book1", result.getFirst().getName());
        assertSame("Book2", result.get(1).getName());
        verify(bookService).findAll();
    }
    @Test
    public void TestGetBooksBad(){
        List<Book> books = new ArrayList<>();
        when(bookService.findAll()).thenReturn(books);

        List<Book> result = stockService.getBooks();
        assertTrue(result.isEmpty());
        verify(bookService).findAll();
    }


}
