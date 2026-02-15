package org.example.bookstore_app.controller;


import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StockService;
import org.example.bookstore_app.model.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShowOrdersAndRequests implements IShowOrdersAndRequests {
    @Inject
    StockService stockService;

    public ShowOrdersAndRequests() { }

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
        List<BookOrder> sortedOrders = stockService.getOrders().stream()
                .sorted(Comparator.comparing(BookOrder::getTotalPrice))
                .toList();

        System.out.println("Заказы по цене:");
        sortedOrders.forEach(order ->
                System.out.println(" - " + order.getId() + " | " +
                        order.getTotalPrice() + " руб. | " + order.getStatus()));
    }

    @Override
    public void showOrdersByStatus() {
        List<BookOrder> sortedOrders = stockService.getOrders().stream()
                .sorted(Comparator.comparing(BookOrder::getStatus))
                .toList();

        System.out.println("Заказы по статусу:");
        sortedOrders.forEach(order ->
                System.out.println(" - " + order.getId() + " | " + order.getStatus() +
                        " | " + stockService.getBookOrderItemByidOrder(order.getId()).size() + " книг(и)"));
    }

    @Override
    public void showRequestsByCount() {
        Map<Book, Long> requestCount = stockService.getRequests().stream()
                .collect(Collectors.groupingBy(
                        request -> request.getOrderItem().getBook(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        requestCount.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(),
                                                                      e1.getValue()))
                .forEach(item ->
                        System.out.println(" - " + item.getKey().getName() +
                                " - " + item.getValue() + " запросов"));
    }

    @Override
    public void showRequestsByAlphabet() {
        Map<Book, Long> requestedBooks = stockService.getRequests().stream()
                .collect(Collectors.groupingBy(
                        request -> {
                            BookOrderItem orderItem = request.getOrderItem();
                            return orderItem != null ? orderItem.getBook()  : null;
                        },
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        requestedBooks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Book::getName)))
                .forEach(entry ->
                        System.out.println(" - " + entry.getKey().getName() +
                                " - " + entry.getValue() + " запросов"));
    }

    @Override
    public List<BookOrder> getCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        return stockService.getOrders().stream()
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

        stockService.getBookOrderItemByidOrder(order.getId()).forEach(item ->
                System.out.println(" - " + item.getBook().getName() +
                        " | " + item.getStatus() + " | " +
                        item.getBook().getPrice() + " руб."));
    }

    @Override
    public List<BookOrder> sortOrderByDate(){
        return stockService.getOrders().stream()
                .sorted(Comparator.comparing(BookOrder::getOrderDate))
                .collect(Collectors.toList());
    }

}
