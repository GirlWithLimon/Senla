package project.view;

public interface IUIFactory {
    MenuBuilder createMenuBuilder();
    Navigator createNavigator(Menu rootMenu);
    MenuController createMenuController();
}