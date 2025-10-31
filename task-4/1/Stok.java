import java.util.*;
import java.time.LocalDate;

public class Stok implements  IBookStok, StokOperation {
    private List<BookAtStok> books = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private  Request toRemove = null;
    
    //добавление книги в хранилище и проверка, что нет заявок на эту книгу, иначе книгу сразу отправить по заявке
    @Override
    public void addBookToStock(Book book, LocalDate date) {
        BookAtStok newBook = new BookAtStok(book, date);
        books.add(newBook);
        newBook.setStatusStok();
             
        toRemove=null;
        
        for (Request request : requests) {
            if (request.getBook().equals(book)) {
                request.ContinueRequest();
                removeBookFromStock(newBook);
                toRemove = request; 
                break;
            }
        }
        if(toRemove != null) {  requests.remove(toRemove);}
        else {
            
        }
    }
    
    @Override
    public void removeBookFromStock(BookAtStok book) {
         books.remove(book);
         if (!books.contains(book)) {
             book.setStatusNo();
         }
    }
    
    @Override
    public void createOrder(Book book) {
        
        BookAtStok availableInstance = findBook(book);
        BookOrder order = new BookOrder(book);
        if (availableInstance != null) {
            
            availableInstance.setStatusNo();
            order.setStatus("Выполнен");
            System.out.println("Продан экземпляр книги: " + availableInstance);
        } else {
            // Нет в наличии - создаем запрос
            Request request = new Request(order);
            requests.add(request);
            order.setStatus("В ожидании");
            System.out.println("Книга отсутствует. Создан запрос.");
        }
      
    }
      private BookAtStok findBook(Book book) {
        for (BookAtStok bookAtStok : books) {
            if (bookAtStok.getBook().equals(book) && bookAtStok.getBoolStatus()) {
                return bookAtStok;
            }
        }
        return null;
    }
    @Override
   public void cancelOrder(BookOrder order) {
        order.setStatus("Отменен");
        requests.removeIf(request -> request.getOrder().equals(order));
    }
    
}