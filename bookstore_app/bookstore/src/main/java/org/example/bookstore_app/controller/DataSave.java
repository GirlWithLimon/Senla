package org.example.bookstore_app.controller;

import org.example.annotation.Component;
import org.example.annotation.Inject;
import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.dao.*;
import org.example.bookstore_app.model.Book;
import org.example.bookstore_app.model.BookCopy;
import org.example.bookstore_app.dao.StockService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

@Component
public class DataSave {
    @Inject
    DBConnect dbConnect;

    @Inject
    StockService stockService;

    @Inject
    BookDAO bookDAO;

    @Inject
    BookCopyDAO bookCopyDAO;

    @Inject
    BookOrderDAO bookOrderDAO;

    @Inject
    BookOrderItemDAO bookOrderItemDAO;

    @Inject
    RequestDAO requestDAO;

    private static final Logger logger = Logger.getLogger(DataSave.class.getName());
    private static final String DATA_FILE = "bookstore_data.out";
    private static DataSave instance;

    public DataSave() {}

    public boolean initialize() {
        System.out.println("Выполняем подключение к базе данных");
        try (Connection conn = dbConnect.getConnection()) {
            if (!tablesExist(conn)) {
                System.out.println("БД пуста");
                //   createTables(conn);
                //   insertInitialData(conn);
                return false;
            } else {
                System.out.println("Выполнено подключение к базе данных");
                loadDate(conn);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static DataSave getInstance() {
        if (instance == null) {
            instance = new DataSave();
        }
        return instance;
    }

    public void saveState(StockService stockService, Connection conn) throws Exception {
        if(tablesExist(conn)) {
            // Сохраняем состояние в БД
            System.out.println("Сохраняем состояние в базу данных...");

            // Сохраняем книги
            for (Book book : stockService.getBooks()) {
                stockService.addBook(book);
            }

            // Сохраняем копии книг
            for (BookCopy copy : stockService.getBooksCopy()) {
               stockService.addBooksCopy(copy);
            }

            // Сохраняем заказы
            for (var order : stockService.getOrders()) {
                stockService.addOrder(order);
            }

            // Сохраняем элементы заказов
            for (var orderItem : bookOrderItemDAO.findAll()) {
                stockService.addBookOrderItem(orderItem);
            }

            // Сохраняем запросы
            for (var request : stockService.getRequests()) {
                stockService.addRequest(request);
            }

            System.out.println("Состояние сохранено в БД");
        }
    }

    public StockService loadDate(Connection conn) {
        try {
            System.out.println("Начинаем загрузку данных из БД...");

            initializeDAOs();

            // Используем существующий StokService из DI или создаем новый
            StockService loadedStockService;
            if (stockService != null) {
                loadedStockService = stockService;
                System.out.println("Используем существующий StokService из DI");
            } else {
                loadedStockService = new StockService();
                System.out.println("Создан новый StokService");
            }


            // Проверяем, что StokService корректно инициализирован
            if (stockService == null) {
                // Регистрируем загруженный StokService в DI контейнере
                ApplicationContext context = ApplicationContext.getInstance();
                context.registerBean(StockService.class, loadedStockService);
                this.stockService = loadedStockService;
            }
            // Загружаем книги
            List<Book> books = stockService.getBooks();
            System.out.println("Найдено книг в БД: " + books.size());
            for (Book book : books) {
                System.out.println("Загружаем книгу: " + book.getName() + " (ID: " + book.getId() + ")");
                loadedStockService.addBook(book);

                // Загружаем копии этой книги
                List<BookCopy> copies = bookCopyDAO.findByBookId(book.getId());
                if (copies != null && !copies.isEmpty()) {
                    for (BookCopy copy : copies) {
                        loadedStockService.addBooksCopy(copy);
                        book.setStatusStok();
                        System.out.println("  - Загружена копия ID: " + copy.getId());
                    }
                }
            }

            // Загружаем заказы
            List<?> orders = stockService.getOrders();
            System.out.println("Найдено заказов в БД: " + orders.size());

            // Загружаем подзаказы
            List<?> orderItems = stockService.getBookOrderItem();
            System.out.println("Найдено подзаказов в БД: " + orders.size());

            // Загружаем запросы
            List<?> requests = stockService.getRequests();
            System.out.println("Найдено запросов в БД: " + requests.size());

            System.out.println("Загрузка данных из БД завершена успешно");
            System.out.println("Всего загружено: " + books.size() + " книг, " +
                    orders.size() + " заказов, " + requests.size() + " запросов");

            return loadedStockService;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из БД: " + e.getMessage());
            e.printStackTrace();

            // Возвращаем пустой StokService в случае ошибки
            return new StockService();
        }
    }

    private void initializeDAOs() {
        System.out.println("Инициализация DAO...");

        ApplicationContext context = ApplicationContext.getInstance();

        try {
            // Проверяем и получаем DAO из контекста
            if (bookDAO == null) {
                bookDAO = context.getBean(BookDAO.class);
                if (bookDAO == null) {
                    System.err.println("BookDAO не найден в DI контейнере");
                    bookDAO = new BookDAO(dbConnect);
                    injectDBConnect(bookDAO);
                    context.registerBean(BookDAO.class, bookDAO);
                }
                System.out.println("BookDAO инициализирован: " + (bookDAO != null));
            }

            if (bookCopyDAO == null) {
                bookCopyDAO = context.getBean(BookCopyDAO.class);
                if (bookCopyDAO == null) {
                    System.err.println("BookCopyDAO не найден в DI контейнере");
                    bookCopyDAO = new BookCopyDAO(dbConnect);
                    injectDBConnect(bookCopyDAO);
                    context.registerBean(BookCopyDAO.class, bookCopyDAO);
                }
                System.out.println("BookCopyDAO инициализирован: " + (bookCopyDAO != null));
            }

            if (bookOrderDAO == null) {
                bookOrderDAO = context.getBean(BookOrderDAO.class);
                if (bookOrderDAO == null) {
                    System.err.println("BookOrderDAO не найден в DI контейнере");
                    bookOrderDAO = new BookOrderDAO(dbConnect);
                    injectDBConnect(bookOrderDAO);
                    context.registerBean(BookOrderDAO.class, bookOrderDAO);
                }
                System.out.println("BookOrderDAO инициализирован: " + (bookOrderDAO != null));
            }

            if (bookOrderItemDAO == null) {
                bookOrderItemDAO = context.getBean(BookOrderItemDAO.class);
                if (bookOrderItemDAO == null) {
                    System.err.println("BookOrderItemDAO не найден в DI контейнере");
                    bookOrderItemDAO = new BookOrderItemDAO(dbConnect);
                    injectDBConnect(bookOrderItemDAO);
                    context.registerBean(BookOrderItemDAO.class, bookOrderItemDAO);
                }
                System.out.println("BookOrderItemDAO инициализирован: " + (bookOrderItemDAO != null));
            }

            if (requestDAO == null) {
                requestDAO = context.getBean(RequestDAO.class);
                if (requestDAO == null) {
                    System.err.println("RequestDAO не найден в DI контейнере");
                    requestDAO = new RequestDAO(dbConnect);
                    injectDBConnect(requestDAO);
                    context.registerBean(RequestDAO.class, requestDAO);
                }
                System.out.println("RequestDAO инициализирован: " + (requestDAO != null));
            }

        } catch (Exception e) {
            System.err.println("Ошибка при инициализации DAO: " + e.getMessage());
        }
    }

    private void injectDBConnect(Object dao) {
        try {
            DBConnect dbConnect = ApplicationContext.getInstance().getBean(DBConnect.class);
            if (dbConnect != null) {
                Field connectField = dao.getClass().getDeclaredField("connect");
                connectField.setAccessible(true);
                connectField.set(dao, dbConnect);
                System.out.println("DBConnect внедрен в " + dao.getClass().getSimpleName());
            } else {
                System.err.println("DBConnect не найден для внедрения в " + dao.getClass().getSimpleName());
            }
        } catch (NoSuchFieldException e) {
            System.err.println("Поле 'connect' не найдено в " + dao.getClass().getSimpleName());
        } catch (Exception e) {
            System.err.println("Ошибка при внедрении DBConnect в " + dao.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private boolean tablesExist(Connection conn) throws SQLException {
        String checkSql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = 'book')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next()) {
                boolean exists = rs.getBoolean(1);
                System.out.println("Таблица 'book' существует: " + exists);
                return exists;
            }
            return false;
        }
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public StockService getStokService() {
        return stockService;
    }

    public void setStokService(StockService stockService) {
        this.stockService = stockService;
    }
}