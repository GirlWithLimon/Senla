package project.controller;

import java.util.List;

import project.model.Book;
import project.model.BookCopy;
import project.model.BookOrder;
import project.model.BookOrderItem;
import project.model.OrderItemStatus;
import project.model.OrderStatus;
import project.model.Request;
import project.model.Stok;

public class OrdersController implements IOrderOperation{
    Stok stok;
    ID idNew;
    public OrdersController(Stok stok) {
        this.stok = stok;
    }

    private BookCopy findBook(Book book) {
        return stok.getBooksCopy().stream()
            .filter(copy -> copy.getBook().equals(book))
            .findFirst()
            .orElse(null);
    }

    @Override
    public BookOrder createOrder(List<Book> bookList, String customerName, String customerContact) {
        BookOrder bookOrder = new BookOrder(customerName, customerContact);
        bookList.stream()
            .map(createOrderItem(this))
            .forEach(bookOrder::addBookToOrder);
            
        stok.addOrder(bookOrder);
        updateOrderStatus(bookOrder);
       
        System.out.println("Создан заказ #" + bookOrder.getOrderId() + 
                         " на " + bookList.size() + " книг(и)");
        return bookOrder;
    }
   
    private BookOrderItem createOrderItem(Book book) {
        BookCopy bookCopy = findBook(book);
        String id = idNew.generateOrderItemId();
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
            String idRequest = idNew.generateRequestId();
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
            List <Request> request = stok.getRequests();
            request.stream()
            .filter(request -> request.getOrderItem().equals(orderItem))
            .forEach(stok.removeRequest(this)));
            stok.removeRequest(request -> request.getOrderItem().equals(orderItem));
        });
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        order.getOrderItems().stream()
            .filter(item -> item.getBook().equals(book))
            .findFirst()
            .ifPresent(itemToRemove -> {
                if (itemToRemove.getBookCopy() != null) {
                    booksCopy.add(itemToRemove.getBookCopy());
                    itemToRemove.getBook().setStatusStok();
                }
                requests.removeIf(request -> request.getOrderItem().equals(itemToRemove));
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
