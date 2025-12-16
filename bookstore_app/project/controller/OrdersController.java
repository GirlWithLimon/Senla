package bookstore_app.project.controller;

import java.util.List;

import bookstore_app.config.annotation.Component;
import bookstore_app.config.annotation.Inject;
import bookstore_app.project.model.Book;
import bookstore_app.project.model.BookCopy;
import bookstore_app.project.model.BookOrder;
import bookstore_app.project.model.BookOrderItem;
import bookstore_app.project.model.OrderItemStatus;
import bookstore_app.project.model.OrderStatus;
import bookstore_app.project.model.Request;
import bookstore_app.project.model.Stok;

@Component
public class OrdersController implements IOrderOperation{
    @Inject
    Stok stok;
    
    public OrdersController() {  }

    private BookCopy findBook(Book book) {
        return stok.getBooksCopy().stream()
            .filter(copy -> copy.getBook().equals(book))
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(int id, List<Book> bookList, String customerName, String customerContact) {
        BookOrder bookOrder = new BookOrder(id, customerName, customerContact);
        bookList.stream()
            .map(this::createOrderItem)
            .forEach(bookOrder::addBookToOrder);
            
        stok.addOrder(bookOrder);
        updateOrderStatus(bookOrder);
       
        System.out.println("Создан заказ #" + bookOrder.getId() + 
                         " на " + bookList.size() + " книг(и)");
        return bookOrder;
    }
   
    private BookOrderItem createOrderItem(Book book) {
        BookCopy bookCopy = findBook(book);
        int id;
        if(stok.getOrders().isEmpty()){
            id = 1;
        } else {
            boolean anyOrderHasBooks = stok.getOrders().stream()
                    .anyMatch(order -> !order.getOrderItems().isEmpty());

            if (anyOrderHasBooks) {
               id = stok.getOrders().stream()
                        .flatMap(order -> order.getOrderItems().stream())
                        .mapToInt(BookOrderItem::getId)
                        .max()
                        .orElse(0) + 1;
            } else {
                id = 1;
            }
        }
        BookOrderItem orderItem = new BookOrderItem(id, book);

        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            stok.removeBooksCopy(bookCopy);
            if (findBook(book) == null) {
                book.setStatusNo();
            }
        } else {
            int idRequest = stok.getRequests().isEmpty()? 1: stok.getRequests().getLast().getId()+1;
            Request request = new Request(idRequest, orderItem);
            stok.addRequest(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
        }
        return orderItem;
    }
    
    @Override
    public void cancelOrder(BookOrder order) {
        stok.removeOrder(order);
        order.setStatus(OrderStatus.CANCELLED);
        
        order.getOrderItems().forEach(orderItem -> {
            if (orderItem.getBookCopy() != null) {
                stok.addBooksCopy(orderItem.getBookCopy());
                orderItem.getBook().setStatusStok();
            }
            stok.getRequests().removeIf(request -> 
                request.getOrderItem().equals(orderItem));
        });
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        order.getOrderItems().stream()
            .filter(item -> item.getBook().equals(book))
            .findFirst()
            .ifPresent(itemToRemove -> {
                if (itemToRemove.getBookCopy() != null) {
                    stok.addBooksCopy(itemToRemove.getBookCopy());
                    itemToRemove.getBook().setStatusStok();
                }
                stok.getRequests().removeIf(request -> 
                    request.getOrderItem().equals(itemToRemove));
                order.getOrderItems().remove(itemToRemove);
                updateOrderStatus(order);
            });
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