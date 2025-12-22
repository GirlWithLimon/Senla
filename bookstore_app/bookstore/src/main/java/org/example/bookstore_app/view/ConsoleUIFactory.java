package org.example.bookstore_app.view;

import org.example.annotation.Inject;
import org.example.annotation.Component;

@Component
public class ConsoleUIFactory implements IUIFactory {

    @Override
    public Navigator createNavigator(Menu rootMenu) {
        return new Navigator(rootMenu);
    }
}