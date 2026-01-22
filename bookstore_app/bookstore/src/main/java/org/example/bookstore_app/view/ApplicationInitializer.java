package org.example.bookstore_app.view;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.dao.DBConnect;
import org.example.bookstore_app.dao.StokService;

import java.sql.Connection;

public class ApplicationInitializer {
    // Удалите @Inject конструктор

    public static ApplicationContext initialize() throws Exception {
        System.out.println("Инициализация приложения...");
        ApplicationContext context = ApplicationContext.getInstance();

        // Инициализируем конфигурацию через DI
        BookstoreConfig config = context.getBean(BookstoreConfig.class);
        if (config == null) {
            System.out.println("Создаем BookstoreConfig через ConfigurationLoader...");
            config = ConfigurationLoader.loadConfiguration(BookstoreConfig.class);
            context.registerBean(BookstoreConfig.class, config);
        }

        DataSave dataSave = DataSave.getInstance();
        context.registerBean(DataSave.class, dataSave);
        System.out.println("DataSave зарегистрирован");

        Connection conn = DBConnect.getInstance().getConnection();
        StokService stokService = dataSave.loadDate(conn);
        context.registerBean(StokService.class, stokService);
        System.out.println("Stok загружен, размер книг: " + stokService.getBooks().size());

        context.initialize();

        OperationController controller = context.getBean(OperationController.class);
        if (controller != null) {
            System.out.println("OperationController получен");
            // Не инициализируем тестовые данные, если уже есть данные из БД
            if (stokService.getBooks().isEmpty()) {
                System.out.println("Инициализируем тестовые данные");
                controller.initializeTestData();
            } else {
                System.out.println("Используем данные из БД, тестовые данные не нужны");
            }
        }

        System.out.println("Инициализация завершена");
        return context;
    }
}