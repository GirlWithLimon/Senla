package bookstore_app.project.view;

import bookstore_app.config.ApplicationContext;
import bookstore_app.config.BookstoreConfig;
import bookstore_app.config.ConfigurationLoader;
import bookstore_app.project.controller.*;
import bookstore_app.project.model.Stok;

public class ApplicationInitializer {
    private static ApplicationContext context;

    public static ApplicationContext initialize() {
        System.out.println("Инициализация приложения...");
        context = ApplicationContext.getInstance();

        // Загружаем конфигурацию
        BookstoreConfig config = loadConfiguration();
        context.registerBean(BookstoreConfig.class, config);

        // Создаем и регистрируем DataSave
        DataSave dataSave = DataSave.getInstance();
        context.registerBean(DataSave.class, dataSave);

        // Загружаем состояние
        Stok stok = dataSave.loadState();
        context.registerBean(Stok.class, stok);

        // Инициализируем контекст (регистрируем компоненты и внедряем зависимости)
        context.initialize();

        // Инициализируем тестовые данные через OperationController
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