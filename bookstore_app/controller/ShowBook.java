package bookstore_app.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bookstore_app.model.Book;
import bookstore_app.model.BookCopy;
import bookstore_app.model.Stok;

public class ShowBook implements IShowBook{
    final Stok stok;
    
    public ShowBook(Stok stok) {
        this.stok = stok;
       
    }
    
    @Override
    public List<BookCopy> getOldBooksSortedByDate() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return stok.getBooksCopy().stream()
            .filter(copy -> copy.getArrivalDate().isBefore(sixMonthsAgo))
            .sorted(Comparator.comparing(BookCopy::getArrivalDate))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookCopy> getOldBooksSortedByPrice() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return stok.getBooksCopy().stream()
            .filter(copy -> copy.getArrivalDate().isBefore(sixMonthsAgo))
            .sorted(Comparator.comparing(copy -> copy.getBook().getPrice()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void showOldBooks() {
        List<BookCopy> oldBooks = getOldBooksSortedByDate();
        System.out.println("Залежавшиеся книги (более 6 месяцев):");
        if (oldBooks.isEmpty()) {
            System.out.println(" - Нет залежавшихся книг");
        } else {
            oldBooks.forEach(copy -> 
                System.out.println(" - " + copy.getBook() + " | Поступление: " + 
                                 copy.getArrivalDate() + " | Цена: " + 
                                 copy.getBook().getPrice() + " руб."));
        }
    }

    @Override
    public  void showBook(){
        if (stok.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stok.getBooks().stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();

        System.out.println("Книги по id:");
        sortedBooks.forEach(book ->
                System.out.println(book.getId()+ " - " + book.getName() + " "+ book.getAuthor()));
    }
    @Override
    public void sortByABC() {
        if (stok.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = sortABCBook();
            
        System.out.println("Книги по алфавиту:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName()));
    }

    @Override
    public void sortByPublicationDate() {
        if (stok.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stok.getBooks().stream()
            .sorted(Comparator.comparing(Book::getPublicationDate))
            .toList();
            
        System.out.println("Книги по дате публикации:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName() + " (" + book.getPublicationDate() + ")"));
    }

    @Override
    public void sortByPrice() {
        if (stok.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = stok.getBooks().stream()
            .sorted(Comparator.comparing(Book::getPrice))
            .toList();
            
        System.out.println("Книги по цене:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName() + " - " + book.getPrice() + " руб."));
    }
    
    @Override
    public void sortByNumberCopies() {
        if (stok.getBooks().isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        
        Map<Book, Long> bookCount = stok.getBooksCopy().stream()
            .collect(Collectors.groupingBy(
                BookCopy::getBook,
                Collectors.counting()
            ));
        
        List<Book> sortedBooks = stok.getBooks().stream()
            .sorted((b1, b2) -> Long.compare(
                bookCount.getOrDefault(b2, 0L),
                bookCount.getOrDefault(b1, 0L)
            ))
            .toList();
        
        System.out.println("Книги по количеству экземпляров:");
        sortedBooks.forEach(book -> {
            long count = bookCount.getOrDefault(book, 0L);
            System.out.println(" - " + book.getName() + " - " + count + " шт.");
        });
    }

    @Override
    public List<Book> sortABCBook(){
        return  stok.getBooks().stream()
                .sorted(Comparator.comparing(Book::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void showAllBook(){
        List<Book> sortedBooks = sortABCBook();
        System.out.println("Список книг:");
        if(sortedBooks.isEmpty()){
            System.out.println("Пуст");
        } else { 
            for (int i = 0; i < sortedBooks.size(); i++) {
                Book book = sortedBooks.get(i);
                System.out.println(book.getId()+ ". " + book.getName()+" - "+ book.getAuthor() + " - " + book.getPrice());
            }
        }
    }

}
