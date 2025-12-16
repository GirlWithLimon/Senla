package bookstore_app.project.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bookstore_app.project.model.Book;
import bookstore_app.project.model.BookOrder;
import bookstore_app.project.model.OrderStatus;
import bookstore_app.project.model.Request;
import bookstore_app.project.model.Stok;

public class ShowOrdersAndRequests implements IShowOrdersAndRequests{
    Stok stok;

    public ShowOrdersAndRequests(Stok stok) {
        this.stok = stok;
    }
   
    @Override
    public void showOrdersByDate() {
        List<BookOrder> sortedOrders = sortOrderByDate();
            
        System.out.println("Заказы по дате:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getId() + " | " + 
                             order.getOrderDate() + " | " + order.getStatus()));
    }
    
    @Override
    public void showOrdersByPrice() {
        List<BookOrder> sortedOrders = stok.getOrders().stream()
            .sorted(Comparator.comparing(BookOrder::getTotalPrice))
            .toList();
            
        System.out.println("Заказы по цене:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getId() + " | " + 
                             order.getTotalPrice() + " руб. | " + order.getStatus()));
    }
    
    @Override
    public void showOrdersByStatus() {
        List<BookOrder> sortedOrders = stok.getOrders().stream()
            .sorted(Comparator.comparing(BookOrder::getStatus))
            .toList();
            
        System.out.println("Заказы по статусу:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getId() + " | " + order.getStatus() +
                             " | " + order.getOrderItems().size() + " книг(и)"));
    }
    
    @Override
    public void showRequestsByCount() {
        Map<Book, Long> requestCount = stok.getRequests().stream()
            .collect(Collectors.groupingBy(
                Request::getBook,
                Collectors.counting()
            ));
        
        requestCount.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> 
                System.out.println(" - " + entry.getKey().getName() + 
                             " - " + entry.getValue() + " запросов"));
    }
    
    @Override
    public void showRequestsByAlphabet() {
        Map<Book, Long> requestedBooks = stok.getRequests().stream()
            .collect(Collectors.groupingBy(
                Request::getBook,
                Collectors.counting()
            ));
            
        requestedBooks.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Book::getName)))
            .forEach(entry -> 
                System.out.println(" - " + entry.getKey().getName() + 
                                 " - " + entry.getValue() + " запросов"));
    }
    
    @Override
    public List<BookOrder> getCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        return stok.getOrders().stream()
            .filter(order -> OrderStatus.COMPLETED.equals(order.getStatus()))
            .filter(order -> !order.getOrderDate().isBefore(start) && 
                           !order.getOrderDate().isAfter(end))
            .sorted(Comparator.comparing(BookOrder::getOrderDate))
            .collect(Collectors.toList());
    }
    
    @Override
    public double getEarnedMoneyByPeriod(LocalDate start, LocalDate end) {
        return getCompletedOrdersByPeriod(start, end).stream()
            .mapToDouble(BookOrder::getTotalPrice)
            .sum();
    }
    
    @Override
    public int getCompletedOrdersCountByPeriod(LocalDate start, LocalDate end) {
        return getCompletedOrdersByPeriod(start, end).size();
    }
    
    @Override
    public void showOrderDetails(BookOrder order) {
        System.out.println("=== Детали заказа #" + order.getId() + " ===");
        System.out.println("Клиент: " + order.getCustomerName());
        System.out.println("Контакт: " + order.getCustomerContact());
        System.out.println("Дата: " + order.getOrderDate());
        System.out.println("Статус: " + order.getStatus());
        System.out.println("Общая стоимость: " + order.getTotalPrice() + " руб.");
        System.out.println("Книги в заказе:");
        
        order.getOrderItems().forEach(item -> 
            System.out.println(" - " + item.getBook().getName() + 
                             " | " + item.getStatus() + " | " + 
                             item.getPrice() + " руб."));
    }
    
    @Override
    public List<BookOrder> sortOrderByDate(){
        return stok.getOrders().stream()
                .sorted(Comparator.comparing(BookOrder::getOrderDate))
                .collect(Collectors.toList());
    }

}
