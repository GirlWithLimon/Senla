package project;

import project.view.ConsoleUIFactory;
import project.view.MenuController;
import project.view.IUIFactory;

public class ConsolePlayTest {
    public static void main(String[] args) {
        IUIFactory factory = ConsoleUIFactory.getInstance();
        MenuController controller = factory.createMenuController();
        controller.run();
    }
}