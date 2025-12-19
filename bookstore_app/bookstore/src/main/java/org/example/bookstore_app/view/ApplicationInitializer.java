package org.example.bookstore_app.view;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.model.Stok;

public class ApplicationInitializer {

    public static ApplicationContext initialize() {
        System.out.println("Инициализация приложения...");
        ApplicationContext context = ApplicationContext.getInstance();

        BookstoreConfig config = loadConfiguration();
        context.registerBean(BookstoreConfig.class, config);

        DataSave dataSave = DataSave.getInstance();
        context.registerBean(DataSave.class, dataSave);

        Stok stok = dataSave.loadState();
        context.registerBean(Stok.class, stok);

        context.initialize();

        OperationController controller = context.getBean(OperationController.class);
        if (controller != null) {
            controller.initializeTestData();
        } else {
            System.err.println("Ошибка: не удалось получить OperationController");
        }

        System.out.println("Инициализация завершена");
        context.printRegisteredBeans();

        return context;
    }

    private static BookstoreConfig loadConfiguration() {
        try {
            return ConfigurationLoader.loadConfiguration(BookstoreConfig.class);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            System.err.println("Используются значения по умолчанию");
            return new BookstoreConfig();
        }
    }
}