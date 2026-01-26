package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StockService;
import org.example.bookstore_app.model.*;

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

    public BooksController() {  }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }

    @Override
    public void addBookToStock(int id, Book book, LocalDate date) {
        boolean bookExists = stockService.getBooks().stream()
                .anyMatch(b -> b.getId() == book.getId());

        if (!bookExists) {
            stockService.addBook(book);
            System.out.println("Книга добавлена в каталог: " + book.getName() +
                    " | ID: " + book.getId() +
                    " | Всего в каталоге: " + stockService.getBooks().size());
        } else {
            System.out.println("Книга уже есть в каталоге: " + book.getName() +
                    " | ID: " + book.getId());
        }
    }

    public void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date) {
        stockService.addBooksCopy(bookCopy);
        stockService.getBooksById(bookCopy.getIdBook()).setStatusStok();

        System.out.println("Добавлена книга на склад: " + stockService.getBooksById(bookCopy.getIdBook()).getName() +
                " | Копий: " + stockService.findCountByIdBook(bookCopy.getIdBook()) +
                " | Книг в каталоге: " + stockService.getBooks().size());

         if (config.isAutoCompleteRequests()) {
            List<Request> requestsToRemove = stockService.getRequests().stream()
                    .filter(request -> stockService.getBookOrderItemByID(request.getIdOrderItem()).getIdBook()==bookCopy.getIdBook())
                    .toList();

            Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
                    .map(this::findOrderByRequest)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            requestsToRemove.forEach(request -> {
                ContinueRequest(stockService.getBookOrderItemByID(request.getIdOrderItem()), bookCopy);
                removeBookCopyfromstock(bookCopy);
            });

            requestsToRemove.forEach(stockService::removeRequest);

            ordersToUpdate.forEach(order -> {
                updateOrderStatus(order);
                System.out.println("Обновлен статус заказа #" + order.getId() +
                        " на: " + order.getStatus());
            });

            if (!requestsToRemove.isEmpty()) {
                System.out.println("Выполнено запросов: " + requestsToRemove.size() +
                        " для книги: " + stockService.getBooksById(bookCopy.getIdBook()).getName());
            }
        } else {
            System.out.println("Автоматическое выполнение запросов отключено в настройках");
        }
    }
    public void ContinueRequest(BookOrderItem orderItem, BookCopy bookCopy) {
        orderItem.setIdBookCopy(bookCopy.getId());
        orderItem.setStatus(OrderItemStatus.COMPLETED);
        stockService.addBookOrderItem(orderItem);
    }

    private BookOrder findOrderByRequest(Request request) {
        return stockService.getOrderByID(stockService.getBookOrderItemByID(request.getIdOrderItem()).getIdOrder());
    }


    @Override
    public void removeBookCopyfromstock(BookCopy bookCopy) {
      bookCopy.setSale(true);
      stockService.addBooksCopy(bookCopy);
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
}