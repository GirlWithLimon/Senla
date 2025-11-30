package bookstore_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import bookstore_app.model.Book;
import bookstore_app.model.BookCopy;
import bookstore_app.model.BookOrder;
import bookstore_app.model.BookOrderItem;
import bookstore_app.model.Request;

public class ID {
       private int findMaxId(List<?> items, java.util.function.Function<Object, Integer> idExtractor) {
        return items.stream()
            .mapToInt(idExtractor::apply)
            .max()
            .orElse(0); 
    }
    public  int generateOrderId(List<BookOrder> order) {
        return findMaxId(order, item -> ((BookOrder) item).getId())+1;
    }
    public  int generateBookId(List<Book> books) {
        return findMaxId(books, item -> ((Book) item).getId())+1;
    }
    
    public int generateCopyId(List<BookCopy> booksCopy) {
        return findMaxId(booksCopy, item -> ((BookCopy) item).getId());
    }
    
    public int generateOrderItemId(List<BookOrder> orders) {
       List<BookOrderItem> allItems = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .collect(Collectors.toList());
        return findMaxId(allItems, item -> ((BookOrderItem) item).getId());
    }
    
    public int generateRequestId(List<Request> requests) {
        return findMaxId(requests, item -> ((Request) item).getId());
    }
}
