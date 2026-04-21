package testforcontrollers;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.controller.ShowBook;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestShowBookWithOldDate {
    @Mock
    private BookstoreConfig config;
    @Mock
    private StockService stockService;
    @InjectMocks
    private ShowBook showBook;

    Book testBookOld1 = new Book();
    Book testBookOld2 = new Book();
    Book testBookNew = new Book();
    Book testBookNew2 = new Book();

    BookCopy oldBook1 = new BookCopy();
    BookCopy oldBook2 = new BookCopy();
    BookCopy newBook = new BookCopy();
    BookCopy newBook2 = new BookCopy();

    LocalDate thresholdDate = LocalDate.now().minusMonths(6);

    @BeforeEach
    public void takeBooks(){
        testBookOld1.setId(1);
        testBookOld1.setName("Test old Book1");
        testBookOld1.setPrice(10.0);

        testBookOld2.setId(2);
        testBookOld2.setName("Test old Book2");
        testBookOld2.setPrice(15.0);

        testBookNew.setId(3);
        testBookNew.setName("Test new Book");
        testBookNew.setPrice(25.0);

        testBookNew2.setId(2);
        testBookNew2.setName("Test new Book2");
        testBookNew2.setPrice(15.0);

        oldBook1.setId(1);
        oldBook1.setBook(testBookOld1);
        oldBook1.setArrivalDate(thresholdDate.minusDays(10));
        oldBook1.setSale(false);

        oldBook2.setId(2);
        oldBook2.setBook(testBookOld2);
        oldBook2.setArrivalDate(thresholdDate.minusDays(5));
        oldBook2.setSale(false);

        newBook.setId(3);
        newBook.setBook(testBookNew);
        newBook.setArrivalDate(thresholdDate.plusDays(1));
        newBook.setSale(false);

        newBook2.setId(2);
        newBook2.setBook(testBookNew2);
        newBook2.setArrivalDate(thresholdDate.plusDays(5));
        newBook2.setSale(false);
    }


    @AfterEach
    public void aboutVisov(){
        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config, atLeastOnce()).getMonthsForOldBook();
    }
    @Test
    public void getOldBooksSortedByDateGood(){
       when(config.getMonthsForOldBook()).thenReturn(6);
        List<BookCopy> allCopies = Arrays.asList(oldBook1, oldBook2,newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByDate();
        assertEquals(2, result.size()); //проверка, что 2 книги
        //проверка, что книга с ценой ниже - выше в списке :)
        assertTrue(result.get(0).getArrivalDate().isBefore(result.get(1).getArrivalDate()));
        //проверка, что книги старше 6 месяцев
        assertTrue(result.get(0).getArrivalDate().isBefore(thresholdDate));
        assertTrue(result.get(1).getArrivalDate().isBefore(thresholdDate));

    }
    @Test
    public void getOldBooksSortedByDateBad(){
        when(config.getMonthsForOldBook()).thenReturn(6);
        List<BookCopy> allCopies = Arrays.asList(newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);
        List<BookCopy> result = showBook.getOldBooksSortedByDate();
        assertTrue(result.isEmpty()); //проверка, что нет книг

    }
   @Test
   public void getOldBooksSortedByPriceGood(){
        when(config.getMonthsForOldBook()).thenReturn(6);
        List<BookCopy> allCopies = Arrays.asList(oldBook1, oldBook2,newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByPrice();
        assertEquals(2, result.size()); //проверка, что 2 книги
        //проверка, что книга с ценой ниже - выше в списке :)
        assertTrue(result.get(0).getBook().getPrice() < (result.get(1).getBook().getPrice()));
        //проверка, что книги старше 6 месяцев
        assertTrue(result.get(0).getArrivalDate().isBefore(thresholdDate));
        assertTrue(result.get(1).getArrivalDate().isBefore(thresholdDate));
    }
    @Test
    public void getOldBooksSortedByPriceBad(){
        when(config.getMonthsForOldBook()).thenReturn(6);
        List<BookCopy> allCopies = Arrays.asList(newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByPrice();

        assertTrue(result.isEmpty()); //проверка, что нет книг
    }

    //поиск старых книг
    @Test
    public void showOldBooksByDateGood(){

        List<BookCopy> oldBooks = Arrays.asList(oldBook1, oldBook2);
        when(config.getMonthsForOldBook()).thenReturn(6);
        when(stockService.getBooksCopy()).thenReturn(oldBooks);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showOldBooksByDate();

        String output = outContent.toString();
        assertTrue(output.contains("Залежавшиеся книги (более 6 месяцев)"));
        assertTrue(output.contains(" - 1 | Поступление: "));
        assertTrue(output.contains(" | Цена: 10.0 руб." ));
        assertTrue(output.contains(" - 2 | Поступление: "));
        assertTrue(output.contains(" | Цена: 15.0 руб." ));
        assertFalse(output.contains("Нет залежавшихся книг"));
        System.setOut(System.out);
    }
    @Test
    public void showOldBooksByDateBad(){
        List<BookCopy> newBooks = Arrays.asList(newBook, newBook2);
        when(config.getMonthsForOldBook()).thenReturn(6);
        when(stockService.getBooksCopy()).thenReturn(newBooks);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showOldBooksByDate();

        String output = outContent.toString();
        assertTrue(output.contains("Залежавшиеся книги (более 6 месяцев)"));
        assertFalse(output.contains(" - 1 | Поступление: "));
        assertFalse(output.contains(" | Цена: 10.0 руб." ));
        assertFalse(output.contains(" - 2 | Поступление: "));
        assertFalse(output.contains(" | Цена: 15.0 руб." ));
        assertTrue(output.contains("Нет залежавшихся книг"));
        System.setOut(System.out);
    }

    @Test
    public void showOldBooksByPriceGood(){

        List<BookCopy> oldBooks = Arrays.asList(oldBook1, oldBook2);
        when(config.getMonthsForOldBook()).thenReturn(6);
        when(stockService.getBooksCopy()).thenReturn(oldBooks);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showOldBooksByPrice();

        String output = outContent.toString();
        assertTrue(output.contains("Залежавшиеся книги (более 6 месяцев)"));
        assertTrue(output.contains(" - 1 | Поступление: "));
        assertTrue(output.contains(" | Цена: 10.0 руб." ));
        assertTrue(output.contains(" - 2 | Поступление: "));
        assertTrue(output.contains(" | Цена: 15.0 руб." ));
        assertFalse(output.contains("Нет залежавшихся книг"));
        System.setOut(System.out);
    }
    @Test
    public void showOldBooksByPriceBad(){
        List<BookCopy> newBooks = Arrays.asList(newBook, newBook2);
        when(config.getMonthsForOldBook()).thenReturn(6);
        when(stockService.getBooksCopy()).thenReturn(newBooks);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        showBook.showOldBooksByPrice();

        String output = outContent.toString();
        assertTrue(output.contains("Залежавшиеся книги (более 6 месяцев)"));
        assertFalse(output.contains(" - 1 | Поступление: "));
        assertFalse(output.contains(" | Цена: 10.0 руб." ));
        assertFalse(output.contains(" - 2 | Поступление: "));
        assertFalse(output.contains(" | Цена: 15.0 руб." ));
        assertTrue(output.contains("Нет залежавшихся книг"));
        System.setOut(System.out);
    }

}
