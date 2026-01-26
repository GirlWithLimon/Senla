package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StockService;
import org.example.bookstore_app.model.*;

import java.util.List;

@Component
public class OrdersController implements IOrderOperation{
    @Inject
    StockService stockService;
    
    public OrdersController() {  }

    private BookCopy findBook(Book book) {
        return stockService.getBookCopyByBookId(book.getId()).stream()
            .filter(copy -> !copy.getSale())
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(int id, List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(id, customerName, customerContact);
        stockService.addOrder(order);
        System.out.println("Выведен из БД заказ #" + order.getId() +
                         " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }

    public BookOrder createNewOrder( List<Book> bookList, String customerName, String customerContact) {
        BookOrder order = new BookOrder(0, customerName, customerContact);
        stockService.addOrder(order);
        for(Book book:bookList){
            createNewOrderItem( order.getId(),book);
        }
        updateOrderStatus(order);
        calculateTotalPrice(order);
        System.out.println("Создан заказ #" + order.getId() +
                " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }
    @Override
    public BookOrderItem createOrderItem(int id, int idOrder,Book book) {//если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(id, book.getId(),idOrder);
        orderItem = stockService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setIdBookCopy(bookCopy.getId());
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stockService.addBook(book);
            }
            stockService.addBookOrderItem(orderItem);
            stockService.addBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem.getId());
            request = stockService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stockService.addBookOrderItem(orderItem);
            stockService.addRequest(request);
        }
        return orderItem;
    }

    private BookOrderItem createNewOrderItem(int idOrder,Book book) {//если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(0, book.getId(),idOrder);
        orderItem = stockService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setIdBookCopy(bookCopy.getId());
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stockService.addBook(book);
            }
            stockService.addBookOrderItem(orderItem);
            stockService.addBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem.getId());
            request = stockService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stockService.addBookOrderItem(orderItem);
            stockService.addRequest(request);
        }
        return orderItem;
    }
    
    @Override
    public void cancelOrder(BookOrder order) {
        List<BookOrderItem> orderItems = stockService.getBookOrderItemByidOrder(order.getId());
        for(BookOrderItem item : orderItems){
            Request request = stockService.getRequestsByidOrderItem(item.getId());
            if(request != null){
                 stockService.removeRequest(request);
            }
            BookCopy bookCopy =  stockService.getBooksCopyByID(item.getIdBookCopy());
            if(bookCopy != null){
                bookCopy.setSale(false);
                stockService.addBooksCopy(bookCopy);
            }
            stockService.removeOrderItem(item);
        }
        stockService.removeOrder(order);
        order.setStatus(OrderStatus.CANCELLED);
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        BookOrderItem item = stockService.getBookOrderItemByidOrder(order.getId()).stream()
                             .filter(item1 -> item1.getIdBook()== book.getId()).findFirst().orElse(null);
        if (item != null){
            Request request = stockService.getRequestsByidOrderItem(item.getId());
            if (request != null) {
                stockService.removeRequest(request);
            }
            BookCopy bookCopy = stockService.getBooksCopyByID(item.getIdBookCopy());
            if (bookCopy != null) {
                bookCopy.setSale(false);
                stockService.addBooksCopy(bookCopy);
            }
            stockService.removeOrderItem(item);
            calculateTotalPrice(stockService.getOrderByID(item.getIdOrder()));
            updateOrderStatus(stockService.getOrderByID(item.getIdOrder()));
        }
    }
    
    private void updateOrderStatus(BookOrder order) {
        long completedCount = stockService.getBookOrderItemByidOrder(order.getId()).stream()
            .filter(item -> OrderItemStatus.COMPLETED.equals(item.getStatus()))
            .count();
        
        long pendingCount = stockService.getBookOrderItemByidOrder(order.getId()).stream()
            .filter(item -> OrderItemStatus.PENDING.equals(item.getStatus()))
            .count();
        
        long totalCount = stockService.getBookOrderItemByidOrder(order.getId()).size();
        
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
        List<BookOrderItem> orderItems = stockService.getBookOrderItemByidOrder(order.getId());
        double price=0;
        for(BookOrderItem item:orderItems){
            price += stockService.getBooksById(item.getIdBook()).getPrice();
        }
        order.setTotalPrice(price);
    }

}