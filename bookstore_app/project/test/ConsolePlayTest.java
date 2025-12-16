package bookstore_app.project.test;

import bookstore_app.project.view.*;
import bookstore_app.config.ApplicationContext;
public class ConsolePlayTest {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("НЕОБРАБОТАННОЕ ИСКЛЮЧЕНИЕ в потоке " + thread.getName() + ":");
            throwable.printStackTrace();
        });

        try {
            System.out.println("=== Запуск книжного магазина ===");

            // 1. Инициализируем и сразу получаем контекст
            ApplicationContext context = ApplicationInitializer.initialize();

            // 2. Создаем фабрику UI
            ConsoleUIFactory factory = new ConsoleUIFactory(context);

            // 3. Создаем все компоненты
            MenuBuilder menuBuilder = factory.createMenuBuilder();
            MenuController menuController = factory.createMenuController(menuBuilder);

            // 4. Запускаем
            menuController.run();

        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }

}