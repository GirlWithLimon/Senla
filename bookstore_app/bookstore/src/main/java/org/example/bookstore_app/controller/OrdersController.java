package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StokService;
import org.example.bookstore_app.model.*;

import java.util.List;

@Component
public class OrdersController implements IOrderOperation{
    @Inject
    StokService stokService;
    
    public OrdersController() {  }

    private BookCopy findBook(Book book) {
        return stokService.getBooksCopy().stream()
            .filter(copy -> copy.getBook().equals(book))
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(int id, List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(id, customerName, customerContact);
        bookList.stream()
            .map(this::createOrderItem)
            .forEach(order::addBookToOrder);
            
        stokService.addOrder(order);
        updateOrderStatus(order);

        calculateTotalPrice(order);
        System.out.println("Создан заказ #" + order.getId() +
                         " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }
   
    private BookOrderItem createOrderItem(Book book) {
        BookCopy bookCopy = findBook(book);
        int id;
        if(stokService.getOrders().isEmpty()){
            id = 1;
        } else {
            boolean anyOrderHasBooks = stokService.getOrders().stream()
                    .anyMatch(order -> !order.getOrderItems().isEmpty());

            if (anyOrderHasBooks) {
               id = stokService.getOrders().stream()
                        .flatMap(order -> order.getOrderItems().stream())
                        .mapToInt(BookOrderItem::getId)
                        .max()
                        .orElse(0) + 1;
            } else {
                id = 1;
            }
        }
        BookOrderItem orderItem = new BookOrderItem(id, book);

        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            stokService.removeBooksCopy(bookCopy);
            if (findBook(book) == null) {
                book.setStatusNo();
            }
        } else {
            int idRequest = stokService.getRequests().isEmpty()? 1: stokService.getRequests().getLast().getId()+1;
            Request request = new Request(idRequest, orderItem);
            stokService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
        }
        return orderItem;
    }
    
    @Override
    public void cancelOrder(BookOrder order) {
        stokService.removeOrder(order);
        order.setStatus(OrderStatus.CANCELLED);
        
        order.getOrderItems().forEach(orderItem -> {
            if (orderItem.getBookCopy() != null) {
                stokService.addBooksCopy(orderItem.getBookCopy());
                orderItem.getBook().setStatusStok();
            }
            stokService.getRequests().removeIf(request ->
                request.getOrderItem().equals(orderItem));
        });
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        order.getOrderItems().stream()
            .filter(item -> item.getBook().equals(book))
            .findFirst()
            .ifPresent(itemToRemove -> {
                if (itemToRemove.getBookCopy() != null) {
                    stokService.addBooksCopy(itemToRemove.getBookCopy());
                    itemToRemove.getBook().setStatusStok();
                }
                stokService.getRequests().removeIf(request ->
                    request.getOrderItem().equals(itemToRemove));
                order.getOrderItems().remove(itemToRemove);
                updateOrderStatus(order);
            });
        calculateTotalPrice(order);
    }
    
    private void updateOrderStatus(BookOrder order) {
        long completedCount = order.getOrderItems().stream()
            .filter(item -> OrderItemStatus.COMPLETED.equals(item.getStatus()))
            .count();
        
        long pendingCount = order.getOrderItems().stream()
            .filter(item -> OrderItemStatus.PENDING.equals(item.getStatus()))
            .count();
        
        long totalCount = order.getOrderItems().size();
        
        if (completedCount == totalCount) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (pendingCount > 0) {
            order.setStatus(OrderStatus.PARTIALLY_COMPLETED);
        } else if (completedCount > 0) {
            order.setStatus(OrderStatus.IN_PROCESS);
        } else {
            order.setStatus(OrderStatus.NEW);
        }
    }
    private void calculateTotalPrice(BookOrder order) {
        double price = order.getOrderItems().stream()
                            .mapToDouble(BookOrderItem::getPrice)
                            .sum();
        order.setTotalPrice(price);
    }

}