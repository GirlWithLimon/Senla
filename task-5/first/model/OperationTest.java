package first.model;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class OperationTest {
    public static void main(String[] args) {
        Operation operation = new Operation();

        Book book1 = new Book("Война и мир", "Л.Н.Толстой", 250.0, LocalDate.of(2014, 10, 24));
        Book book2 = new Book("Мастер и Маргарита", "М.А.Булгаков", 260.0, LocalDate.of(2014, 10, 24));
        Book book3 = new Book("Преступление и наказание", "Ф.М.Достоевский", 200.0, LocalDate.of(2015, 5, 10));
        Book book4 = new Book("1984", "Дж.Оруэлл", 300.0, LocalDate.of(2019, 1, 15));
        
        // Добавляем залежавшуюся книгу для теста
        Book book5 = new Book("Старая книга", "Автор", 150.0, LocalDate.of(2020, 1, 1));
        operation.addBookToStock(book5, LocalDate.now().minusMonths(8));
        
        operation.addBookToStock(book1, LocalDate.now().minusMonths(3));
        operation.addBookToStock(book2, LocalDate.now().minusMonths(7));
        operation.addBookToStock(book1, LocalDate.now().minusMonths(1));
        operation.addBookToStock(book3, LocalDate.now().minusMonths(2));
        
        
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ СОРТИРОВКИ КНИГ ===");
        operation.showBooksByABC();
        operation.showBooksByPrice();
        operation.showBooksByNumberCopies();
        
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАКАЗОВ ===");
        List<Book> orderBooks = Arrays.asList(book1);
        operation.createOrder(orderBooks, "Иван Иванов", "ivan@mail.com");
        List<Book> order1Books = Arrays.asList(book1, book2, book4);
        operation.createOrder(order1Books, "Иван Иванов", "ivan@mail.com");
        
        List<Book> order2Books = Arrays.asList(book3, book1);
        BookOrder order1 = operation.createOrder(order2Books, "Петр Петров", "petr@mail.com");
        operation.addBookToStock(book1, LocalDate.now());
        System.out.println("\n=== ИНФОРМАЦИЯ О ЗАКАЗАХ ===");
        operation.showOrdersByStatus();
        operation.showOrdersByPrice();
        
        System.out.println("\n=== ЗАПРОСЫ ===");
        operation.showRequestsByCount();
        operation.showRequestsByAlphabet();
        
        System.out.println("\n=== ЗАЛЕЖАВШИЕСЯ КНИГИ ===");
        operation.showOldBooks();
        
        System.out.println("\n=== ДЕТАЛИ ПЕРВОГО ЗАКАЗА ===");
        operation.showOrderDetails(order1);
        
        System.out.println("\n=== ИНФОРМАЦИЯ О КНИГЕ ===");
        System.out.println(operation.showBookInformation(book1));
        
        System.out.println("\n=== СТАТИСТИКА ЗА ПЕРИОД ===");
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();
        operation.showCompletedOrdersByPeriod(start, end);
        operation.showEarnedMoneyByPeriod(start, end);
        operation.showCompletedOrdersCountByPeriod(start, end);
    }
}