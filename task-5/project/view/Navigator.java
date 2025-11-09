package first.view;

public class Navigator {
    private Menu currentMenu;
    
    public Navigator(Menu rootMenu) {
        this.currentMenu = rootMenu;
    }
    
    public void printMenu() {
        currentMenu.display();
    }
    
    public void navigate(int index) {
        if (index == 0) {
            if (currentMenu.getName().equals("Главное меню")) {
                System.out.println("Выход из программы...");
                System.exit(0);
            } else {
                currentMenu = MenuBuilder.getInstance().getRootMenu();
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