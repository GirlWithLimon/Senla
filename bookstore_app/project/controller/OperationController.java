package bookstore_app.project.controller;

import bookstore_app.config.annotation.Component;
import bookstore_app.config.annotation.Inject;
import bookstore_app.project.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OperationController {
    @Inject
    private Stok stok;
    @Inject
    private IBookStok bookInStok;
    @Inject
    private IShowBook showBook;
    @Inject
    private IShowOrdersAndRequests showOrdersAndRequests;
    @Inject
    private IOrderOperation orderOperation;
    @Inject
    private ImportExportService importExportService;
    @Inject
    private DataSave dataSave;

    public OperationController() {
        setupShutdownHook();
    }
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nСохранение состояния программы...");
            dataSave.saveState(stok);
            System.out.println("Состояние успешно сохранено.");
        }));
    }
    public void initializeTestData() {
        if (!stok.getBooks().isEmpty() || !stok.getOrders().isEmpty()) {
            System.out.println("Загружены сохраненные данные:");
            System.out.println("- Книг в каталоге: " + stok.getBooks().size());
            System.out.println("- Экземпляров на складе: " + stok.getBooksCopy().size());
            System.out.println("- Активных заказов: " + stok.getOrders().size());
            System.out.println("- Активных запросов: " + stok.getRequests().size());
            return;
        }
        else {
            Book book1 = addBookToStock("Война и мир", "Л.Н.Толстой", 250.0, LocalDate.of(2014, 10, 24), LocalDate.now());
            Book book2 = addBookToStock("Мастер и Маргарита", "М.А.Булгаков", 260.0, LocalDate.of(2014, 10, 24), LocalDate.now().minusMonths(1));
            Book book3 = addBookToStock("Преступление и наказание", "Ф.М.Достоевский", 200.0, LocalDate.of(2015, 5, 10), LocalDate.now().minusYears(1));
            Book book4 = addBookToStock("1984", "Дж.Оруэлл", 300.0, LocalDate.of(2019, 1, 15), LocalDate.now().minusMonths(5));

            Book book5 = addBookToStock("Старая книга", "Автор", 150.0, LocalDate.of(2020, 1, 1), LocalDate.now().minusMonths(24));
            BookCopy bookCopy = addBookCopyToStock(1, LocalDate.now());
            List<Book> order1Books = List.of(book1, book2);
            createOrder(order1Books, "Иван Иванов", "ivan@mail.com");

            List<Book> order2Books = List.of(book3, book4);
            createOrder(order2Books, "Петр Петров", "petr@mail.com");

            System.out.println("Тестовые данные инициализированы!");
        }
    }

    public Book addBookToStock(int id, String name, String author, Double price, LocalDate datePublication, LocalDate date) {
        Book book = new Book(id, name, author, price, datePublication);
        bookInStok.addBookToStock(id, book, date);
        return book;
    }

    public Book addBookToStock(String name, String author, Double price, LocalDate datePublication, LocalDate date) {
        int id = stok.getBooks().isEmpty() ? 1 : stok.getBooks().getLast().getId() + 1;
        Book book = new Book(id, name, author, price, datePublication);
        bookInStok.addBookToStock(id, book, date);
        return book;
    }
    public BookCopy addBookCopyToStock(int idBook, LocalDate date) {
        int id = stok.getBooksCopy().isEmpty() ? 1 : stok.getBooksCopy().getLast().getId() + 1;
        Book book = stok.getBooks().stream().filter(books -> books.getId()==idBook)
                .findFirst().orElse(null);

        BookCopy bookCopy = new BookCopy(id,book,date);
        bookInStok.addBookCopyToStock(id, bookCopy, date);
        return bookCopy;
    }

    public void removeBookFromStock(BookCopy bookCopy) {
        bookInStok.removeBookCopyfromstock(bookCopy);
    }

    public List<Book> getBooks(){
        return  showBook.sortABCBook();
    }

    public void showSortABCBook() {
        showBook.showAllBook();
    }
    public void showSortBook() {
        showBook.showBook();
    }
    public String showBookInformation(Book book) {
        return bookInStok.showBookInformation(book);
    }

    public void checkOrderDate(String booksInput, String customerName, String customerContact){
        List<Book> selectedBooks = Arrays.stream(booksInput.split(","))
                .map(String::trim)
                .map(indexStr -> {
                    try {
                        int index = Integer.parseInt(indexStr);
                        return index > 0 && index <= showBook.sortABCBook().size() ? showBook.sortABCBook().get(index - 1) : null;
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный номер: " + indexStr);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!selectedBooks.isEmpty()) {
            BookOrder order = createOrder(selectedBooks, customerName, customerContact);
            System.out.println("Заказ создан! ID: " + order.getId());
        } else {
            System.out.println("Не выбрано ни одной книги!");
        }
    }

    public  List<BookOrder> getOrder(){
        return showOrdersAndRequests.sortOrderByDate();
    }

    public BookOrder createOrder(int id, List<Book> books, String customerName, String customerContact) {
        return orderOperation.createOrder(id, books, customerName, customerContact);
    }

    public BookOrder createOrder(List<Book> books, String customerName, String customerContact) {
        int id = stok.getOrders().isEmpty()? 1 : stok.getOrders().getLast().getId()+1;
        return orderOperation.createOrder(id, books, customerName, customerContact);
    }

    public void cancelOrder(BookOrder order) {
        orderOperation.cancelOrder(order);
    }

    public void cancelOrderItem(BookOrder order, Book book) {
        orderOperation.cancelOrderItem(order, book);
    }

    public void showBooksByABC() {
        showBook.sortByABC();
    }

    public void showBooksByPublicationDate() {
        showBook.sortByPublicationDate();
    }

    public void showBooksByPrice() {
        showBook.sortByPrice();
    }

    public void showBooksByNumberCopies() {
        showBook.sortByNumberCopies();
    }

    public void showOldBooksSortedByDate() {
        showBook.showOldBooksByDate();
    }

    public void showOldBooksSortedByPrice() {
        showBook.showOldBooksByPrice();
    }

    public void showRequestsByCount() {
        showOrdersAndRequests.showRequestsByCount();
    }

    public void showRequestsByAlphabet() {
        showOrdersAndRequests.showRequestsByAlphabet();
    }

    public void showOrderDetails(BookOrder order) {
        showOrdersAndRequests.showOrderDetails(order);
    }

    public void showOrdersByDate() {
        showOrdersAndRequests.showOrdersByDate();
    }

    public void showOrdersByPrice() {
        showOrdersAndRequests.showOrdersByPrice();
    }

    public void showOrdersByStatus() {
        showOrdersAndRequests.showOrdersByStatus();
    }

    public void showCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        List<BookOrder> completedOrders = showOrdersAndRequests.getCompletedOrdersByPeriod(start, end);
        System.out.println("Выполненные заказы за период " + start + " - " + end + ":");
        completedOrders.forEach(order ->
                System.out.println(" - " + order.getId() + " | " +
                        order.getOrderDate() + " | " + order.getTotalPrice() + " руб."));
    }

    public void showEarnedMoneyByPeriod(LocalDate start, LocalDate end) {
        double earned = showOrdersAndRequests.getEarnedMoneyByPeriod(start, end);
        System.out.println("Заработанные средства за период " + start + " - " + end +
                ": " + earned + " руб.");
    }

    public void showCompletedOrdersCountByPeriod(LocalDate start, LocalDate end) {
        int count = showOrdersAndRequests.getCompletedOrdersCountByPeriod(start, end);
        System.out.println("Количество выполненных заказов за период " + start + " - " +
                end + ": " + count);
    }

    public void exportToCSV(String entityType, String filePath) throws IOException {
        importExportService.exportEntities(entityType, filePath);
    }

    public void importFromCSV(String entityType, String filePath) throws IOException {
        System.out.println("Начало импорта " + entityType + " из " + filePath);
        System.out.println("До импорта - Книги: " + stok.getBooks().size() +
                ", Заказы: " + stok.getOrders().size());

        importExportService.importEntities(entityType, filePath);

        System.out.println("После импорта - Книги: " + stok.getBooks().size() +
                ", Заказы: " + stok.getOrders().size());
    }

    public String getAvailableEntityTypes() {
        return importExportService.getAvailableEntityTypes();
    }
}