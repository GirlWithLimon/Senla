package org.example.bookstore_app.view;

import org.example.annotation.Component;
import org.example.annotation.Inject;

@Component
public class Navigator {
    private Menu currentMenu;
    private Menu rootMenu;

    @Inject
    public Navigator() { }

    public Navigator(Menu rootMenu) {
        this.rootMenu = rootMenu;
        this.currentMenu = rootMenu;
    }

    public void setRootMenu(Menu rootMenu) {
        this.rootMenu = rootMenu;
        if (this.currentMenu == null) {
            this.currentMenu = rootMenu;
        }
    }

    public void printMenu() {
        if (currentMenu == null) {
            System.err.println("ОШИБКА: currentMenu не установлен!");
            System.err.println("rootMenu = " + rootMenu);
            if (rootMenu != null) {
                currentMenu = rootMenu;
                System.out.println("Используем rootMenu как currentMenu");
            } else {
                System.out.println("Создаем временное меню...");
                Menu tempMenu = new Menu("Главное меню");
                tempMenu.addMenuItem(new MenuItem("Выйти", () -> {
                    System.out.println("Выход из программы...");
                    System.exit(0);
                }));
                currentMenu = tempMenu;
            }
        }
        currentMenu.display();
    }

    public void navigate(int index) {
        if (index == 0) {
            if (currentMenu.getName().equals("Главное меню")) {
                System.out.println("Выход из программы...");
                System.exit(0);
            } else {
                currentMenu = rootMenu;
            }
            return;
        }

        if (index < 1 || index > currentMenu.getMenuItems().size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        MenuItem selectedItem = currentMenu.getMenuItems().get(index - 1);

        if (selectedItem.hasSubMenu()) {
            currentMenu = selectedItem.getSubMenu();
        } else {
            selectedItem.doAction();
        }
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }
}