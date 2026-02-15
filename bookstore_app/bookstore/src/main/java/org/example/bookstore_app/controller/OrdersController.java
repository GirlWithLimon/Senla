package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StockService;
import org.example.bookstore_app.model.*;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Component
public class OrdersController implements IOrderOperation{
    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
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
    public BookOrder createOrder(int id,
                                 List<Book> bookList,
                                 String customerName,
                                 String customerContact) {
        BookOrder order = new BookOrder(id, customerName, customerContact);
        stockService.addOrder(order);
        System.out.println("Выведен из БД заказ #" + order.getId() +
                         " на " + bookList.size() + " книг(и) с итоговой ценой: " + order.getTotalPrice());
        return order;
    }

    public BookOrder createNewOrder( List<Book> bookList,
                                     String customerName,
                                     String customerContact) {
        BookOrder order = new BookOrder(0, customerName, customerContact);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(0);
        stockService.addOrder(order);
        for(Book book:bookList){
            createNewOrderItem( order,book);
        }
        updateOrderStatus(order);
        calculateTotalPrice(order);
        System.out.println("Создан заказ #" + order.getId()
                +" на " + bookList.size() + " книг(и) с итоговой ценой: "
                + order.getTotalPrice());
        return order;
    }
    @Override
    public BookOrderItem createOrderItem(int id,BookOrder order,Book book) {
        //если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(id, book,order);
        orderItem = stockService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
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
            Request request = new Request(0, orderItem);
            request = stockService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stockService.addBookOrderItem(orderItem);
            stockService.addRequest(request);
        }
        return orderItem;
    }

    private void createNewOrderItem(BookOrder order, Book book) {
        //если вставка со стороны пользователя, то ид изначально 0
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(0, book,order);
        orderItem = stockService.addBookOrderItem(orderItem);

        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy.getBook());
            bookCopy.setSale(true);
            if (findBook(book) == null) {
                book.setStatusNo();
                stockService.addBook(book);
            }
            stockService.addBookOrderItem(orderItem);
            stockService.addBooksCopy(bookCopy);
        } else {
            Request request = new Request(0, orderItem);
            request = stockService.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
            stockService.addBookOrderItem(orderItem);
        }
    }

    @Override
    public void cancelOrder(int idOrder) {
        BookOrder order = stockService.getOrderByID(idOrder);
        if (order == null) {
            System.out.println("Заказ с ID " + idOrder + " не найден!");
            return;
        }

        List<BookOrderItem> orderItems = stockService.getBookOrderItemByidOrder(order.getId());

        for (BookOrderItem item : orderItems) {
            // 1. Сначала находим request
            Request request = stockService.getRequestsByidOrderItem(item.getId());

            // 2. Обновляем статус книги, если есть копия
            BookCopy bookCopy = item.getBookCopy();
            if (bookCopy != null) {
                bookCopy.setSale(false);
                stockService.updateBooksCopy(bookCopy);

                Book book = item.getBook();
                if (book != null) {
                    // Проверяем, есть ли еще другие копии
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

            // 4. ТЕПЕРЬ можно удалить orderItem (уже нет ссылок на него)
            stockService.removeOrderItem(item);
        }

        // 5. В последнюю очередь удаляем сам order
        stockService.removeOrder(order);

        System.out.println("Заказ #" + idOrder + " успешно отменен!");
        stockService.refreshCache();
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
        List<BookOrderItem> orderItems = stockService.getBookOrderItemByidOrder(order.getId());
        double price=0;
        for(BookOrderItem item:orderItems){
            price += item.getBook().getPrice();
        }
        order.setTotalPrice(price);
    }


}
