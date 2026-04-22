package testforservices;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
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
public class TestStokServiceBooksCopy {
    @Mock
    private IBookCopyService bookCopyService;
    @InjectMocks
    private StockService stockService;

    Book book1;
    Book book2;
    BookCopy bookCopy1;
    BookCopy bookCopy2;


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

        bookCopy1= new BookCopy();
        bookCopy1.setBook(book1);

        bookCopy2 = new BookCopy();
        bookCopy2.setBook(book2);
    }

    @Test
    public void TestGetBooksCopy(){
        List<BookCopy> booksCopy = Arrays.asList(bookCopy1,bookCopy2);
        when(bookCopyService.findAll()).thenReturn(booksCopy);

        List<BookCopy> result = stockService.getBooksCopy();
        assertEquals(2, result.size());
        assertSame("Book1", result.getFirst().getBook().getName());
        assertSame("Book2", result.get(1).getBook().getName());
        verify(bookCopyService).findAll();
    }
    @Test
    public void TestGetBooksCopyBad(){
        List<BookCopy> booksCopy = new ArrayList<>();
        when(bookCopyService.findAll()).thenReturn(booksCopy);

        List<BookCopy> result = stockService.getBooksCopy();
        assertTrue(result.isEmpty());
        verify(bookCopyService).findAll();
    }
}
