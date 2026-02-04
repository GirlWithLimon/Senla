package org.example.bookstore_app.view;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.dao.DBConfig;
import org.example.bookstore_app.dao.DBConnect;
import org.example.bookstore_app.dao.StockService;


public class ApplicationInitializer {

    public static ApplicationContext initialize() {
        System.out.println("Инициализация приложения...");
        ApplicationContext context = ApplicationContext.getInstance();

        BookstoreConfig config = loadConfiguration(BookstoreConfig.class);
        context.registerBean(BookstoreConfig.class, config);
        System.out.println("Конфигурация зарегистрирована");
        DBConfig dbConfig = loadConfiguration(DBConfig.class);
        context.registerBean(DBConfig.class, dbConfig);
        System.out.println("Конфигурация зарегистрирована");

        DBConnect conn =  DBConnect.getInstance();
        context.registerBean(DBConnect.class, conn);
        System.out.println("DBConnect загружен");

        StockService stock = StockService.getInstance();
        context.registerBean(StockService.class, stock);
        System.out.println("Stok загружен");

        DataSave dataSave = DataSave.getInstance();
        context.registerBean(DataSave.class, dataSave);
        System.out.println("DataSave зарегистрирован");



        context.initialize();

        OperationController controller = context.getBean(OperationController.class);
        if (controller != null) {
            System.out.println("OperationController получен, инициализируем тестовые данные");
            controller.initializeTestData();
        } else {
            System.err.println("Ошибка: не удалось получить OperationController");
            context.printRegisteredBeans();
        }

        System.out.println("Инициализация завершена");
        return context;
    }

    private static <T> T loadConfiguration(Class <T> configClass) {
        try {
            return ConfigurationLoader.loadConfiguration(configClass);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            System.err.println("Используются значения по умолчанию");
            return null;
        }
    }
}