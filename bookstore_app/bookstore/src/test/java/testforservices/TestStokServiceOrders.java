package testforservices;

import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.model.BookOrder;
import org.example.bookstore_app.service.IBookCopyService;
import org.example.bookstore_app.service.IOrderService;
import org.example.bookstore_app.service.StockService;
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
public class TestStokServiceOrders {
    @Mock
    private IOrderService orderService;
    @InjectMocks
    private StockService stockService;

    BookOrder order1;
    BookOrder order2;


    @BeforeEach
    public void books(){
        order1 = new BookOrder();
        order1.setId(1);
        order1.setCustomerContact("7-920");
        order1.setCustomerName("Name1");

        order2 = new BookOrder();
        order2.setId(1);
        order2.setCustomerContact("7-922");
        order2.setCustomerName("Name2");
    }

    @Test
    public void TestGetOrders(){
        List<BookOrder> booksOrder = Arrays.asList(order1,order2);
        when(orderService.findAll()).thenReturn(booksOrder);

        List<BookOrder> result = stockService.getOrders();
        assertEquals(2, result.size());
        assertSame("Name1", result.getFirst().getCustomerName());
        assertSame("Name2", result.get(1).getCustomerName());
        verify(orderService).findAll();
    }
    @Test
    public void TestGetOrdersBad(){
        List<BookOrder> orders = new ArrayList<>();
        when(orderService.findAll()).thenReturn(orders);

        List<BookOrder> result = stockService.getOrders();
        assertTrue(result.isEmpty());
        verify(orderService).findAll();
    }
}
