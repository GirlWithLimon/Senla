package org.example.bookstore_app.test;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.view.ApplicationInitializer;
import org.example.bookstore_app.view.MenuBuilder;
import org.example.bookstore_app.view.MenuController;
import org.example.bookstore_app.view.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartTest {
    private static final Logger logger = LoggerFactory.getLogger(StartTest.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Необработанное исключение в потоке " + thread.getName() + ":");
            throwable.printStackTrace();
        });

        try {
            System.out.println("=== Запуск книжного магазина ===");

            ApplicationContext context = ApplicationInitializer.initialize();

            MenuBuilder menuBuilder = context.getBean(MenuBuilder.class);
            if (menuBuilder == null) {
                logger.error("ОШИБКА: MenuBuilder не создан!");
                context.printRegisteredBeans();
                return;
            }

            logger.debug("Создаем Navigator...");
            Navigator navigator = menuBuilder.createNavigator();
            if (navigator == null) {
                logger.error("ОШИБКА: Navigator не создан!");
                return;
            }

            logger.debug("Создаем MenuController...");
            MenuController menuController = context.getBean(MenuController.class);
            if (menuController == null) {
                logger.error("ОШИБКА: MenuController не найден в DI!");
                return;
            }

            try {
                java.lang.reflect.Method setNavigatorMethod =
                 MenuController.class.getMethod("setNavigator", Navigator.class);
                setNavigatorMethod.invoke(menuController, navigator);
                logger.debug("Navigator установлен в MenuController через сеттер");
            } catch (NoSuchMethodException e) {
                logger.debug("Метод setNavigator не найден, используем конструктор");
                menuController = new MenuController(navigator);
                context.registerBean(MenuController.class, menuController);
            }

            logger.debug("Запускаем меню...");
            menuController.run();
        } catch (Exception e) {
            logger.error("Ошибка при запуске приложения: {}", e.getMessage());
        }
    }
}
