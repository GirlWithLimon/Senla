package first.controller;

import first.model.*;
import java.time.LocalDate;
import java.util.List;

public class OperationController {
    private final  Operation operation;
    private final  Stok stok;

    public OperationController() {
        this.stok = new Stok();
        this.operation = new Operation(stok, stok, stok);
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
        operation.showBooksByABC();
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
       operation.showCompletedOrdersByPeriod(start, end);
    }
    
    public void showOldBooksSortedByDate() {
        operation.showOldBooksSortedByDate();
    }
    
    public void showOldBooksSortedByPrice() {
        operation.showOldBooksSortedByPrice();
    }
    
    
}