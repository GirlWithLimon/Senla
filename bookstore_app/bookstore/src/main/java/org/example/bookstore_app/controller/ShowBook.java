package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class ShowBook implements IShowBook {
    @Autowired
    private StockService stockService;
    @Autowired
    private BookstoreConfig config;
    Logger logger = Logger.getLogger(getClass().getName());
    public ShowBook(){}

    @Override
    public List<BookCopy> getOldBooksSortedByDate() {
        LocalDate thresholdDate = LocalDate.now().minusMonths(config.getMonthsForOldBook());
        return stockService.getBooksCopy().stream()
                .filter(copy -> copy.getArrivalDate().isBefore(thresholdDate))
                .sorted(Comparator.comparing(BookCopy::getArrivalDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopy> getOldBooksSortedByPrice() {
        LocalDate thresholdDate = LocalDate.now().minusMonths(config.getMonthsForOldBook());
        return stockService.getBooksCopy().stream()
                .filter(copy -> copy.getArrivalDate().isBefore(thresholdDate))
                .sorted(Comparator.comparing(copy -> copy.getBook().getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public void showOldBooksByDate() {
        List<BookCopy> oldBooks = getOldBooksSortedByDate();
        int monthsThreshold = config.getMonthsForOldBook();
        System.out.println("Залежавшиеся книги (более " + monthsThreshold + " месяцев):");
        if (oldBooks.isEmpty()) {
            System.out.println(" - Нет залежавшихся книг");
        } else {
            oldBooks.forEach(copy ->
                    System.out.println(" - " + copy.getBook().getId() + " | Поступление: " +
                            copy.getArrivalDate() + " | Цена: " +
                            copy.getBook().getPrice() + " руб."));
        }
    }

    @Override
    public void showOldBooksByPrice() {
        List<BookCopy> oldBooks = getOldBooksSortedByPrice();
        int monthsThreshold = config.getMonthsForOldBook();
        System.out.println("Залежавшиеся книги (более " + monthsThreshold + " месяцев):");
        if (oldBooks.isEmpty()) {
            System.out.println(" - Нет залежавшихся книг");
        } else {
            oldBooks.forEach(copy ->
                    System.out.println(" - " + copy.getBook().getId() + " | Поступление: "
                            + copy.getArrivalDate() + " | Цена: "
                            + copy.getBook().getPrice() + " руб."));
        }
    }

    @Override
    public void showBook() {
        if (stockService.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stockService.getBooks().stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();

        System.out.println("Книги по id:");
        sortedBooks.forEach(book ->
                System.out.println(book.getId() + " - " + book.getName() + " " + book.getAuthor()));
    }

    @Override
    public void sortByABC() {
        if (stockService.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = sortABCBook();

        System.out.println("Книги по алфавиту:");
        for(int i=0; i<sortedBooks.size();i++){
            System.out.println(i + " - " + sortedBooks.get(i).getName());
        }

    }

    @Override
    public void sortByPublicationDate() {
        if (stockService.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stockService.getBooks().stream()
                .sorted(Comparator.comparing(Book::getPublicationDate))
                .toList();
        logger.info("Книги по дате публикации:");
        System.out.println("Книги по дате публикации:");
        sortedBooks.forEach(book ->
                System.out.println(" - " + book.getName() + " (" + book.getPublicationDate() + ")"));
    }

    @Override
    public void sortByPrice() {
        if (stockService.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stockService.getBooks().stream()
                .sorted(Comparator.comparing(Book::getPrice))
                .toList();

        System.out.println("Книги по цене:");
        sortedBooks.forEach(book ->
                System.out.println(" - " + book.getName() + " - " + book.getPrice() + " руб."));
    }

    @Override
    public void sortByNumberCopies() {
        if (stockService.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }

        List<BookCopy> allCopies = stockService.findWithBookId();
        Map<Book, Long> availableCount = new HashMap<>();
        Map<Book, Long> soldCount = new HashMap<>();
        Set<Book> uniqueBooks = new HashSet<>();

        for (BookCopy copy : allCopies) {
            Book book = copy.getBook();
            uniqueBooks.add(book);

            if (!copy.getSale()) {
                availableCount.put(book, availableCount.getOrDefault(book, 0L) + 1);
            } else {
                soldCount.put(book, soldCount.getOrDefault(book, 0L) + 1);
            }
        }

        System.out.println("\n=== КНИГИ В НАЛИЧИИ (по убыванию) ===");

        // Сортируем уникальные книги по количеству в наличии
        uniqueBooks.stream().sorted((b1, b2) -> {
                    long count1 = availableCount.getOrDefault(b1, 0L);
                    long count2 = availableCount.getOrDefault(b2, 0L);
                    return Long.compare(count2, count1);
                })
                .forEach(book -> {
                    long available = availableCount.getOrDefault(book, 0L);
                    long sold = soldCount.getOrDefault(book, 0L);
                    long total = available + sold;

                    if (available > 0) {
                        System.out.printf("%s (ID: %d) - %d шт. в наличии (всего: %d, продано: %d)%n",
                                book.getName(), book.getId(), available, total, sold);
                    } else {
                        System.out.printf("%s (ID: %d) - нет в наличии (всего: %d, продано: %d)%n",
                                book.getName(), book.getId(), total, sold);
                    }
                });
    }

    @Override
    public List<Book> sortABCBook() {
        return stockService.getBooks().stream()
                .sorted(Comparator.comparing(Book::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void showAllBook() {
        List<Book> sortedBooks = sortABCBook();
        System.out.println("Список книг:");
        if (sortedBooks.isEmpty()) {
            System.out.println("Пуст");
        } else {
            for (Book book : sortedBooks) {
                System.out.println(book.getId() + ". " + book.getName() + " - " +
                        book.getAuthor() + " - " + book.getPrice() + " руб.");
            }
        }
    }



     public boolean isOldBook(BookCopy bookCopy) {
        LocalDate thresholdDate = LocalDate.now().minusMonths(config.getMonthsForOldBook());
        return bookCopy.getArrivalDate().isBefore(thresholdDate);
    }

    public long getOldBooksCount() {
        return stockService.getBooksCopy().stream()
                .filter(this::isOldBook)
                .count();
    }

}
