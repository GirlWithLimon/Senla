package org.example.bookstore_app.view;

public interface IUIFactory {
    MenuBuilder createMenuBuilder();
    Navigator createNavigator(Menu rootMenu);
    MenuController createMenuController(MenuBuilder menuBuilder);
}