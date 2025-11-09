package first;

import first.view.ConsoleUIFactory;
import first.view.MenuController;
import first.view.IUIFactory;

public class ConsolePlayTest {
    public static void main(String[] args) {
        IUIFactory factory = ConsoleUIFactory.getInstance();
        MenuController controller = factory.createMenuController();
        controller.run();
    }
}