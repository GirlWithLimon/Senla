package project.view;

import project.controller.OperationController;
import project.model.*;
import java.time.LocalDate;
import java.util.ArrayList; 
import java.util.List;
import java.util.Scanner;

public final class MenuBuilder {
    private static MenuBuilder instance;
    private Menu rootMenu;
    private final OperationController controller;
    private final Scanner scanner;
    
    private MenuBuilder() {
        this.controller = new OperationController();
        this.scanner = new Scanner(System.in, "UTF-8");
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
        // Главное меню
        rootMenu = new Menu("Главное меню");
        
        // Добавление пунктов в главное меню
        rootMenu.addMenuItem(new MenuItem("Добавление книги на склад", () -> {
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
        
        rootMenu.addMenuItem(new MenuItem("Создание заказа", (IAction) () -> {
            System.out.println("\n=== Создание нового заказа ===");
            System.out.print("Имя клиента: ");
            String customerName = scanner.nextLine();
            System.out.print("Контактные данные: ");
            String customerContact = scanner.nextLine();
            
            if (controller.getAllBooks().isEmpty()) {
                System.out.println("В каталоге нет книг для заказа!");
                return;
            }
            
            System.out.println("Доступные книги:");
            for (int i = 0; i < controller.getAllBooks().size(); i++) {
                Book book = controller.getAllBooks().get(i);
                System.out.println((i + 1) + ". " + book.getName() + " - " + book.getAuthor() + " - " + book.getPrice() + " руб.");
            }
            
            System.out.print("Выберите номера книг через запятую: ");
            String booksInput = scanner.nextLine();
            String[] bookIndexes = booksInput.split(",");
            
            List<Book> selectedBooks = new ArrayList<>();
            for (String indexStr : bookIndexes) {
                try {
                    int index = Integer.parseInt(indexStr.trim());
                    if (index > 0 && index <= controller.getAllBooks().size()) {
                        selectedBooks.add(controller.getAllBooks().get(index - 1));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный номер: " + indexStr);
                }
            }
            
            if (!selectedBooks.isEmpty()) {
                BookOrder order = controller.createOrder(selectedBooks, customerName, customerContact);
                System.out.println("Заказ создан! ID: " + order.getOrderId());
            } else {
                System.out.println("Не выбрано ни одной книги!");
            }
        }));
        
        // Создание подменю
        Menu bookInfoMenu = createBookInfoMenu();
        Menu orderManagementMenu = createOrderManagementMenu();
        
        // Добавление подменю в главное меню
        rootMenu.addMenuItem(new MenuItem("Информация про книги", bookInfoMenu));
        rootMenu.addMenuItem(new MenuItem("Управление заказами", orderManagementMenu));
    }
    
    private Menu createBookInfoMenu() {
        Menu menu = new Menu("Информация про книги");
        
        menu.addMenuItem(new MenuItem("Показать информацию о книге", () -> {
            System.out.println("\n=== Информация о книге ===");
            if (controller.getAllBooks().isEmpty()) {
                System.out.println("В каталоге нет книг!");
                return;
            }
            
            System.out.println("Доступные книги:");
            for (int i = 0; i < controller.getAllBooks().size(); i++) {
                Book book = controller.getAllBooks().get(i);
                System.out.println((i + 1) + ". " + book.getName() + " - " + book.getAuthor());
            }
            
            System.out.print("Выберите номер книги: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            if (choice > 0 && choice <= controller.getAllBooks().size()) {
                Book selectedBook = controller.getAllBooks().get(choice - 1);
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
    
    private Menu createOrderManagementMenu() {
        Menu menu = new Menu("Управление заказами");
        
        menu.addMenuItem(new MenuItem("Отменить заказ", (IAction) () -> {
            System.out.println("\n=== Отмена заказа ===");
            if (controller.getAllOrders().isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }
            
            System.out.println("Активные заказы:");
            for (int i = 0; i < controller.getAllOrders().size(); i++) {
                BookOrder order = controller.getAllOrders().get(i);
                System.out.println((i + 1) + ". ID: " + order.getOrderId() +
                        " | Клиент: " + order.getCustomerName() +
                        " | Статус: " + order.getStatus());
            }
            
            System.out.print("Выберите номер заказа для отмены: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            if (choice > 0 && choice <= controller.getAllOrders().size()) {
                BookOrder selectedOrder = controller.getAllOrders().get(choice - 1);
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
            if (controller.getAllOrders().isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }
            
            System.out.println("Активные заказы:");
            for (int i = 0; i < controller.getAllOrders().size(); i++) {
                BookOrder order = controller.getAllOrders().get(i);
                System.out.println((i + 1) + ". ID: " + order.getOrderId() +
                        " | Клиент: " + order.getCustomerName());
            }
            
            System.out.print("Выберите номер заказа: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice > 0 && choice <= controller.getAllOrders().size()) {
                BookOrder selectedOrder = controller.getAllOrders().get(choice - 1);
                controller.showOrderDetails(selectedOrder);
            } else {
                System.out.println("Неверный выбор!");
            }
        }));
        
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
        
        menu.addMenuItem(new MenuItem("Запросы по алфавиту", () -> {
            System.out.println("\n=== Запросы по алфавиту ===");
            controller.showRequestsByAlphabet();
        }));
        
        menu.addMenuItem(new MenuItem("Запросы по количеству", () -> {
            System.out.println("\n=== Запросы по количеству ===");
            controller.showRequestsByCount();
        }));
        
        return menu;
    }
    
    public Menu getRootMenu() {
        return rootMenu;
    }
}