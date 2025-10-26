import java.util.*;

public class Stok {
    private List<Book> books = new ArrayList<>();
    private List<BookOrder> request = new ArrayList<>();
    
    // Добавить книгу на склад
    public void addBookToStock(Book book) {
        books.add(book);
        book.setStatusStok();
        
        // Временный список для удаления
        List<BookOrder> toRemove = new ArrayList<>();
        
        for (BookOrder order : request) {
            if (order.getBook().equals(book) && order.getStatus().equals("Новый")) {
                order.setStatus("Выполнен");
                removeBookFromStock(book);
                System.out.println("Выдана книга по запросу: " + book.getName());
                toRemove.add(order); // Помечаем для удаления
                break;
            }
        }
        
        // Удаляем выполненные заказы
        request.removeAll(toRemove);
    }
    
    // Остальные методы без изменений
    public void removeBookFromStock(Book book) {
        book.setStatusNo();
    }
    
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
    
   public void cancelOrder(BookOrder order) {
        order.setStatus("Отменен");
        request.remove(order); // Важно: удаляем из списка запросов
    }
    
    public Integer countRequest(Book book){
        int count = 0;
        for(BookOrder order : request){
            if(order.getBook().equals(book)) count++;
        }
        System.out.println("Количество запросов на книгу: " + count);
        return count;
    }
}