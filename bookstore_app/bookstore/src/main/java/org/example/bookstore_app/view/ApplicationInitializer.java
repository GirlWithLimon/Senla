package org.example.bookstore_app.view;

import org.example.annotation.Inject;
import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.config.BookstoreConfig;
import org.example.bookstore_app.config.ConfigurationLoader;
import org.example.bookstore_app.controller.DataSave;
import org.example.bookstore_app.controller.OperationController;
import org.example.bookstore_app.model.Stok;

public class ApplicationInitializer {
    private  BookstoreConfig config;
    @Inject
    public ApplicationInitializer(BookstoreConfig config) {
        this.config = config;
    }

    public static ApplicationContext initialize() {
        System.out.println("Инициализация приложения...");
        ApplicationContext context = ApplicationContext.getInstance();

        BookstoreConfig config = loadConfiguration();
        context.registerBean(BookstoreConfig.class, config);
        System.out.println("Конфигурация зарегистрирована");

        DataSave dataSave = DataSave.getInstance();
        context.registerBean(DataSave.class, dataSave);
        System.out.println("DataSave зарегистрирован");


        //возвращение сохраненного состояния
        Stok stok = dataSave.loadState();
        context.registerBean(Stok.class, stok);
        System.out.println("Stok загружен, размер книг: " + stok.getBooks().size());

        context.initialize();

       OperationController controller = context.getBean(OperationController.class);
        if (controller != null) {
            System.out.println("OperationController получен, инициализируем тестовые данные");
            controller.loadDate();
        } else {
            System.err.println("Ошибка: не удалось получить OperationController");
            context.printRegisteredBeans();
        }

        System.out.println("Инициализация завершена");
        return context;
    }

    private static BookstoreConfig loadConfiguration() {

        if(config.isUseBD())
        try {
            return ConfigurationLoader.loadConfiguration(BookstoreConfig.class);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            System.err.println("Используются значения по умолчанию");
            return new BookstoreConfig();
        }
    }
}