package testforcontrollers;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.controller.ShowBook;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.service.StockService;
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
public class TestShowBook {
    @Mock
    private BookstoreConfig config;
    @Mock
    private StockService stockService;
    @InjectMocks
    private ShowBook showBook;
    @Test
    public void getOldBooksSortedByDateGood(){
        LocalDate now = LocalDate.now();
        when(config.getMonthsForOldBook()).thenReturn(6);
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookOld1 = new Book();
        testBookOld1.setId(1);
        testBookOld1.setName("Test old Book1");
        testBookOld1.setPrice(10.0);

        Book testBookOld2 = new Book();
        testBookOld2.setId(2);
        testBookOld2.setName("Test old Book2");
        testBookOld2.setPrice(15.0);

        Book testBookNew = new Book();
        testBookNew.setId(3);
        testBookNew.setName("Test new Book");
        testBookNew.setPrice(25.0);

        BookCopy oldBook1 = new BookCopy();
        oldBook1.setId(1);
        oldBook1.setBook(testBookOld1);
        oldBook1.setArrivalDate(thresholdDate.minusDays(10));
        oldBook1.setSale(false);

        BookCopy oldBook2 = new BookCopy();
        oldBook2.setId(2);
        oldBook2.setBook(testBookOld2);
        oldBook2.setArrivalDate(thresholdDate.minusDays(5));
        oldBook2.setSale(false);

        BookCopy newBook = new BookCopy();
        newBook.setId(3);
        newBook.setBook(testBookNew);
        newBook.setArrivalDate(thresholdDate.plusDays(1));
        newBook.setSale(false);

        List<BookCopy> allCopies = Arrays.asList(oldBook1, oldBook2,newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByDate();
        assertEquals(2, result.size()); //проверка, что 2 книги
        //проверка, что книга с ценой ниже - выше в списке :)
        assertTrue(result.get(0).getArrivalDate().isBefore(result.get(1).getArrivalDate()));
        //проверка, что книги старше 6 месяцев
        assertTrue(result.get(0).getArrivalDate().isBefore(thresholdDate));
        assertTrue(result.get(1).getArrivalDate().isBefore(thresholdDate));

        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config).getMonthsForOldBook();

    }
    @Test
    public void getOldBooksSortedByDateBad(){
        LocalDate now = LocalDate.now();
        when(config.getMonthsForOldBook()).thenReturn(6);
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookNew = new Book();
        testBookNew.setId(3);
        testBookNew.setName("Test new Book");
        testBookNew.setPrice(25.0);

        BookCopy newBook = new BookCopy();
        newBook.setId(3);
        newBook.setBook(testBookNew);
        newBook.setArrivalDate(thresholdDate.plusDays(1));
        newBook.setSale(false);

        List<BookCopy> allCopies = Arrays.asList(newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByDate();

        assertTrue(result.isEmpty()); //проверка, что нет книг
        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config).getMonthsForOldBook();

    }
   @Test
   public void getOldBooksSortedByPriceGood(){
        LocalDate now = LocalDate.now();
        when(config.getMonthsForOldBook()).thenReturn(6);
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookOld1 = new Book();
        testBookOld1.setId(1);
        testBookOld1.setName("Test old Book1");
        testBookOld1.setPrice(10.0);

        Book testBookOld2 = new Book();
        testBookOld2.setId(2);
        testBookOld2.setName("Test old Book2");
        testBookOld2.setPrice(15.0);

        Book testBookNew = new Book();
        testBookNew.setId(3);
        testBookNew.setName("Test new Book");
        testBookNew.setPrice(25.0);

        BookCopy oldBook1 = new BookCopy();
        oldBook1.setId(1);
        oldBook1.setBook(testBookOld1);
        oldBook1.setArrivalDate(thresholdDate.minusDays(10));
        oldBook1.setSale(false);

        BookCopy oldBook2 = new BookCopy();
        oldBook2.setId(2);
        oldBook2.setBook(testBookOld2);
        oldBook2.setArrivalDate(thresholdDate.minusDays(5));
        oldBook2.setSale(false);

        BookCopy newBook = new BookCopy();
        newBook.setId(3);
        newBook.setBook(testBookNew);
        newBook.setArrivalDate(thresholdDate.plusDays(1));
        newBook.setSale(false);

        List<BookCopy> allCopies = Arrays.asList(oldBook1, oldBook2,newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByPrice();
        assertEquals(2, result.size()); //проверка, что 2 книги
        //проверка, что книга с ценой ниже - выше в списке :)
        assertTrue(result.get(0).getBook().getPrice() < (result.get(1).getBook().getPrice()));
        //проверка, что книги старше 6 месяцев
        assertTrue(result.get(0).getArrivalDate().isBefore(thresholdDate));
        assertTrue(result.get(1).getArrivalDate().isBefore(thresholdDate));

        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config).getMonthsForOldBook();

    }
    @Test
    public void getOldBooksSortedByPriceBad(){
        LocalDate now = LocalDate.now();
        when(config.getMonthsForOldBook()).thenReturn(6);
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookNew = new Book();
        testBookNew.setId(3);
        testBookNew.setName("Test new Book");
        testBookNew.setPrice(25.0);

        BookCopy newBook = new BookCopy();
        newBook.setId(3);
        newBook.setBook(testBookNew);
        newBook.setArrivalDate(thresholdDate.plusDays(1));
        newBook.setSale(false);

        List<BookCopy> allCopies = Arrays.asList(newBook);
        when(stockService.getBooksCopy()).thenReturn(allCopies);

        List<BookCopy> result = showBook.getOldBooksSortedByPrice();

        assertTrue(result.isEmpty()); //проверка, что нет книг
        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config).getMonthsForOldBook();

    }

    //поиск старых книг
    @Test
    public void showOldBooksByDateGood(){
        LocalDate now = LocalDate.now();
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookOld1 = new Book();
        testBookOld1.setId(1);
        testBookOld1.setName("Test old Book1");
        testBookOld1.setPrice(10.0);

        Book testBookOld2 = new Book();
        testBookOld2.setId(2);
        testBookOld2.setName("Test old Book2");
        testBookOld2.setPrice(15.0);

        BookCopy oldBook1 = new BookCopy();
        oldBook1.setId(1);
        oldBook1.setBook(testBookOld1);
        oldBook1.setArrivalDate(thresholdDate.minusDays(10));
        oldBook1.setSale(false);

        BookCopy oldBook2 = new BookCopy();
        oldBook2.setId(2);
        oldBook2.setBook(testBookOld2);
        oldBook2.setArrivalDate(thresholdDate.minusDays(5));
        oldBook2.setSale(false);

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
        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config, atLeast(2)).getMonthsForOldBook();

    }
    @Test
    public void showOldBooksByDateBad(){
        LocalDate now = LocalDate.now();
        LocalDate thresholdDate = now.minusMonths(6);

        Book testBookNew1 = new Book();
        testBookNew1.setId(1);
        testBookNew1.setName("Test new Book1");
        testBookNew1.setPrice(10.0);

        Book testBookNew2 = new Book();
        testBookNew2.setId(2);
        testBookNew2.setName("Test new Book2");
        testBookNew2.setPrice(15.0);

        BookCopy newBook1 = new BookCopy();
        newBook1.setId(1);
        newBook1.setBook(testBookNew1);
        newBook1.setArrivalDate(thresholdDate.plusDays(10));
        newBook1.setSale(false);

        BookCopy newBook2 = new BookCopy();
        newBook2.setId(2);
        newBook2.setBook(testBookNew2);
        newBook2.setArrivalDate(thresholdDate.plusDays(5));
        newBook2.setSale(false);

        List<BookCopy> newBooks = Arrays.asList(newBook1, newBook2);
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
        //проверка вообще что были вызваны
        verify(stockService).getBooksCopy();
        verify(config, atLeast(2)).getMonthsForOldBook();

    }
}
