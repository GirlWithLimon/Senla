package first.controller;

import first.model.*;
import java.time.LocalDate;
import java.util.List;

public class OperationController {
    private final Operation operation;
    private final Stok stok;

    public OperationController() {
        this.stok = new Stok();
        this.operation = new Operation(stok, stok, stok);
    }
    
    // тестовыt данные
    public void initializeTestData() {
        Book book1 = new Book("Война и мир", "Л.Н.Толстой", 250.0, LocalDate.of(2014, 10, 24));
        Book book2 = new Book("Мастер и Маргарита", "М.А.Булгаков", 260.0, LocalDate.of(2014, 10, 24));
        Book book3 = new Book("Преступление и наказание", "Ф.М.Достоевский", 200.0, LocalDate.of(2015, 5, 10));
        Book book4 = new Book("1984", "Дж.Оруэлл", 300.0, LocalDate.of(2019, 1, 15));
        
        Book book5 = new Book("Старая книга", "Автор", 150.0, LocalDate.of(2020, 1, 1));
        addBookToStock(book5, LocalDate.now().minusMonths(8));
        
        addBookToStock(book1, LocalDate.now().minusMonths(3));
        addBookToStock(book2, LocalDate.now().minusMonths(7));
        addBookToStock(book1, LocalDate.now().minusMonths(1));
        addBookToStock(book3, LocalDate.now().minusMonths(2));
        addBookToStock(book4, LocalDate.now().minusMonths(1));
        
        List<Book> order1Books = List.of(book1, book2);
        createOrder(order1Books, "Иван Иванов", "ivan@mail.com");
        
        List<Book> order2Books = List.of(book3, book4);
        createOrder(order2Books, "Петр Петров", "petr@mail.com");
        
        System.out.println("Тестовые данные инициализированы!");
    }
        
    public void addBookToStock(Book book, LocalDate date) {
        operation.addBookToStock(book, date);
    }
    
    public void removeBookFromStock(BookCopy bookCopy) {
        operation.removeBookFromStock(bookCopy);
    }
    
    public String showBookInformation(Book book) {
        return operation.showBookInformation(book);
    }
    
    public BookOrder createOrder(List<Book> books, String customerName, String customerContact) {
        return operation.createOrder(books, customerName, customerContact);
    }
    
    public void cancelOrder(BookOrder order) {
        operation.cancelOrder(order);
    }
    
    public void cancelOrderItem(BookOrder order, Book book) {
        operation.cancelOrderItem(order, book);
    }
    
    public void showBooksByABC() {
        operation.showBooksByABC();
    }
    
    public void showBooksByPublicationDate() {
        operation.showBooksByPublicationDate();
    }
    
    public void showBooksByPrice() {
        operation.showBooksByPrice();
    }
    
    public void showBooksByNumberCopies() {
        operation.showBooksByNumberCopies();
    }
    
    public void showOldBooks() {
        operation.showOldBooks();
    }

    public void showRequestsByCount() {
        operation.showRequestsByCount();
    }
    
    public void showRequestsByAlphabet() {
        operation.showRequestsByAlphabet();
    }
    
    public void showOrderDetails(BookOrder order) {
        operation.showOrderDetails(order);
    }
    
    public void showOrdersByDate() {
        operation.showOrdersByDate();
    }
    
    public void showOrdersByPrice() {
        operation.showOrdersByPrice();
    }
    
    public void showOrdersByStatus() {
        operation.showOrdersByStatus();
    }
    
    public void showCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
       operation.showCompletedOrdersByPeriod(start, end);
    }
    
    public void showEarnedMoneyByPeriod(LocalDate start, LocalDate end) {
       operation.showEarnedMoneyByPeriod(start, end);
    }
    
    public void showCompletedOrdersCountByPeriod(LocalDate start, LocalDate end) {
       operation.showCompletedOrdersCountByPeriod(start, end);
    }
    
    public void showOldBooksSortedByDate() {
        operation.showOldBooksSortedByDate();
    }
    
    public void showOldBooksSortedByPrice() {
        operation.showOldBooksSortedByPrice();
    }
    
    public List<Book> getAllBooks() {
        return stok.getBooks();
    }
    
    public List<BookOrder> getAllOrders() {
        return stok.getOrders();
    }
}