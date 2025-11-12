package project.view;

import project.controller.OperationController;
import project.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class MenuBuilder {
    private static MenuBuilder instance;
    private Menu rootMenu;
    private final OperationController controller;
    private final Scanner scanner;
    
    private MenuBuilder() {
        this.controller = new OperationController();
        this.scanner = new Scanner(System.in);
        this.controller.initializeTestData();
        buildMenu();
    }
    
    public static MenuBuilder getInstance() {
        if (instance == null) {
            instance = new MenuBuilder();
        }
        return instance;
    }
    
    public void buildMenu() {
        rootMenu = new Menu("Главное меню");
        
        Menu stockMenu = createStockMenu();
        Menu orderMenu = createOrderMenu();
        Menu reportMenu = createReportMenu();
        Menu searchMenu = createSearchMenu();
        
        rootMenu.addMenuItem(new MenuItem("Управление складом", stockMenu));
        rootMenu.addMenuItem(new MenuItem("Управление заказами", orderMenu));
        rootMenu.addMenuItem(new MenuItem("Отчеты и аналитика", reportMenu));
        rootMenu.addMenuItem(new MenuItem("Поиск и сортировка", searchMenu));
    }
    
    private Menu createStockMenu() {
        Menu menu = new Menu("Управление складом");
        
        menu.addMenuItem(new MenuItem("Добавить книгу на склад", () -> {
            System.out.println("\n=== Добавление книги на склад ===");
            System.out.print("Название книги: ");
            String name = scanner.nextLine();
            System.out.print("Автор: ");
            String author = scanner.nextLine();
            System.out.print("Цена: ");
            double price = scanner.nextDouble();
            scanner.nextLine();
            
            Book book = new Book(name, author, price, LocalDate.now());
            controller.addBookToStock(book, LocalDate.now());
            System.out.println("Книга добавлена на склад!");
        }));
        
        menu.addMenuItem(new MenuItem("Показать информацию о книге", () -> {
            System.out.println("\n=== Информация о книге ===");
            List<Book> allBooks = controller.getAllBooks();
            if (allBooks.isEmpty()) {
                System.out.println("В каталоге нет книг!");
                return;
            }
            
            System.out.println("Доступные книги:");
            for (int i = 0; i < allBooks.size(); i++) {
                Book book = allBooks.get(i);
                System.out.println((i + 1) + ". " + book.getName() + " - " + book.getAuthor());
            }
            
            System.out.print("Выберите номер книги: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice > 0 && choice <= allBooks.size()) {
                Book selectedBook = allBooks.get(choice - 1);
                String info = controller.showBookInformation(selectedBook);
                System.out.println("Информация о книге: " + info);
            } else {
                System.out.println("Неверный выбор!");
            }
        }));
        
        menu.addMenuItem(new MenuItem("Показать залежавшиеся книги", () -> {
            System.out.println("\n=== Залежавшиеся книги ===");
            controller.showOldBooks();
        }));
        
        return menu;
    }
    
    private Menu createOrderMenu() {
        Menu menu = new Menu("Управление заказами");
        
        menu.addMenuItem(new MenuItem("Создать новый заказ", () -> {
            System.out.println("\n=== Создание нового заказа ===");
            System.out.print("Имя клиента: ");
            String customerName = scanner.nextLine();
            System.out.print("Контактные данные: ");
            String customerContact = scanner.nextLine();
            
            List<Book> allBooks = controller.getAllBooks();
            if (allBooks.isEmpty()) {
                System.out.println("В каталоге нет книг для заказа!");
                return;
            }
            
            System.out.println("Доступные книги:");
            for (int i = 0; i < allBooks.size(); i++) {
                Book book = allBooks.get(i);
                System.out.println((i + 1) + ". " + book.getName() + " - " + book.getAuthor() + " - " + book.getPrice() + " руб.");
            }
            
            System.out.print("Выберите номера книг через запятую: ");
            String booksInput = scanner.nextLine();
            
            List<Book> selectedBooks = Arrays.stream(booksInput.split(","))
                .map(String::trim)
                .map(indexStr -> {
                    try {
                        int index = Integer.parseInt(indexStr);
                        return index > 0 && index <= allBooks.size() ? allBooks.get(index - 1) : null;
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный номер: " + indexStr);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            if (!selectedBooks.isEmpty()) {
                BookOrder order = controller.createOrder(selectedBooks, customerName, customerContact);
                System.out.println("Заказ создан! ID: " + order.getOrderId());
            } else {
                System.out.println("Не выбрано ни одной книги!");
            }
        }));
        
        menu.addMenuItem(new MenuItem("Отменить заказ", () -> {
            System.out.println("\n=== Отмена заказа ===");
            List<BookOrder> allOrders = controller.getAllOrders();
            if (allOrders.isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }
            
            System.out.println("Активные заказы:");
            for (int i = 0; i < allOrders.size(); i++) {
                BookOrder order = allOrders.get(i);
                System.out.println((i + 1) + ". ID: " + order.getOrderId() + 
                                 " | Клиент: " + order.getCustomerName() + 
                                 " | Статус: " + order.getStatus());
            }
            
            System.out.print("Выберите номер заказа для отмены: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice > 0 && choice <= allOrders.size()) {
                BookOrder selectedOrder = allOrders.get(choice - 1);
                controller.cancelOrder(selectedOrder);
                System.out.println("Заказ отменен!");
            } else {
                System.out.println("Неверный выбор!");
            }
        }));
        
        menu.addMenuItem(new MenuItem("Показать все заказы", () -> {
            System.out.println("\n=== Все заказы ===");
            controller.showOrdersByStatus();
        }));
        
        menu.addMenuItem(new MenuItem("Детали заказа", () -> {
            System.out.println("\n=== Детали заказа ===");
            List<BookOrder> allOrders = controller.getAllOrders();
            if (allOrders.isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }
            
            System.out.println("Активные заказы:");
            for (int i = 0; i < allOrders.size(); i++) {
                BookOrder order = allOrders.get(i);
                System.out.println((i + 1) + ". ID: " + order.getOrderId() + 
                                 " | Клиент: " + order.getCustomerName());
            }
            
            System.out.print("Выберите номер заказа: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice > 0 && choice <= allOrders.size()) {
                BookOrder selectedOrder = allOrders.get(choice - 1);
                controller.showOrderDetails(selectedOrder);
            } else {
                System.out.println("Неверный выбор!");
            }
        }));
        
        return menu;
    }
    
    private Menu createReportMenu() {
        Menu menu = new Menu("Отчеты и аналитика");
        
        menu.addMenuItem(new MenuItem("Заказы по дате", () -> {
            System.out.println("\n=== Заказы по дате ===");
            controller.showOrdersByDate();
        }));
        
        menu.addMenuItem(new MenuItem("Заказы по статусу", () -> {
            System.out.println("\n=== Заказы по статусу ===");
            controller.showOrdersByStatus();
        }));
        
        menu.addMenuItem(new MenuItem("Заказы по цене", () -> {
            System.out.println("\n=== Заказы по цене ===");
            controller.showOrdersByPrice();
        }));
        
        menu.addMenuItem(new MenuItem("Статистика за период", () -> {
            System.out.println("\n=== Статистика за период ===");
            System.out.print("Начальная дата (гггг-мм-дд): ");
            String startStr = scanner.nextLine();
            System.out.print("Конечная дата (гггг-мм-дд): ");
            String endStr = scanner.nextLine();
            
            try {
                LocalDate start = LocalDate.parse(startStr);
                LocalDate end = LocalDate.parse(endStr);
                
                controller.showCompletedOrdersByPeriod(start, end);
                controller.showEarnedMoneyByPeriod(start, end);
                controller.showCompletedOrdersCountByPeriod(start, end);
            } catch (Exception e) {
                System.out.println("Ошибка в формате даты! Используйте формат гггг-мм-дд");
            }
        }));
        
        return menu;
    }
    
    private Menu createSearchMenu() {
        Menu menu = new Menu("Поиск и сортировка");
        
        menu.addMenuItem(new MenuItem("Книги по алфавиту", () -> {
            System.out.println("\n=== Книги по алфавиту ===");
            controller.showBooksByABC();
        }));
        
        menu.addMenuItem(new MenuItem("Книги по цене", () -> {
            System.out.println("\n=== Книги по цене ===");
            controller.showBooksByPrice();
        }));
        
        menu.addMenuItem(new MenuItem("Книги по дате публикации", () -> {
            System.out.println("\n=== Книги по дате публикации ===");
            controller.showBooksByPublicationDate();
        }));
        
        menu.addMenuItem(new MenuItem("Книги по количеству экземпляров", () -> {
            System.out.println("\n=== Книги по количеству экземпляров ===");
            controller.showBooksByNumberCopies();
        }));
        
        menu.addMenuItem(new MenuItem("Запросы по алфавиту", () -> {
            System.out.println("\n=== Запросы по алфавиту ===");
            controller.showRequestsByAlphabet();
        }));
        
        menu.addMenuItem(new MenuItem("Запросы по количеству", () -> {
            System.out.println("\n=== Запросы по количеству ===");
            controller.showRequestsByCount();
        }));
        
        menu.addMenuItem(new MenuItem("Залежавшиеся книги по дате", () -> {
            System.out.println("\n=== Залежавшиеся книги по дате ===");
            controller.showOldBooksSortedByDate();
        }));
        
        menu.addMenuItem(new MenuItem("Залежавшиеся книги по цене", () -> {
            System.out.println("\n=== Залежавшиеся книги по цене ===");
            controller.showOldBooksSortedByPrice();
        }));
        
        return menu;
    }
    
    public Menu getRootMenu() {
        return rootMenu;
    }
}