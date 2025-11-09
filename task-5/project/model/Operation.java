package first.model;
import java.time.LocalDate;
import java.util.List;

public class Operation {
    private final IBookStok bookStok;
    private final IShowBook bookShow;
    private final IOrderOperation orderOperation;
    
    public Operation(IBookStok bookStok, IShowBook bookShow, IOrderOperation orderOperation) {
        this.bookStok = bookStok;
        this.bookShow = bookShow;
        this.orderOperation = orderOperation;
    }
     

    public void addBookToStock(Book book, LocalDate date) {
        bookStok.addBookToStock(book, date);
    }
    
    public void removeBookFromStock(BookCopy bookCopy) {
        bookStok.removeBookFromStock(bookCopy);
    }
    
    public String showBookInformation(Book book) {
        return bookShow.showBookInformation(book);
    }
    
    public BookOrder createOrder(List<Book> books, String customerName, String customerContact) {
        return orderOperation.createOrder(books, customerName, customerContact);
    }
    
    public void cancelOrder(BookOrder order) {
        orderOperation.cancelOrder(order);
    }
    
    public void cancelOrderItem(BookOrder order, Book book) {
        orderOperation.cancelOrderItem(order, book);
    }
    
    public void showBooksByABC() {
        bookShow.sortByABC();
    }
    
    public void showBooksByPublicationDate() {
        bookShow.sortByPublicationDate();
    }
    
    public void showBooksByPrice() {
        bookShow.sortByPrice();
    }
    
    public void showBooksByNumberCopies() {
        bookShow.sortByNumberCopies();
    }
    
    public void showOldBooks() {
        bookShow.showOldBooks();
    }

    public void showRequestsByCount() {
        orderOperation.showRequestsByCount();
    }
    
    public void showRequestsByAlphabet() {
        orderOperation.showRequestsByAlphabet();
    }
    
    public void showOrderDetails(BookOrder order) {
        orderOperation.showOrderDetails(order);
    }
    
    public void showOrdersByDate() {
        orderOperation.showOrdersByDate();
    }
    
    public void showOrdersByPrice() {
        orderOperation.showOrdersByPrice();
    }
    
    public void showOrdersByStatus() {
        orderOperation.showOrdersByStatus();
    }
    
    public void showCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        List<BookOrder> completedOrders = orderOperation.getCompletedOrdersByPeriod(start, end);
        System.out.println("Выполненные заказы за период " + start + " - " + end + ":");
        for (BookOrder order : completedOrders) {
            System.out.println(" - " + order.getOrderId() + " | " + 
                             order.getOrderDate() + " | " + order.getTotalPrice() + " руб.");
        }
    }
    
    public void showEarnedMoneyByPeriod(LocalDate start, LocalDate end) {
        double earned = orderOperation.getEarnedMoneyByPeriod(start, end);
        System.out.println("Заработанные средства за период " + start + " - " + end + 
                          ": " + earned + " руб.");
    }
    
    public void showCompletedOrdersCountByPeriod(LocalDate start, LocalDate end) {
        int count = orderOperation.getCompletedOrdersCountByPeriod(start, end);
        System.out.println("Количество выполненных заказов за период " + start + " - " + 
                          end + ": " + count);
    }
    
    public void showOldBooksSortedByDate() {
        List<BookCopy> oldBooks = bookShow.getOldBooksSortedByDate();
        System.out.println("Залежавшиеся книги по дате поступления:");
        for (BookCopy copy : oldBooks) {
            System.out.println(" - " + copy.getBook() + " | Поступление: " + 
                             copy.getArrivalDate() + " | Цена: " + 
                             copy.getBook().getPrice() + " руб.");
        }
    }
    
    public void showOldBooksSortedByPrice() {
        List<BookCopy> oldBooks = bookShow.getOldBooksSortedByPrice();
        System.out.println("Залежавшиеся книги по цене:");
        for (BookCopy copy : oldBooks) {
            System.out.println(" - " + copy.getBook() + " | Цена: " + 
                             copy.getBook().getPrice() + " руб. | Поступление: " + 
                             copy.getArrivalDate());
        }
    }
    
    
}