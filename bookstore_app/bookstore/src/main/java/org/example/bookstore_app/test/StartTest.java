package org.example.bookstore_app.test;

import org.example.bookstore_app.view.ApplicationInitializer;
import org.example.bookstore_app.view.MenuBuilder;
import org.example.bookstore_app.view.MenuController;
import org.example.bookstore_app.view.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class StartTest {
    private static final Logger logger = LoggerFactory.getLogger(StartTest.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Необработанное исключение в потоке " + thread.getName() + ":");
            throwable.printStackTrace();
        });

        try {
            System.out.println("=== Запуск книжного магазина ===");

            // Инициализация Spring контекста
            ApplicationContext springContext = ApplicationInitializer.initialize();

            // Получение бинов из Spring контекста
            MenuBuilder menuBuilder = springContext.getBean(MenuBuilder.class);
            if (menuBuilder == null) {
                logger.error("ОШИБКА: MenuBuilder не создан!");
                return;
            }

            logger.debug("Создаем Navigator...");
            Navigator navigator = menuBuilder.createNavigator();
            if (navigator == null) {
                logger.error("ОШИБКА: Navigator не создан!");
                return;
            }

            logger.debug("Создаем MenuController...");
            MenuController menuController = springContext.getBean(MenuController.class);
            if (menuController == null) {
                logger.error("ОШИБКА: MenuController не найден в DI!");
                return;
            }

            // Устанавливаем Navigator в MenuController
            menuController.setNavigator(navigator);
            logger.debug("Navigator установлен в MenuController");

            logger.debug("Запускаем меню...");
            menuController.run();

        } catch (Exception e) {
            logger.error("Ошибка при запуске приложения: {}", e.getMessage(), e);
        }
    }
}