package bookstore_app.view;

public class ConsoleUIFactory implements IUIFactory {
    private static ConsoleUIFactory instance;
    
    private ConsoleUIFactory() {}
    
    public static ConsoleUIFactory getInstance() {
        if (instance == null) {
            instance = new ConsoleUIFactory();
        }
        return instance;
    }
    
    @Override
    public MenuBuilder createMenuBuilder() {
        return MenuBuilder.getInstance();
    }
    
    @Override
    public Navigator createNavigator(Menu rootMenu) {
        return new Navigator(rootMenu);
    }
    
    @Override
    public MenuController createMenuController() {
        return new MenuController();
    }
}