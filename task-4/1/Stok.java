import java.util.*;
import java.time.LocalDate;

public class Stok implements  IShowBook, IOrderOperation, IBookStok {
    private List<BookCopy> books = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
     private List<BookOrder> orders = new ArrayList<>();
    private  Request toRemove = null;
    
    //добавление книги в хранилище и проверка, что нет заявок на эту книгу, иначе книгу сразу отправить по заявке
    @Override
    public void addBookToStock(Book book, LocalDate date) {
        BookCopy newBook = new BookCopy(book, date);
        books.add(newBook);
             
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
    public void removeBookFromStock(BookCopy book) {
         books.remove(book);
         if (!books.contains(book)) {
             book.getBook().setStatusNo();
         }
    }
    
    @Override
    public void createOrder(Book book) {
        BookCopy bookCopy = findBook(book);
        BookOrder order = new BookOrder(book);
        orders.add(order);
        if (bookCopy != null) {
            order.setBookCopy(bookCopy);
            order.setStatus("Выполнен");
            System.out.println("Продан экземпляр книги: " + bookCopy);
            books.remove(bookCopy);
            if(findBook(book)==null){
                book.setStatusNo();
            }
        } else {
            // Нет в наличии - создаем запрос
            Request request = new Request(order);
            requests.add(request);
            order.setStatus("В ожидании");
            System.out.println("Книга отсутствует. Создан запрос.");
        }
      
    }
    private BookCopy findBook(Book book) {
    if(!book.getBoolStatus()) return null;
    for (BookCopy bookCopy : books) {
        if (bookCopy.getBook().equals(book)) {
            return bookCopy;
        }
    }
    return null;
    }
    @Override
    public void cancelOrder(BookOrder order) {
            orders.remove(order);
            order.setStatus("Отменен");
            books.add(order.getBookCopy());
            order.setBookCopy(null);
            order.getBook().setStatusStok();
            requests.removeIf(request -> request.getOrder().equals(order));
    }
    
    //функции для просмотра списка книг с разными видами сортировки
    @Override
    public void SortByABC(){}
    @Override
    public void SortByPublicationDate(){}
    @Override
    public void SortByPrice(){}
    @Override
    public void SortByNumberCopies(){}

    @Override
    public String showBookInformation(Book book){
       return book.getInfo();
    }


    //функции для просмотра списка заказов с разными видами сортировки
    @Override
    public void showOrdersByDate(){}
    @Override
    public void showOrdersByPrice(){}
    @Override
    public void showOrdersByStatus(){}
}