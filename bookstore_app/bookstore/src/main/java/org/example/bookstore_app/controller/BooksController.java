package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.dao.StokService;
import org.example.bookstore_app.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BooksController implements IBookStok {
    @Inject
    StokService stokService;
    @Inject
    private BookstoreConfig config;

    public BooksController() {  }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }

    @Override
    public void addBookToStock(int id, Book book, LocalDate date) {
        boolean bookExists = stokService.getBooks().stream()
                .anyMatch(b -> b.getId() == book.getId());

        if (!bookExists) {
            stokService.addBook(book);
            System.out.println("Книга добавлена в каталог: " + book.getName() +
                    " | ID: " + book.getId() +
                    " | Всего в каталоге: " + stokService.getBooks().size());
        } else {
            System.out.println("Книга уже есть в каталоге: " + book.getName() +
                    " | ID: " + book.getId());
        }
    }

    public void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date) {
        stokService.addBooksCopy(bookCopy);
        stokService.getBooksById(bookCopy.getIdBook()).setStatusStok();

        System.out.println("Добавлена книга на склад: " + stokService.getBooksById(bookCopy.getIdBook()).getName() +
                " | Копий: " + countBookCopies(bookCopy.getIdBook()) +
                " | Книг в каталоге: " + stokService.getBooks().size());

         if (config.isAutoCompleteRequests()) {
            List<Request> requestsToRemove = stokService.getRequests().stream()
                    .filter(request -> stokService.getBookOrderItemByID(request.getIdOrderItem()).equals(bookCopy.getIdBook()))
                    .toList();

            Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
                    .map(this::findOrderByRequest)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            requestsToRemove.forEach(request -> {
                ContinueRequest(stokService.getBookOrderItemByID(request.getIdOrderItem()), bookCopy);
                removeBookCopyfromstock(bookCopy);
            });

            requestsToRemove.forEach(stokService::removeRequest);

            ordersToUpdate.forEach(order -> {
                updateOrderStatus(order);
                System.out.println("Обновлен статус заказа #" + order.getId() +
                        " на: " + order.getStatus());
            });

            if (!requestsToRemove.isEmpty()) {
                System.out.println("Выполнено запросов: " + requestsToRemove.size() +
                        " для книги: " + stokService.getBooksById(bookCopy.getIdBook()).getName());
            }
        } else {
            System.out.println("Автоматическое выполнение запросов отключено в настройках");
        }
    }
    public void ContinueRequest(BookOrderItem orderItem, BookCopy bookCopy) {
        orderItem.setIdBookCopy(bookCopy.getId());
        orderItem.setStatus(OrderItemStatus.COMPLETED);
    }

    private BookOrder findOrderByRequest(Request request) {
        return stokService.getOrders().stream()
                .filter(order -> order.getOrderItems().contains(stokService.getBookOrderItemByID(request.getIdOrderItem())))
                .findFirst()
                .orElse(null);
    }

    private int countBookCopies(Integer idBook) {
        return (int) stokService.getBooksCopy().stream()
                .filter(copy -> copy.getIdBook()==idBook)
                .count();
    }

    @Override
    public void removeBookCopyfromstock(BookCopy bookCopy) {
        stokService.removeBooksCopy(bookCopy);
        boolean hasOtherCopies = stokService.getBooksCopy().stream()
                .anyMatch(copy -> copy.getIdBook()==bookCopy.getIdBook());
        if (!hasOtherCopies) {
            stokService.getBooksById(bookCopy.getIdBook()).setStatusNo();
        }
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
}