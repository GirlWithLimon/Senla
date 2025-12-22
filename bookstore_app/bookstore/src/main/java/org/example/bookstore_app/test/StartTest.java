package org.example.bookstore_app.test;

import org.example.bookstore_app.config.ApplicationContext;
import org.example.bookstore_app.view.ApplicationInitializer;
import org.example.bookstore_app.view.MenuBuilder;
import org.example.bookstore_app.view.MenuController;
import org.example.bookstore_app.view.Navigator;

public class StartTest {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Необработанное исключение в потоке " + thread.getName() + ":");
            throwable.printStackTrace();
        });

        try {
            System.out.println("=== Запуск книжного магазина ===");

            ApplicationContext context = ApplicationInitializer.initialize();

            MenuBuilder menuBuilder = context.getBean(MenuBuilder.class);
            if (menuBuilder == null) {
                System.err.println("ОШИБКА: MenuBuilder не создан!");
                context.printRegisteredBeans();
                return;
            }

            System.out.println("Создаем Navigator...");
            Navigator navigator = menuBuilder.createNavigator();
            if (navigator == null) {
                System.err.println("ОШИБКА: Navigator не создан!");
                return;
            }

            System.out.println("Создаем MenuController...");
            MenuController menuController = context.getBean(MenuController.class);
            if (menuController == null) {
                System.err.println("ОШИБКА: MenuController не найден в DI!");
                return;
            }

            try {
                java.lang.reflect.Method setNavigatorMethod =
                        MenuController.class.getMethod("setNavigator", Navigator.class);
                setNavigatorMethod.invoke(menuController, navigator);
                System.out.println("Navigator установлен в MenuController через сеттер");
            } catch (NoSuchMethodException e) {
                System.out.println("Метод setNavigator не найден, используем конструктор");
                menuController = new MenuController(navigator);
                context.registerBean(MenuController.class, menuController);
            }

            System.out.println("Запускаем меню...");
            menuController.run();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}