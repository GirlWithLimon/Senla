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
        return stokService.getBookCopyByBookId(book.getId()).stream()
            .filter(copy -> !copy.getSale())
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(int id, List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(id, customerName, customerContact);
        stokService.addOrder(order);
//        for(Book book:bookList){
//            createOrderItem(0, order.getId(),book);
//        }
//        updateOrderStatus(order);
//        calculateTotalPrice(order);
        System.out.println("Выведен из БД заказ #" + order.getId() +
                         " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }

    public BookOrder createNewOrder( List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(0, customerName, customerContact);
        stokService.addOrder(order);
        for(Book book:bookList){
            createOrderItem(0, order.getId(),book);
        }
        updateOrderStatus(order);
        calculateTotalPrice(order);
        System.out.println("Создан заказ #" + order.getId() +
                " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }
   
    private BookOrderItem createOrderItem(int idOrder,Book book) {//если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(id, book.getId(),idOrder);
        orderItem = stokService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setIdBookCopy(bookCopy.getId());
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stokService.addBook(book);
            }
            stokService.addBookOrderItem(orderItem);
            stokService.addBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem.getId());
            request = stokService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stokService.addBookOrderItem(orderItem);
            stokService.addRequest(request);
        }
        return orderItem;
    }
    private BookOrderItem createOrderItem(int id, int idOrder,Book book) {//если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(id, book.getId(),idOrder);
        orderItem = stokService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setIdBookCopy(bookCopy.getId());
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stokService.addBook(book);
            }
            stokService.addBookOrderItem(orderItem);
            stokService.addBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem.getId());
            request = stokService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stokService.addBookOrderItem(orderItem);
            stokService.addRequest(request);
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