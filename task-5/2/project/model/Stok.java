package project.model;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class Stok implements IShowBook, IOrderOperation, IBookStok {
    private final List<BookCopy> booksCopy = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();
    private final List<BookOrder> orders = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    
    public void addBookToCatalog(Book book) {
        boolean bookExists = books.stream()
            .anyMatch(b -> b.getName().equals(book.getName()) && b.getAuthor().equals(book.getAuthor()));
            
        if (!bookExists) {
            books.add(book);
            System.out.println("Книга добавлена в каталог: " + book.getName() + " | Всего в каталоге: " + books.size());
        } else {
            System.out.println("Книга уже есть в каталоге: " + book.getName());
        }
    }
    
    @Override
    public void addBookToStock(Book book, LocalDate date) {
        addBookToCatalog(book);
        BookCopy newBook = new BookCopy(book, date);
        booksCopy.add(newBook);
        book.setStatusStok();     
        
        System.out.println("Добавлена книга на склад: " + book.getName() + 
                      " | Копий: " + countBookCopies(book) + 
                      " | Книг в каталоге: " + books.size());
        
        List<Request> requestsToRemove = requests.stream()
            .filter(request -> request.getBook().equals(book))
            .collect(Collectors.toList());
            
        Set<BookOrder> ordersToUpdate = requestsToRemove.stream()
            .map(this::findOrderByRequest)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        requestsToRemove.forEach(request -> {
            request.ContinueRequest(newBook);
            removeBookFromStock(newBook);
        });
        
        requests.removeAll(requestsToRemove);
        
        ordersToUpdate.forEach(order -> {
            updateOrderStatus(order);
            System.out.println("Обновлен статус заказа #" + order.getOrderId() + 
                          " на: " + order.getStatus());
        });
        
        if (!requestsToRemove.isEmpty()) {
            System.out.println("Выполнено запросов: " + requestsToRemove.size() + 
                          " для книги: " + book.getName());
        }
    }
    
    private BookOrder findOrderByRequest(Request request) {
        return orders.stream()
            .filter(order -> order.getOrderItems().contains(request.getOrderItem()))
            .findFirst()
            .orElse(null);
    }
    
    private int countBookCopies(Book book) {
        return (int) booksCopy.stream()
                .filter(copy -> copy.getBook().equals(book))
                .count();
    }
    
    @Override
    public void removeBookFromStock(BookCopy book) {
        booksCopy.remove(book);
        boolean hasOtherCopies = booksCopy.stream()
            .anyMatch(copy -> copy.getBook().equals(book.getBook()));
        if (!hasOtherCopies) {
            book.getBook().setStatusNo();
        }
    }
    
    @Override
    public BookOrder createOrder(List<Book> bookList, String customerName, String customerContact) {
        BookOrder bookOrder = new BookOrder(customerName, customerContact);
        bookList.stream()
            .map(this::createOrderItem)
            .forEach(bookOrder::addBookToOrder);
            
        orders.add(bookOrder);
        updateOrderStatus(bookOrder);
       
        System.out.println("Создан заказ #" + bookOrder.getOrderId() + 
                         " на " + bookList.size() + " книг(и)");
        return bookOrder;
    }
   
    private BookOrderItem createOrderItem(Book book) {
        BookCopy bookCopy = findBook(book);
        BookOrderItem orderItem = new BookOrderItem(book);
        
        if (bookCopy != null) {
            orderItem.setBookCopy(bookCopy);
            orderItem.setStatus(OrderItemStatus.COMPLETED);
            System.out.println("Продан экземпляр книги: " + bookCopy);
            booksCopy.remove(bookCopy);
            if (findBook(book) == null) {
                book.setStatusNo();
            }
        } else {
            Request request = new Request(orderItem);
            requests.add(request);
            orderItem.setStatus(OrderItemStatus.PENDING);
            System.out.println("Книга отсутствует. Создан запрос: " + book.getName());
        }
        return orderItem;
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
    
    private BookCopy findBook(Book book) {
        return booksCopy.stream()
            .filter(copy -> copy.getBook().equals(book))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public void cancelOrder(BookOrder order) {
        orders.remove(order);
        order.setStatus(OrderStatus.CANCELLED);
        
        order.getOrderItems().forEach(orderItem -> {
            if (orderItem.getBookCopy() != null) {
                booksCopy.add(orderItem.getBookCopy());
                orderItem.getBook().setStatusStok();
            }
            requests.removeIf(request -> request.getOrderItem().equals(orderItem));
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
    
    @Override
    public void showOrdersByDate() {
        List<BookOrder> sortedOrders = orders.stream()
            .sorted(Comparator.comparing(BookOrder::getOrderDate))
            .collect(Collectors.toList());
            
        System.out.println("Заказы по дате:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getOrderId() + " | " + 
                             order.getOrderDate() + " | " + order.getStatus()));
    }
    
    @Override
    public void showOrdersByPrice() {
        List<BookOrder> sortedOrders = orders.stream()
            .sorted(Comparator.comparing(BookOrder::getTotalPrice))
            .collect(Collectors.toList());
            
        System.out.println("Заказы по цене:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getOrderId() + " | " + 
                             order.getTotalPrice() + " руб. | " + order.getStatus()));
    }
    
    @Override
    public void showOrdersByStatus() {
        List<BookOrder> sortedOrders = orders.stream()
            .sorted(Comparator.comparing(BookOrder::getStatus))
            .collect(Collectors.toList());
            
        System.out.println("Заказы по статусу:");
        sortedOrders.forEach(order -> 
            System.out.println(" - " + order.getOrderId() + " | " + order.getStatus() +
                             " | " + order.getOrderItems().size() + " книг(и)"));
    }
    
    @Override
    public void showRequestsByCount() {
        Map<Book, Long> requestCount = requests.stream()
            .collect(Collectors.groupingBy(
                Request::getBook,
                Collectors.counting()
            ));
        
        requestCount.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> 
                System.out.println(" - " + entry.getKey().getName() + 
                             " - " + entry.getValue() + " запросов"));
    }
    
    @Override
    public void showRequestsByAlphabet() {
        Map<Book, Long> requestedBooks = requests.stream()
            .collect(Collectors.groupingBy(
                Request::getBook,
                Collectors.counting()
            ));
            
        requestedBooks.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Book::getName)))
            .forEach(entry -> 
                System.out.println(" - " + entry.getKey().getName() + 
                                 " - " + entry.getValue() + " запросов"));
    }
    
    @Override
    public List<BookOrder> getCompletedOrdersByPeriod(LocalDate start, LocalDate end) {
        return orders.stream()
            .filter(order -> OrderStatus.COMPLETED.equals(order.getStatus()))
            .filter(order -> !order.getOrderDate().isBefore(start) && 
                           !order.getOrderDate().isAfter(end))
            .sorted(Comparator.comparing(BookOrder::getOrderDate))
            .collect(Collectors.toList());
    }
    
    @Override
    public double getEarnedMoneyByPeriod(LocalDate start, LocalDate end) {
        return getCompletedOrdersByPeriod(start, end).stream()
            .mapToDouble(BookOrder::getTotalPrice)
            .sum();
    }
    
    @Override
    public int getCompletedOrdersCountByPeriod(LocalDate start, LocalDate end) {
        return (int) getCompletedOrdersByPeriod(start, end).stream()
            .count();
    }
    
    @Override
    public void showOrderDetails(BookOrder order) {
        System.out.println("=== Детали заказа #" + order.getOrderId() + " ===");
        System.out.println("Клиент: " + order.getCustomerName());
        System.out.println("Контакт: " + order.getCustomerContact());
        System.out.println("Дата: " + order.getOrderDate());
        System.out.println("Статус: " + order.getStatus());
        System.out.println("Общая стоимость: " + order.getTotalPrice() + " руб.");
        System.out.println("Книги в заказе:");
        
        order.getOrderItems().forEach(item -> 
            System.out.println(" - " + item.getBook().getName() + 
                             " | " + item.getStatus() + " | " + 
                             item.getPrice() + " руб."));
    }
    
    @Override
    public List<BookCopy> getOldBooksSortedByDate() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return booksCopy.stream()
            .filter(copy -> copy.getArrivalDate().isBefore(sixMonthsAgo))
            .sorted(Comparator.comparing(BookCopy::getArrivalDate))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookCopy> getOldBooksSortedByPrice() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return booksCopy.stream()
            .filter(copy -> copy.getArrivalDate().isBefore(sixMonthsAgo))
            .sorted(Comparator.comparing(copy -> copy.getBook().getPrice()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void showOldBooks() {
        List<BookCopy> oldBooks = getOldBooksSortedByDate();
        System.out.println("Залежавшиеся книги (более 6 месяцев):");
        if (oldBooks.isEmpty()) {
            System.out.println(" - Нет залежавшихся книг");
        } else {
            oldBooks.forEach(copy -> 
                System.out.println(" - " + copy.getBook() + " | Поступление: " + 
                                 copy.getArrivalDate() + " | Цена: " + 
                                 copy.getBook().getPrice() + " руб."));
        }
    }
    
    @Override
    public void sortByABC() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = books.stream()
            .sorted(Comparator.comparing(Book::getName))
            .collect(Collectors.toList());
            
        System.out.println("Книги по алфавиту:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName()));
    }

    @Override
    public void sortByPublicationDate() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = books.stream()
            .sorted(Comparator.comparing(Book::getPublicationDate))
            .collect(Collectors.toList());
            
        System.out.println("Книги по дате публикации:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName() + " (" + book.getPublicationDate() + ")"));
    }

    @Override
    public void sortByPrice() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        List<Book> sortedBooks = books.stream()
            .sorted(Comparator.comparing(Book::getPrice))
            .collect(Collectors.toList());
            
        System.out.println("Книги по цене:");
        sortedBooks.forEach(book -> 
            System.out.println(" - " + book.getName() + " - " + book.getPrice() + " руб."));
    }
    
    @Override
    public void sortByNumberCopies() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        
        Map<Book, Long> bookCount = booksCopy.stream()
            .collect(Collectors.groupingBy(
                BookCopy::getBook,
                Collectors.counting()
            ));
        
        List<Book> sortedBooks = books.stream()
            .sorted((b1, b2) -> Long.compare(
                bookCount.getOrDefault(b2, 0L),
                bookCount.getOrDefault(b1, 0L)
            ))
            .collect(Collectors.toList());
        
        System.out.println("Книги по количеству экземпляров:");
        sortedBooks.forEach(book -> {
            long count = bookCount.getOrDefault(book, 0L);
            System.out.println(" - " + book.getName() + " - " + count + " шт.");
        });
    }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }
    
    public List<BookOrder> getOrders() { 
        return new ArrayList<>(orders); 
    }
    
    public List<Request> getRequests() { 
        return new ArrayList<>(requests); 
    }
    
    public List<BookCopy> getBooksCopy() { 
        return new ArrayList<>(booksCopy); 
    }
    
    public List<Book> getBooks() { 
        return new ArrayList<>(books); 
    }
}