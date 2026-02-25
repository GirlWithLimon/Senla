package org.example.bookstore_app.controller;


import org.example.bookstore_app.model.*;
import org.example.bookstore_app.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OrdersController implements IOrderOperation{
    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    StockService stockService;
    
    public OrdersController() {  }

    private BookCopy findBook(Book book) {
        return stockService.getBookCopyByBookId(book.getId()).stream()
            .filter(copy -> !copy.getSale())
            .findFirst()
            .orElse(null);
    }


    public BookOrder createOrder(List<Book> bookList,
                                 String customerName,
                                 String customerContact) {
        BookOrder order = new BookOrder(0, customerName, customerContact);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(0);
        BookOrder orderN = stockService.addOrder(order);
        for(Book book:bookList){
            createOrderItem(orderN,book);
        }
        updateOrderStatus(orderN);
        stockService.updateOrder(orderN);
        calculateTotalPrice(orderN);
        System.out.println("Создан заказ #" + orderN.getId()
                +" на " + bookList.size() + " книг(и) с итоговой ценой: "
                + orderN.getTotalPrice());
        return orderN;
    }

    @Override
    public BookOrderItem createOrderItem(BookOrder order, Book book) {
        //если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(0, book,order);
        orderItem = stockService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + book.getName());
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stockService.updateBook(book);
            }
            stockService.updateBookOrderItem(orderItem);
            stockService.updateBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem);
            request = stockService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stockService.updateBookOrderItem(orderItem);
        }
        return orderItem;
    }

    @Override
    public void cancelOrder(int idOrder) {
        BookOrder order = stockService.getOrderByID(idOrder);
        if (order == null) {
            System.out.println("Заказ с ID " + idOrder + " не найден!");
            return;
        }

        // ИСПРАВЛЕНО: используем метод с загрузкой всех данных
        List<BookOrderItem> orderItems = stockService.findByOrderIdWithAllData(order.getId());

        for (BookOrderItem item : orderItems) {
            // 1. Сначала находим request (уже загружен)
            Request request = stockService.getRequestsByidOrderItem(item.getId()); // ← уже не нужно вызывать отдельный метод

            // 2. Обновляем статус книги, если есть копия
            BookCopy bookCopy = item.getBookCopy();  // ← уже загружено
            if (bookCopy != null) {
                bookCopy.setSale(false);
                stockService.updateBooksCopy(bookCopy);

                Book book = item.getBook();  // ← уже загружено
                if (book != null) {
                    if (stockService.findCountByIdBook(book.getId()) > 0) {
                        book.setStatusStok();
                    } else {
                        book.setStatusNo();
                    }
                    stockService.updateBook(book);
                }
            }

            // 3. Удаляем request (если есть)
            if (request != null) {
                stockService.removeRequest(request);
            }

            // 4. ТЕПЕРЬ можно удалить orderItem
            stockService.removeOrderItem(item);
        }

        stockService.removeOrder(order);
        System.out.println("Заказ #" + idOrder + " успешно отменен!");
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        BookOrderItem item = stockService.getBookOrderItemByidOrder(order.getId()).stream()
                             .filter(item1 -> item1.getBook().getId()== book.getId()).findFirst().orElse(null);
        if (item != null){
            Request request = stockService.getRequestsByidOrderItem(item.getId());
            if (request != null) {
                stockService.removeRequest(request);
            }
            BookCopy bookCopy = item.getBookCopy();
            if (bookCopy != null) {
                bookCopy.setSale(false);
            }
            stockService.removeOrderItem(item);
            calculateTotalPrice(item.getOrder());
            updateOrderStatus(item.getOrder());
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
       double price = stockService.findSumByIdOrder(order.getId());
        order.setTotalPrice(price);
        stockService.updateOrder(order);
    }


}
