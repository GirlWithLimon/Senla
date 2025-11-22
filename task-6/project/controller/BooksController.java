package project.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import project.model.Book;
import project.model.BookCopy;
import project.model.BookOrder;
import project.model.Request;
import project.model.Stok;

public class BooksController implements IBookStok{
    Stok stok;
    ID idNew;

    public BooksController (Stok stok) {
        this.stok = stok;
    }

    public void addBookToCatalog(Book book) {
        boolean bookExists = books.stream()
            .anyMatch(b -> b.getId().equals(book.getId()));
            
        if (!bookExists) {
            books.add(book);
            System.out.println("Книга добавлена в каталог: " + book.getName() + " | ID: " + book.getId() + " | Всего в каталоге: " + books.size());
        } else {
            System.out.println("Книга уже есть в каталоге: " + book.getName() + " | ID: " + book.getId());
        }
    }
    
    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }
    @Override
    public void addBookToStock(String id, Book book, LocalDate date) {
        addBookToCatalog(book);
        BookCopy newBook = new BookCopy(id, book, date);
        booksCopy.add(newBook);
        book.setStatusStok();     
        
        System.out.println("Добавлена книга на склад: " + book.getName() + 
                      " | Копий: " + countBookCopies(book) + 
                      " | Книг в каталоге: " + books.size());
        
        List<Request> requestsToRemove = requests.stream()
            .filter(request -> request.getBook().equals(book))
            .collect(Collectors.toList());
            
        Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
            .map(this::findOrderByRequest)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        requestsToRemove.forEach(request -> {
            request.ContinueRequest(newBook);
            removeBookFromStock(newBook);
        });
        
        requests.removeAll(requestsToRemove);
        
        ordersToUpdate.forEach(order -> {
            updateOrderStatus(order);
            System.out.println("Обновлен статус заказа #" + order.getOrderId() + 
                          " на: " + order.getStatus());
        });
        
        if (!requestsToRemove.isEmpty()) {
            System.out.println("Выполнено запросов: " + requestsToRemove.size() + 
                          " для книги: " + book.getName());
        }
    }
    
    private BookOrder findOrderByRequest(Request request) {
        return orders.stream()
            .filter(order -> order.getOrderItems().contains(request.getOrderItem()))
            .findFirst()
            .orElse(null);
    }
    
    private int countBookCopies(Book book) {
        return (int) booksCopy.stream()
                .filter(copy -> copy.getBook().equals(book))
                .count();
    }
    
    @Override
    public void removeBookFromStock(BookCopy book) {
        booksCopy.remove(book);
        boolean hasOtherCopies = booksCopy.stream()
            .anyMatch(copy -> copy.getBook().equals(book.getBook()));
        if (!hasOtherCopies) {
            book.getBook().setStatusNo();
        }
    }
}
