package bookstore_app.controller;

import bookstore_app.config.BookstoreConfig;
import bookstore_app.config.ConfigFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import bookstore_app.model.Book;
import bookstore_app.model.BookCopy;
import bookstore_app.model.BookOrder;
import bookstore_app.model.OrderItemStatus;
import bookstore_app.model.OrderStatus;
import bookstore_app.model.Request;
import bookstore_app.model.Stok;

public class BooksController implements IBookStok {
    Stok stok;
    private final BookstoreConfig config;

    public BooksController(Stok stok) {
        this.stok = stok;
        this.config = ConfigFactory.getConfig(BookstoreConfig.class);
    }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }

    @Override
    public void addBookToStock(int id, Book book, LocalDate date) {
        boolean bookExists = stok.getBooks().stream()
                .anyMatch(b -> b.getId() == book.getId());

        if (!bookExists) {
            stok.addBook(book);
            System.out.println("Книга добавлена в каталог: " + book.getName() +
                    " | ID: " + book.getId() +
                    " | Всего в каталоге: " + stok.getBooks().size());
        } else {
            System.out.println("Книга уже есть в каталоге: " + book.getName() +
                    " | ID: " + book.getId());
        }
    }

    public void addBookCopyToStock(int id, BookCopy bookCopy, LocalDate date) {
        stok.addBooksCopy(bookCopy);
        bookCopy.getBook().setStatusStok();

        System.out.println("Добавлена книга на склад: " + bookCopy.getBook().getName() +
                " | Копий: " + countBookCopies(bookCopy.getBook()) +
                " | Книг в каталоге: " + stok.getBooks().size());

         if (config.isAutoCompleteRequests()) {
            List<Request> requestsToRemove = stok.getRequests().stream()
                    .filter(request -> request.getBook().equals(bookCopy.getBook()))
                    .toList();

            Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
                    .map(this::findOrderByRequest)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            requestsToRemove.forEach(request -> {
                request.ContinueRequest(bookCopy);
                removeBookCopyfromstock(bookCopy);
            });

            requestsToRemove.forEach(stok::removeRequest);

            ordersToUpdate.forEach(order -> {
                updateOrderStatus(order);
                System.out.println("Обновлен статус заказа #" + order.getId() +
                        " на: " + order.getStatus());
            });

            if (!requestsToRemove.isEmpty()) {
                System.out.println("Выполнено запросов: " + requestsToRemove.size() +
                        " для книги: " + bookCopy.getBook().getName());
            }
        } else {
            System.out.println("Автоматическое выполнение запросов отключено в настройках");
        }
    }

    private BookOrder findOrderByRequest(Request request) {
        return stok.getOrders().stream()
                .filter(order -> order.getOrderItems().contains(request.getOrderItem()))
                .findFirst()
                .orElse(null);
    }

    private int countBookCopies(Book book) {
        return (int) stok.getBooksCopy().stream()
                .filter(copy -> copy.getBook().equals(book))
                .count();
    }

    @Override
    public void removeBookCopyfromstock(BookCopy book) {
        stok.removeBooksCopy(book);
        boolean hasOtherCopies = stok.getBooksCopy().stream()
                .anyMatch(copy -> copy.getBook().equals(book.getBook()));
        if (!hasOtherCopies) {
            book.getBook().setStatusNo();
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