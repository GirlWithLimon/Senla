package project.controller;

import java.util.List;
import java.util.stream.Collectors;

import project.model.Book;
import project.model.BookCopy;
import project.model.BookOrder;
import project.model.BookOrderItem;
import project.model.Request;

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
