package testforcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.bookstore_app.controller.rest.BookRestController;
import org.example.bookstore_app.dto.BookRequestDTO;
import org.example.bookstore_app.exception.GlobalExceptionHandler;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class TestBookRestController {
    MockMvc mockMvc;
    @Mock
    private StockService stockService;
    @InjectMocks
    private BookRestController bookRestController;

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

        mockMvc = MockMvcBuilders.
                standaloneSetup(bookRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void getAllBooksGood() throws Exception{
        List<Book> books = Arrays.asList(book1, book2);
        when(stockService.getBooks()).thenReturn(books);
       MvcResult mvcResult = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Book1"));
        assertTrue(jsonResponse.contains("Book2"));
        assertTrue(jsonResponse.contains("Author1"));
    }
    @Test
    public void getAllBooksBad() throws Exception{
        List<Book> books =  new ArrayList<>();
        when(stockService.getBooks()).thenReturn(books);
        MvcResult mvcResult = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertFalse(jsonResponse.contains("Book1"));
        assertFalse(jsonResponse.contains("Book2"));
        assertFalse(jsonResponse.contains("Author1"));
    }

    @Test
    public void getOneBookGood() throws Exception{
        when(stockService.getBooksById(1)).thenReturn(book1);
        MvcResult mvcResult = mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Book1"));
        assertTrue(jsonResponse.contains("Author1"));
        assertFalse(jsonResponse.contains("Book2"));
        assertFalse(jsonResponse.contains("Author2"));
    }

    @Test
    public void getOneBookBad() throws Exception{
        when(stockService.getBooksById(1)).thenReturn(null);
        MvcResult mvcResult = mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Книга с ID 1 не найдена"));
        assertFalse(jsonResponse.contains("Book1"));
        assertFalse(jsonResponse.contains("Author1"));
        assertFalse(jsonResponse.contains("Book2"));
        assertFalse(jsonResponse.contains("Author2"));
    }

    @Test
    public void postBookGood() throws Exception{
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setName("Book1");
        bookRequest.setAuthor("Author1");
        bookRequest.setPrice(40.0);
        bookRequest.setPublicationDate(LocalDate.now());
        bookRequest.setInformation("Test book description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Book1"));
        assertTrue(jsonResponse.contains("Author1"));
        verify(stockService).addBook(any());
    }
    @Test
    public void postBookBad() throws Exception{
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setAuthor("Author1");
        bookRequest.setPrice(40.0);
        bookRequest.setPublicationDate(LocalDate.now());
        bookRequest.setInformation("Test book description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertFalse(jsonResponse.contains("Book1"));
        assertFalse(jsonResponse.contains("Author1"));
        verify(stockService, never()).addBook(any());
    }

    @Test
    public void putBookGood() throws Exception{
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setName("Book1");
        bookRequest.setAuthor("Author1");
        bookRequest.setPrice(40.0);
        bookRequest.setPublicationDate(LocalDate.now());
        bookRequest.setInformation("Test book description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        when(stockService.getBooksById(1)).thenReturn(book1);

        MvcResult mvcResult = mockMvc.perform(put("/api/books/1", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Book1"));
        assertTrue(jsonResponse.contains("Author1"));
        verify(stockService).getBooksById(anyInt());
        verify(stockService).updateBook(any());
    }
    @Test
    public void putBookBadNotHaveSoBook() throws Exception{
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setName("Book1");
        bookRequest.setAuthor("Author1");
        bookRequest.setPrice(40.0);
        bookRequest.setPublicationDate(LocalDate.now());
        bookRequest.setInformation("Test book description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        when(stockService.getBooksById(1)).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(put("/api/books/1", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertFalse(jsonResponse.contains("Book1"));
        assertFalse(jsonResponse.contains("Author1"));
        verify(stockService).getBooksById(anyInt());
        verify(stockService, never()).updateBook(any());
    }
    @Test
    public void putBookBadNotHaveName() throws Exception{
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setAuthor("Author1");
        bookRequest.setPrice(40.0);
        bookRequest.setPublicationDate(LocalDate.now());
        bookRequest.setInformation("Test book description");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        MvcResult mvcResult = mockMvc.perform(put("/api/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertFalse(jsonResponse.contains("Book1"));
        assertFalse(jsonResponse.contains("Author1"));
        verify(stockService, never()).getBooksById(anyInt());
        verify(stockService, never()).updateBook(any());
    }

    @Test
    public void deleteBookGood() throws Exception{

        when(stockService.getBooksById(1)).thenReturn(book1);
        MvcResult mvcResult = mockMvc.perform(delete("/api/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        verify(stockService).getBooksById(anyInt());
        verify(stockService).removeBook(any());
    }
    @Test
    public void deleteBookBad() throws Exception{

        when(stockService.getBooksById(1)).thenReturn(null);
        MvcResult mvcResult = mockMvc.perform(delete("/api/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(stockService).getBooksById(anyInt());
        verify(stockService, never()).removeBook(any());
    }
}
