package bookstore_app;

import bookstore_app.view.ConsoleUIFactory;
import bookstore_app.view.MenuController;
import bookstore_app.view.IUIFactory;

public class ConsolePlayTest {
    public static void main(String[] args) {
        IUIFactory factory = ConsoleUIFactory.getInstance();
        MenuController controller = factory.createMenuController();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nПрограмма завершена.");
        }));
        controller.run();
    }
}