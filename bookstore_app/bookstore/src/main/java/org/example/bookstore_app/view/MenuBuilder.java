package org.example.bookstore_app.view;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.controller.OperationController;

import java.time.LocalDate;
import java.util.Scanner;

@Component
public final class MenuBuilder {
    private volatile Menu rootMenu;
    private final BookstoreConfig config;
    private final OperationController controller;
    private final Scanner scanner;
    private final Object lock = new Object();

    @Inject
    public MenuBuilder(BookstoreConfig config, OperationController controller) {
        this.config = config;
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public Navigator createNavigator() {
        buildMenu();
        if (rootMenu == null) {
            System.err.println("ОШИБКА: rootMenu не создан!");
            rootMenu = new Menu("Главное меню");
            rootMenu.addMenuItem(new MenuItem("Тестовая функция", () ->
                    System.out.println("Меню работает!")));
        }

        Navigator navigator = new Navigator(rootMenu);

        try {
            ApplicationContext context = ApplicationContext.getInstance();
            context.registerBean(Navigator.class, navigator);
        } catch (Exception e) {
            System.err.println("Не удалось зарегистрировать Navigator в DI: " + e.getMessage());
        }

        return navigator;
    }

    private void buildMenu() {
        synchronized (lock) {
            if (rootMenu != null) {
                return;
            }

            System.out.println("Создаем меню...");

            try {
                rootMenu = new Menu("Главное меню");

                Menu stockMenu = createStockMenu();
                Menu orderMenu = createOrderMenu();
                Menu reportMenu = createReportMenu();
                Menu searchMenu = createSearchMenu();
                Menu importExportMenu = createImportExportMenu();
                Menu propertiesMenu = createPropertiesMenu();

                rootMenu.addMenuItem(new MenuItem("Управление складом", stockMenu));
                rootMenu.addMenuItem(new MenuItem("Управление заказами", orderMenu));
                rootMenu.addMenuItem(new MenuItem("Отчеты и аналитика", reportMenu));
                rootMenu.addMenuItem(new MenuItem("Поиск и сортировка", searchMenu));
                rootMenu.addMenuItem(new MenuItem("Импорт/Экспорт данных", importExportMenu));
                rootMenu.addMenuItem(new MenuItem("Настройки", propertiesMenu));

                System.out.println("Меню успешно создано с " + rootMenu.getMenuItems().size() + " пунктами");
            } catch (Exception e) {
                System.err.println("Ошибка при создании меню: " + e.getMessage());
                e.printStackTrace();
                rootMenu = new Menu("Главное меню");
                rootMenu.addMenuItem(new MenuItem("Тест", () -> System.out.println("Тест")));
            }
        }
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
            boolean dateParsing=false;
            LocalDate datePublication = LocalDate.now();
            while (!dateParsing) {
                System.out.print("Дата публикации (гггг-мм-дд): ");
                String dateString = scanner.nextLine();
                try {
                    datePublication = LocalDate.parse(dateString);
                    dateParsing = true;
                } catch (Exception e) {
                    System.out.println("Ошибка формата даты! Используйте гггг-мм-дд");
                }
            }
            controller.addBookToStock(name, author, price, datePublication, LocalDate.now());
            System.out.println("Книга добавлена на склад!");
        }));

        menu.addMenuItem(new MenuItem("Добавить экземпляр книги на склад", () -> {
            System.out.println("\n=== Добавление экземпляра книги на склад ===");
            controller.showSortBook();
            System.out.print("Выберите id книги: ");
            int booksInput=0;
            try{
                booksInput = scanner.nextInt();
                scanner.nextLine();
            }
            catch (Exception e) {
                System.out.println("Необходимо ввести целое число!");
                scanner.nextLine();
                return;
            }

            controller.addBookCopyToStock(booksInput, LocalDate.now());
        }));

        menu.addMenuItem(new MenuItem("Показать информацию о книге", () -> {
            System.out.println("\n=== Информация о книге ===");

            System.out.println("Доступные книги:");
            controller.showBooksByABC();

            System.out.print("Выберите номер книги: ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Необходимо ввести целое число!");
                scanner.nextLine();
                return;
            }

            if (choice > 0 && choice <= controller.getBooks().size()) {
                String info = controller.showBookInformation(controller.getBooks().get(choice-1));
                System.out.println("Информация о книге: " + info);
            } else {
                System.out.println("Неверный выбор!");
            }
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

            controller.showSortABCBook();
            System.out.print("Выберите номера книг через запятую: ");
            String booksInput = scanner.nextLine();

            controller.checkOrderDate(booksInput, customerName, customerContact);
        }));

        menu.addMenuItem(new MenuItem("Отменить заказ", () -> {
            System.out.println("\n=== Отмена заказа ===");
            controller.showOrdersByDate();

            if (controller.getOrder().isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }

            System.out.print("Выберите номер заказа для отмены: ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Необходимо ввести целое число!");
                scanner.nextLine();
                return;
            }
            controller.cancelOrder(choice);
            System.out.println("Заказ отменен!");
        }));

        menu.addMenuItem(new MenuItem("Показать все заказы", () -> {
            System.out.println("\n=== Все заказы ===");
            controller.showOrdersByStatus();
        }));

        menu.addMenuItem(new MenuItem("Детали заказа", () -> {
            System.out.println("\n=== Детали заказа ===");
            if (controller.getOrder().isEmpty()) {
                System.out.println("Нет активных заказов!");
                return;
            }

            System.out.println("Активные заказы:");
            for (int i = 0; i < controller.getOrder().size(); i++) {
                System.out.println((i + 1) + ". ID: " + controller.getOrder().get(i).getId() +
                        " | Клиент: " + controller.getOrder().get(i).getCustomerName());
            }

            System.out.print("Выберите номер заказа: ");
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Необходимо ввести целое число!");
                scanner.nextLine();
                return;
            }

            if (choice > 0 && choice <= controller.getOrder().size()) {
                controller.showOrderDetails(controller.getOrder().get(choice - 1));
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

    private Menu createImportExportMenu() {
        Menu menu = new Menu("Импорт/Экспорт данных");

        menu.addMenuItem(new MenuItem("Экспорт книг в CSV", () -> {
            System.out.println("\n=== Экспорт книг в CSV ===");
            System.out.print("Введите путь для сохранения файла (например: C:/books.csv): ");
            String filePath = scanner.nextLine();

            try {
                controller.exportToCSV("books", filePath);
                System.out.println("Книги успешно экспортированы в: " + filePath);
            } catch (Exception e) {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        menu.addMenuItem(new MenuItem("Импорт книг из CSV", () -> {
            System.out.println("\n=== Импорт книг из CSV ===");
            System.out.print("Введите путь к CSV файлу: ");
            String filePath = scanner.nextLine();

            try {
                controller.importFromCSV("books", filePath);
                System.out.println("Книги успешно импортированы из: " + filePath);
            } catch (Exception e) {
                System.out.println("Ошибка при импорте: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        menu.addMenuItem(new MenuItem("Экспорт заказов в CSV", () -> {
            System.out.println("\n=== Экспорт заказов в CSV ===");
            System.out.print("Введите путь для сохранения файла: ");
            String filePath = scanner.nextLine();

            try {
                controller.exportToCSV("orders", filePath);
                System.out.println("Заказы успешно экспортированы в: " + filePath);
            } catch (Exception e) {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        menu.addMenuItem(new MenuItem("Импорт заказов из CSV", () -> {
            System.out.println("\n=== Импорт заказов из CSV ===");
            System.out.print("Введите путь к CSV файлу: ");
            String filePath = scanner.nextLine();

            try {
                controller.importFromCSV("orders", filePath);
                System.out.println("Заказы успешно импортированы из: " + filePath);
            } catch (Exception e) {
                System.out.println("Ошибка при импорте: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        menu.addMenuItem(new MenuItem("Показать доступные типы данных", () -> {
            System.out.println("\n=== Доступные типы данных для импорта/экспорта ===");
            System.out.println(controller.getAvailableEntityTypes());
        }));

        return menu;
    }

    private Menu createPropertiesMenu() {
        Menu menu = new Menu("Настройки");

        menu.addMenuItem(new MenuItem("Показать текущие настройки", () -> {
            System.out.println("\n=== Текущие настройки ===");
            System.out.println(" - Месяцев для залежавшихся книг: " + config.getMonthsForOldBook());
            System.out.println(" - Автовыполнение запросов: " +
                    (config.isAutoCompleteRequests() ? "Включено" : "Отключено"));
            System.out.println(" - Разделитель CSV: " + config.getCsvDelimiter());
        }));

        menu.addMenuItem(new MenuItem("Изменить период для залежавшихся книг", () -> {
            System.out.println("\n=== Изменение периода для залежавшихся книг ===");
            System.out.print("Текущее значение: " + config.getMonthsForOldBook() + " месяцев");
            System.out.print("\nВведите новое количество месяцев: ");

            try {
                int months = scanner.nextInt();
                scanner.nextLine();

                if (months > 0) {
                    config.setMonthsForOldBook(months);
                    System.out.println("Настройка сохранена! Новое значение: " + months + " месяцев");
                } else {
                    System.out.println("Значение должно быть больше 0!");
                }
            } catch (Exception e) {
                System.out.println("Ошибка! Введите целое число.");
                scanner.nextLine();
            }
        }));

        menu.addMenuItem(new MenuItem("Включить/выключить автовыполнение запросов", () -> {
            System.out.println("\n=== Управление автовыполнением запросов ===");
            boolean current = config.isAutoCompleteRequests();
            System.out.println("Текущее состояние: " + (current ? "Включено" : "Отключено"));
            System.out.print("Включить автовыполнение? (да/нет): ");

            String answer = scanner.nextLine().toLowerCase();
            boolean newValue;

            if (answer.equals("да") || answer.equals("д") || answer.equals("yes") || answer.equals("y")) {
                newValue = true;
            } else if (answer.equals("нет") || answer.equals("н") || answer.equals("no") || answer.equals("n")) {
                newValue = false;
            } else {
                System.out.println("Неверный ответ. Настройка не изменена.");
                return;
            }

            config.setAutoCompleteRequests(newValue);
            System.out.println("Настройка сохранена! Автовыполнение запросов: " +
                    (newValue ? "Включено" : "Отключено"));
        }));
        return menu;
    }

    public Menu getRootMenu() {
        buildMenu();
        return rootMenu;
    }
}