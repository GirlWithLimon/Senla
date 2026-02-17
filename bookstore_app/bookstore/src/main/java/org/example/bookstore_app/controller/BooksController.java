package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.*;
import org.example.bookstore_app.service.StockService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BooksController implements IBookStok {
    @Inject
    StockService stockService;
    @Inject
    private BookstoreConfig config;
    Logger logger = LoggerFactory.getLogger(BooksController.class);

    public BooksController() {  }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }

    @Override
    public void addBookToStock(Book book) {
        try {
            stockService.addBook(book);
            logger.info("Книга добавлена в каталог: {} | ID: {} | Всего в каталоге: {}",
                    book.getName(),
                    book.getId(),
                    stockService.getBooks().size());
        } catch (Exception e) {
            logger.error("Ошибка при добавлении книги {}", e.getMessage());
        }

    }

    public void addBookCopyToStock(BookCopy bookCopy, LocalDate date) {
        stockService.addBooksCopy(bookCopy);
        bookCopy.getBook().setStatusStok();

        System.out.println("Добавлена книга на склад: " + bookCopy.getBook().getName() +
                " | Копий: " + stockService.findCountByIdBook(bookCopy.getBook().getId()) +
                " | Книг в каталоге: " + stockService.getBooks().size());

         if (config.isAutoCompleteRequests()) {
            List<Request> requestsToRemove = stockService.findByRequestIdWithBook(bookCopy.getBook().getId());

            Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
                    .map(this::findOrderByRequest)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            requestsToRemove.forEach(request -> {
                ContinueRequest(request.getOrderItem(), bookCopy);
                removeBookCopyfromstock(bookCopy);
            });

            requestsToRemove.forEach(stockService::removeRequest);

            ordersToUpdate.forEach(order -> {
                updateOrderStatus(order);
                stockService.updateOrder(order);
                System.out.println("Обновлен статус заказа #" + order.getId() +
                        " на: " + order.getStatus());
            });

            if (!requestsToRemove.isEmpty()) {
                System.out.println("Выполнено запросов: " + requestsToRemove.size() +
                        " для книги: " + bookCopy.getBook().getName());
            }
            stockService.updateBook(bookCopy.getBook());
        } else {
            logger.info("Автоматическое выполнение запросов отключено в настройках");
        }
    }
    public void ContinueRequest(BookOrderItem orderItem, BookCopy bookCopy) {
        orderItem.setBookCopy(bookCopy);
        orderItem.setStatus(OrderItemStatus.COMPLETED);
        stockService.updateBookOrderItem(orderItem);
    }

    private BookOrder findOrderByRequest(Request request) {
        return request.getOrderItem().getOrder();
    }


    @Override
    public void removeBookCopyfromstock(BookCopy bookCopy) {
      bookCopy.setSale(true);
      stockService.updateBooksCopy(bookCopy);
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
        stockService.addOrder(order);
    }

    public Book getBooksById(int idBook){
        return stockService.getBooksById(idBook);
    }
}
