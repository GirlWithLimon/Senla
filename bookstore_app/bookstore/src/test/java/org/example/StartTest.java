package org.example;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.view.ApplicationInitializer;
import org.example.bookstore_app.view.ConsoleUIFactory;
import org.example.bookstore_app.view.MenuBuilder;
import org.example.bookstore_app.view.MenuController;

public class StartTest {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("необработанное исключение в потоке " + thread.getName() + ":");
            throwable.printStackTrace();
        });

        try {
            System.out.println("=== Запуск книжного магазина ===");

            ApplicationContext context = ApplicationInitializer.initialize();
            ConsoleUIFactory factory = new ConsoleUIFactory(context);

            MenuBuilder menuBuilder = factory.createMenuBuilder();
            MenuController menuController = factory.createMenuController(menuBuilder);

            menuController.run();

        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
