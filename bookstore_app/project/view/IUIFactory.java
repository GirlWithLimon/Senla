package bookstore_app.project.view;

public interface IUIFactory {
    MenuBuilder createMenuBuilder();
    Navigator createNavigator(Menu rootMenu);
    MenuController createMenuController(MenuBuilder menuBuilder);
}