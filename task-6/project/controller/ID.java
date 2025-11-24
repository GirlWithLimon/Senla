package project.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import project.model.Book;
import project.model.BookCopy;
import project.model.BookOrder;
import project.model.BookOrderItem;
import project.model.Request;

public class ID {
      private String generateUniqueId(String prefix, List<?> items, java.util.function.Function<Object, String> idExtractor) {
        String id;
        boolean isUnique;
        int attempts = 0;
        
        do {
            id = prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
            final String newId = id;
            isUnique = items.stream().noneMatch(item -> idExtractor.apply(item).equals(newId));
            attempts++;
            
            if (attempts > 100) {
                throw new RuntimeException("Не удалось сгенерировать уникальный ID после 100 попыток");
            }
        } while (!isUnique);
        
        return id;
    }
    public  String generateOrderId(List<BookOrder> order) {
        return generateUniqueId("ORDER", order, item -> ((BookOrder) item).getId());
    }
    public  String generateBookId(List<Book> books) {
        return generateUniqueId("BOOK", books, item -> ((Book) item).getId());
    }
    
    public String generateCopyId(List<BookCopy> booksCopy) {
        return generateUniqueId("COPY", booksCopy, item -> ((BookCopy) item).getId());
    }
    
    public String generateOrderItemId(List<BookOrder> orders) {
       List<BookOrderItem> allItems = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .collect(Collectors.toList());
        return generateUniqueId("ITEM", allItems, item -> ((BookOrderItem) item).getId());
    }
    
    public String generateRequestId(List<Request> requests) {
        return generateUniqueId("REQUEST", requests, item -> ((Request) item).getId());
    }
}
