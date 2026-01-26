package org.example.bookstore_app.controller;

import org.example.bookstore_app.config.BookstoreConfig;
import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.dao.StockService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShowBook implements IShowBook {
    @Inject
    private StockService stockService;
    @Inject
    private BookstoreConfig config;
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
                .sorted(Comparator.comparing(copy -> stockService.getBooksById(copy.getIdBook()).getPrice()))
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
                    System.out.println(" - " + copy.getIdBook() + " | Поступление: " +
                            copy.getArrivalDate() + " | Цена: " +
                            stockService.getBooksById(copy.getIdBook()).getPrice() + " руб."));
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
                    System.out.println(" - " + copy.getIdBook() + " | Поступление: " +
                            copy.getArrivalDate() + " | Цена: " +
                            stockService.getBooksById(copy.getIdBook()).getPrice() + " руб."));
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

        Map<Integer, Long> bookCount = stockService.getBooksCopy().stream()
                .collect(Collectors.groupingBy(
                        BookCopy::getIdBook,
                        Collectors.counting()
                ));

        List<Book> sortedBooks = stockService.getBooks().stream()
                .sorted((b1, b2) -> {
                    long count1 = bookCount.getOrDefault(b1.getId(), 0L);
                    long count2 = bookCount.getOrDefault(b2.getId(), 0L);
                    return Long.compare(count2, count1);
                })
                .toList();

        System.out.println("Книги по количеству экземпляров:");
        sortedBooks.forEach(book -> {
            long count = bookCount.getOrDefault(book.getId(), 0L);
            System.out.println(" - " + book.getName() + " - " + count + " шт.");
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
            for (int i = 0; i < sortedBooks.size(); i++) {
                Book book = sortedBooks.get(i);
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