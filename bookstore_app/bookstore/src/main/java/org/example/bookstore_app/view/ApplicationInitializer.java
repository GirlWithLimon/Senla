package org.example.bookstore_app.view;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.dao.DBConnect;
import org.example.bookstore_app.dao.StokService;

import java.sql.Connection;

public class ApplicationInitializer {

    public static ApplicationContext initialize() throws Exception {
        System.out.println("=== Инициализация приложения ===");

        // 1. Инициализируем DI контейнер
        ApplicationContext context = ApplicationContext.getInstance();
        context.initialize();

        System.out.println("\n=== Проверка всех компонентов ===");

        // 2. Получаем все необходимые компоненты
        BookstoreConfig config = getComponentWithFallback(context, BookstoreConfig.class, "BookstoreConfig");
        DBConnect dbConnect = getComponentWithFallback(context, DBConnect.class, "DBConnect");
        StokService stokService = getComponentWithFallback(context, StokService.class, "StokService");
        DataSave dataSave = getComponentWithFallback(context, DataSave.class, "DataSave");
        OperationController controller = getComponentWithFallback(context, OperationController.class, "OperationController");

        // 3. Проверяем, есть ли данные в StokService
        System.out.println("\n=== Проверка состояния данных ===");
        boolean hasData = false;
        if (stokService != null) {
            int bookCount = stokService.getBooks().size();
            int copyCount = stokService.getBooksCopy().size();
            int orderCount = stokService.getOrders().size();

            System.out.println("Книг в каталоге: " + bookCount);
            System.out.println("Экземпляров на складе: " + copyCount);
            System.out.println("Заказов: " + orderCount);

            hasData = bookCount > 0 || copyCount > 0 || orderCount > 0;
        }

        // 4. Пробуем загрузить данные из БД (если данных нет или есть подключение)
        boolean dbLoaded = false;
        if (dbConnect != null && dataSave != null) {
            try {
                System.out.println("\n=== Попытка подключения к базе данных ===");
                Connection conn = dbConnect.getConnection();
                if (conn != null && !conn.isClosed()) {
                    System.out.println("✓ Подключение к БД установлено успешно!");
                        System.out.println("Загружаем данные из БД...");
                        dataSave.loadDate(conn);
                        dbLoaded = true;


                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("⚠ Не удалось подключиться к БД: " + e.getMessage());
                System.out.println("Причина: " + e.getClass().getSimpleName());

                // Выводим больше информации об ошибке для отладки
                if (e.getCause() != null) {
                    System.out.println("Корневая причина: " + e.getCause().getMessage());
                }
            }
        } else {
            System.out.println("⚠ DBConnect или DataSave не доступны для подключения к БД");
        }

        // 5. Если данных нет ни из БД, ни в StokService, инициализируем тестовые данные
        if (!hasData && !dbLoaded && controller != null) {
            System.out.println("\n=== Инициализация тестовых данных ===");
            try {
                controller.initializeTestData();
                System.out.println("✓ Тестовые данные инициализированы");
            } catch (Exception e) {
                System.err.println("✗ Ошибка при инициализации тестовых данных: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 6. Финальная проверка
        System.out.println("\n=== Финальная проверка системы ===");
        if (stokService != null) {
            System.out.println("1. StokService: ✓ (" + stokService.getBooks().size() + " книг)");
        } else {
            System.err.println("1. StokService: ✗ (НЕ НАЙДЕН!)");
        }

        if (controller != null) {
            System.out.println("2. OperationController: ✓");
        } else {
            System.err.println("2. OperationController: ✗");
        }

        if (config != null) {
            System.out.println("3. BookstoreConfig: ✓");
        } else {
            System.err.println("3. BookstoreConfig: ✗");
        }

        if (dbConnect != null) {
            System.out.println("4. DBConnect: ✓");
        } else {
            System.err.println("4. DBConnect: ✗");
        }

        System.out.println("\n=== Инициализация завершена! ===");
        return context;
    }

    private static <T> T getComponentWithFallback(ApplicationContext context, Class<T> type, String name) {
        try {
            T component = context.getBean(type);
            if (component != null) {
                System.out.println("✓ " + name + " загружен через DI");
                return component;
            } else {
                System.err.println("✗ " + name + " не найден в DI контейнере");
                return null;
            }
        } catch (Exception e) {
            System.err.println("✗ Ошибка при получении " + name + ": " + e.getMessage());
            return null;
        }
    }
}