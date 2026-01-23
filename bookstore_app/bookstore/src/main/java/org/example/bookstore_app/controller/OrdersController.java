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
            .filter(copy -> copy.getIdBook()==book.getId())
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(int id, List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(id, customerName, customerContact);
        for(Book book:bookList){
            createOrderItem(order.getId(),book);
        }
            
        stokService.addOrder(order);
        order.setOrderItems(stokService.getBookOrderItem(order.getId()));
        updateOrderStatus(order);

        calculateTotalPrice(order);
        System.out.println("Создан заказ #" + order.getId() +
                         " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }
   
    private BookOrderItem createOrderItem(int idOrder,Book book) {
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
        BookOrderItem orderItem = new BookOrderItem(id, book.getId());

        if (bookCopy != null) {
            orderItem.setIdBookCopy(bookCopy.getId());
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            stokService.removeBooksCopy(bookCopy);
            if (findBook(book) == null) {
                book.setStatusNo();
            }
        } else {
            int idRequest = stokService.getRequests().isEmpty()? 1: stokService.getRequests().getLast().getId()+1;
            Request request = new Request(idRequest, orderItem.getId());
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
            if (orderItem.getIdBookCopy() != 0) {
                stokService.addBooksCopy(stokService.getBooksCopyByID(orderItem.getIdBookCopy()) );
                stokService.getBooksById(orderItem.getIdBook()).setStatusStok();
            }
            stokService.getRequests().removeIf(request ->
                    stokService.getBookOrderItemByID(request.getIdOrderItem()).equals(orderItem));
        });
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        order.getOrderItems().stream()
            .filter(item -> item.getIdBook()==book.getId())
            .findFirst()
            .ifPresent(itemToRemove -> {
                if (itemToRemove.getIdBookCopy() != 0) {
                    stokService.addBooksCopy(itemToRemove.getIdBookCopy());
                    itemToRemove.getIdBook().setStatusStok();
                }
                stokService.getRequests().removeIf(request ->
                        stokService.getBookOrderItemByID(request.getIdOrderItem()).equals(itemToRemove));
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
        List<BookOrderItem> orderItems = order.getOrderItems();
        double price=0;
        for(BookOrderItem item:orderItems){
            price += stokService.getBooksById(item.getIdBook()).getPrice();
        }
        order.setTotalPrice(price);
    }

}