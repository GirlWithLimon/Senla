package bookstore_app.project.view;

import bookstore_app.config.ApplicationContext;
import bookstore_app.config.BookstoreConfig;
import bookstore_app.project.controller.OperationController;

public class ConsoleUIFactory implements IUIFactory {
    private final ApplicationContext context;

    public ConsoleUIFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public MenuBuilder createMenuBuilder() {
        return new MenuBuilder(
                context.getBean(BookstoreConfig.class),
                context.getBean(OperationController.class)
        );
    }

    @Override
    public Navigator createNavigator(Menu rootMenu) {
        return new Navigator(rootMenu);
    }

    @Override
    public MenuController createMenuController(MenuBuilder menuBuilder) {
        return new MenuController(menuBuilder);
    }
}