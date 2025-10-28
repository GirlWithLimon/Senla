import java.util.*;

public class Stok implements  IBookStok {
    private List<Book> books = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private  Request toRemove = null;
    
    @Override
    public void addBookToStock(Book book) {
        books.add(book);
        book.setStatusStok();
             
        toRemove=null;
        
        for (Request request : requests) {
            if (request.getBook().equals(book)) {
                request.ContinueRequest();
                removeBookFromStock(book);
                toRemove = request; 
                break;
            }
        }
        if(toRemove != null) {  requests.remove(toRemove);}
        else {
            
        }
    }
    
    @Override
    public void removeBookFromStock(Book book) {
         books.remove(book);
         if (!books.contains(book)) {
             book.setStatusNo();
         }
    }
    
    @Override
    public void createOrder(Book book) {
        BookOrder order = new BookOrder(book);
        
        if (book.getStatus().equals("Отсутствует")) {
            Request request = new Request(order);
            requests.add(request);
           
        } else {
            order.setStatus("Выполнен");
            System.out.println("Выдана книга: " + book.getName());
            removeBookFromStock(book);
        }
    }
    
    @Override
   public void cancelOrder(BookOrder order) {
        order.setStatus("Отменен");
        requests.removeIf(request -> request.getOrder().equals(order));
    }
    
}