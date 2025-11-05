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
        
        Set<BookOrder> ordersToUpdate = new HashSet<>();
        List<Request> requestsToRemove = new ArrayList<>();
        
        for (Request request : requests) {
            if (request.getBook().equals(book)) {
                request.ContinueRequest(newBook);
                removeBookFromStock(newBook);
                requestsToRemove.add(request);
                
                BookOrder order = findOrderByRequest(request);
                if (order != null) {
                    ordersToUpdate.add(order);
                }
            }
        }
        
        requests.removeAll(requestsToRemove);
        
        for (BookOrder order : ordersToUpdate) {
            updateOrderStatus(order);
            System.out.println("Обновлен статус заказа #" + order.getOrderId() + 
                          " на: " + order.getStatus());
        }
        
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
        for (Book book : bookList) {
            bookOrder.addBookToOrder(createOrderItem(book)); 
        }
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
        for (BookCopy bookCopy : booksCopy) {
            if (bookCopy.getBook().equals(book)) {
                return bookCopy;
            }
        }
        return null;
    }
    
    @Override
    public void cancelOrder(BookOrder order) {
        orders.remove(order);
        order.setStatus(OrderStatus.CANCELLED);
        for (BookOrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getBookCopy() != null) {
                booksCopy.add(orderItem.getBookCopy());
                orderItem.getBook().setStatusStok();
            }
            requests.removeIf(request -> request.getOrderItem().equals(orderItem));
        }
    }
    
    @Override
    public void cancelOrderItem(BookOrder order, Book book) {
        BookOrderItem itemToRemove = null;
        for (BookOrderItem item : order.getOrderItems()) {
            if (item.getBook().equals(book)) {
                if (item.getBookCopy() != null) {
                    booksCopy.add(item.getBookCopy());
                    item.getBook().setStatusStok();
                }
                requests.removeIf(request -> request.getOrderItem().equals(item));
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove != null) {
            order.getOrderItems().remove(itemToRemove);
            updateOrderStatus(order);
        }
    }
    
    @Override
    public void showOrdersByDate() {
        orders.sort(Comparator.comparing(BookOrder::getOrderDate));
        System.out.println("Заказы по дате:");
        for (BookOrder order : orders) {
            System.out.println(" - " + order.getOrderId() + " | " + 
                             order.getOrderDate() + " | " + order.getStatus());
        }
    }
    
    @Override
    public void showOrdersByPrice() {
        orders.sort(Comparator.comparing(BookOrder::getTotalPrice));
        System.out.println("Заказы по цене:");
        for (BookOrder order : orders) {
            System.out.println(" - " + order.getOrderId() + " | " + 
                             order.getTotalPrice() + " руб. | " + order.getStatus());
        }
    }
    
    @Override
    public void showOrdersByStatus() {
        orders.sort(Comparator.comparing(BookOrder::getStatus));
        System.out.println("Заказы по статусу:");
        for (BookOrder order : orders) {
            System.out.println(" - " + order.getOrderId() + " | " + order.getStatus() +
                             " | " + order.getOrderItems().size() + " книг(и)");
        }
    }
    
    @Override
    public void showRequestsByCount() {
        Map<Book, Integer> requestCount = new HashMap<>();
        for (Request request : requests) {
            requestCount.put(request.getBook(), 
                           requestCount.getOrDefault(request.getBook(), 0) + 1);
        }
        
        List<Map.Entry<Book, Integer>> sorted = new ArrayList<>(requestCount.entrySet());
        sorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        System.out.println("Запросы по количеству:");
        for (Map.Entry<Book, Integer> entry : sorted) {
            System.out.println(" - " + entry.getKey().getName() + 
                             " - " + entry.getValue() + " запросов");
        }
    }
    
    @Override
    public void showRequestsByAlphabet() {
        List<Book> requestedBooks = requests.stream()
            .map(Request::getBook)
            .distinct()
            .sorted(Comparator.comparing(Book::getName))
            .collect(Collectors.toList());
            
        System.out.println("Запросы по алфавиту:");
        for (Book book : requestedBooks) {
            long count = requests.stream()
                .filter(req -> req.getBook().equals(book))
                .count();
            System.out.println(" - " + book.getName() + " - " + count + " запросов");
        }
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
        return getCompletedOrdersByPeriod(start, end).size();
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
        
        for (BookOrderItem item : order.getOrderItems()) {
            System.out.println(" - " + item.getBook().getName() + 
                             " | " + item.getStatus() + " | " + 
                             item.getPrice() + " руб.");
        }
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
            for (BookCopy copy : oldBooks) {
                System.out.println(" - " + copy.getBook() + " | Поступление: " + 
                                 copy.getArrivalDate() + " | Цена: " + 
                                 copy.getBook().getPrice() + " руб.");
            }
        }
    }
    
    @Override
    public void sortByABC() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        books.sort(Comparator.comparing(Book::getName));
        System.out.println("Книги по алфавиту:");
        for (Book book : books) {
            System.out.println(" - " + book.getName());
        }
    }

    @Override
    public void sortByPublicationDate() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        books.sort(Comparator.comparing(Book::getPublicationDate));
        System.out.println("Книги по дате публикации:");
        for (Book book : books) {
            System.out.println(" - " + book.getName() + " (" + book.getPublicationDate() + ")");
        }
    }

    @Override
    public void sortByPrice() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        books.sort(Comparator.comparing(Book::getPrice));
        System.out.println("Книги по цене:");
        for (Book book : books) {
            System.out.println(" - " + book.getName() + " - " + book.getPrice() + " руб.");
        }
    }
    
    @Override
    public void sortByNumberCopies() {
        if (books.isEmpty()) {
            System.out.println("Каталог книг пуст");
            return;
        }
        
        Map<Book, Integer> bookCount = new HashMap<>();
        for (BookCopy copy : booksCopy) {
            bookCount.put(copy.getBook(), bookCount.getOrDefault(copy.getBook(), 0) + 1);
        }
        
        List<Book> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort((b1, b2) -> {
            int count1 = bookCount.getOrDefault(b1, 0);
            int count2 = bookCount.getOrDefault(b2, 0);
            return Integer.compare(count2, count1);
        });
        
        System.out.println("Книги по количеству экземпляров:");
        for (Book book : sortedBooks) {
            int count = bookCount.getOrDefault(book, 0);
            System.out.println(" - " + book.getName() + " - " + count + " шт.");
        }
    }

    @Override
    public String showBookInformation(Book book) {
        return book.getInfo();
    }
    
    public List<BookOrder> getOrders() { 
        return orders; 
    }
    
    public List<Request> getRequests() { 
        return requests; 
    }
    
    public List<BookCopy> getBooksCopy() { 
        return booksCopy; 
    }
    
    public List<Book> getBooks() { 
        return books; 
    }
}