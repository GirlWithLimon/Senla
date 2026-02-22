package org.example.bookstore_app.view;

import org.springframework.stereotype.Component;

@Component
public class ConsoleUIFactory implements IUIFactory {

    @Override
    public Navigator createNavigator(final Menu rootMenu) {
        return new Navigator(rootMenu);
    }
}
