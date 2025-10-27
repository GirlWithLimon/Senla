import java.util.*;

public class Stok implements  IBookStok {
    private List<Book> books = new ArrayList<>();
    private List<BookOrder> request = new ArrayList<>();
    
    
    @Override
    public void addBookToStock(Book book) {
        books.add(book);
        book.setStatusStok();
        
        
        List<BookOrder> toRemove = new ArrayList<>();
        
        for (BookOrder order : request) {
            if (order.getBook().equals(book) && order.getStatus().equals("Новый")) {
                order.setStatus("Выполнен");
                removeBookFromStock(book);
                System.out.println("Выдана книга по запросу: " + book.getName());
                toRemove.add(order); 
                break;
            }
        }
        
        request.removeAll(toRemove);
    }
    
    @Override
    public void removeBookFromStock(Book book) {
        book.setStatusNo();
    }
    
    @Override
    public void createOrder(Book book) {
        BookOrder order = new BookOrder(book);
        
        if (book.getStatus().equals("Отсутствует")) {
            request.add(order);
            System.out.println("Создан запрос на книгу: " + book.getName());
        } else {
            order.setStatus("Выполнен");
            System.out.println("Выдана книга: " + book.getName());
            removeBookFromStock(book);
        }
    }
    
    @Override
   public void cancelOrder(BookOrder order) {
        order.setStatus("Отменен");
        request.remove(order); 
    }
    
}