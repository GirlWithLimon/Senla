package bookstore_app.project.test;

import bookstore_app.project.view.ConsoleUIFactory;
import bookstore_app.project.view.MenuController;
import bookstore_app.project.view.IUIFactory;

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