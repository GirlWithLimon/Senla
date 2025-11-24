package project.model;
import java.util.*;

public class Stok {
    private final List<BookCopy> booksCopy = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();
    private final List<BookOrder> orders = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    
    
    public void addOrder(BookOrder order){
        orders.add(order);
    }
    public void removeOrder(BookOrder order){
        orders.remove(order);
    }
    public List<BookOrder> getOrders() { 
        return new ArrayList<>(orders); 
    }
    
    public void addRequest(Request request){
        requests.add(request);
    }
    public void removeRequest(Request request){
        requests.remove(request);
    }
    public void removeRequests(List<Request> requests){
        requests.removeAll(requests);
    }
    public List<Request> getRequests() { 
        return new ArrayList<>(requests); 
    }
    
    public void addBooksCopy(BookCopy copy){
        booksCopy.add(copy);
    }
    public void removeBooksCopy(BookCopy copy){
        booksCopy.remove(copy);
    }
    public List<BookCopy> getBooksCopy() { 
        return new ArrayList<>(booksCopy); 
    }
    
    public void addBook(Book book){
        books.add(book);
    }
    public void removeBook(Book book){
        books.remove(book);
    }
    public List<Book> getBooks() { 
        return new ArrayList<>(books); 
    }
}